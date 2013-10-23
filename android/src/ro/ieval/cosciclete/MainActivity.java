package ro.ieval.cosciclete;

import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.location.Location;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import java.io.IOException;

import au.id.tedp.mapdroid.OSMMapView;

public class MainActivity extends Activity{
	private final class Callback implements NetworkCall.Callback{
		public void handleResponse(final byte[] data){
			try{
				final FileOutputStream os = new FileOutputStream(markersCachePath);
				os.write(data);
				os.close();
				final JSONArray array = new JSONArray(new String(data));
				final Loc[] markers = new Loc[array.length()];
				for(int i=0;i<markers.length;i++)
					markers[i] = Loc.fromJSONObject(array.getJSONObject(i));

				map.setMarkers(markers);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public static Toast unauthorized;
	public String markersCachePath;
	private OSMMapView map;

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		unauthorized = Toast.makeText(this, "ERROR: You cannot perform this operation. Change the username/password combination in the preferences", Toast.LENGTH_LONG);
		markersCachePath = this.getCacheDir() + "/markers.json";

		map = (OSMMapView) findViewById(R.id.mapView);
		map.setCenterCoords(44.43201000f, 26.10397f);
		//		map.setCenterCoords(7.14031900f, 9.12345000f);

		
		if(new File(markersCachePath).exists()){
			final byte[] data = new byte[(int)(new File(markersCachePath).length() + 5)];
			try {
				final FileInputStream is = new FileInputStream(markersCachePath);
				is.read(data);
				new Callback().handleResponse(data);
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
    }

	@Override
	protected void onStart(){
		super.onStart();
		map.startLocationUpdates(this);
		drawMarkers();
	}

	public void zoomIn(final View ignored){
		int newZoom = map.getZoom() + 1;
		if(newZoom > 15)
			drawMarkers();
		else
			map.setMarkers(null);
		map.setZoom(newZoom);
	}

	public void zoomOut(final View ignored){
		int newZoom = map.getZoom() - 1;
		if(newZoom > 15)
			drawMarkers();
		map.setZoom(newZoom);
	}

	private void drawMarkers(){
		NetworkCall.call(this, "/points", null, new Callback());
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu){
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item){
		if(item == null)
			return super.onOptionsItemSelected(null);

		switch(item.getItemId()){
		case R.id.newItem:
			startActivity(new Intent(this, EditActivity.class));
			return true;
		case R.id.prefs:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
