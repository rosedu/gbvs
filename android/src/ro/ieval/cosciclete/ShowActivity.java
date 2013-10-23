package ro.ieval.cosciclete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.location.Location;
import android.widget.TextView;
import android.widget.ImageView;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import android.net.Uri;
import android.widget.Button;
import android.content.Intent;

import au.id.tedp.mapdroid.OSMMapView;

public class ShowActivity extends Activity{
	private final class PointCallback implements NetworkCall.Callback{
		public void handleResponse(final byte[] data){
			try{
				final JSONObject object = new JSONObject(new String(data));
				marker = Loc.fromJSONObject(object);
				populateFields();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public static final String EXTRA_MARKER = "marker";
	private Loc marker;

	private void populateFields(){
		setTitle(marker.name);
 		final ImageView photo = (ImageView) findViewById(R.id.photo);
		final TextView bikes = (TextView) findViewById(R.id.bikes);
		final TextView description = (TextView) findViewById(R.id.description);

		if(marker.photo != null)
			photo.setImageBitmap(marker.photo);
		if(marker.description != null)
			description.setText(marker.description);
		if(marker.email == null)
			((Button) findViewById(R.id.emailButton)).setVisibility(View.GONE);
		if(marker.phone == null)
			((Button) findViewById(R.id.dialButton)).setVisibility(View.GONE);
		bikes.setText("Available bikes: " + marker.bikes);
	}

	@Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);
		marker = (Loc) getIntent().getSerializableExtra(EXTRA_MARKER);
		populateFields();
		NetworkCall.call(this, "/point", null, new PointCallback(), "name", marker.name);
    }

	@Override
	public boolean onCreateOptionsMenu(final Menu menu){
		getMenuInflater().inflate(R.menu.show, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item){
		if(item == null)
			return super.onOptionsItemSelected(null);

		switch(item.getItemId()){
		case R.id.editItem:
			final Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra(EditActivity.EXTRA_MARKER, marker);
			startActivity(intent);
			return true;
		case R.id.deleteItem:
			NetworkCall.call(this, "/delpoint", null, null, "name", marker.name);
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void addBike(final View ignored){
		NetworkCall.call(this, "/addbikes", null, null, "name", marker.name, "bikes", "1");
		NetworkCall.call(this, "/point", null, new PointCallback(), "name", marker.name);
    }

	public void removeBike(final View ignored){
		NetworkCall.call(this, "/addbikes", null, null, "name", marker.name, "bikes", "-1");
		NetworkCall.call(this, "/point", null, new PointCallback(), "name", marker.name);
    }

	public void email(final View ignored){
		final Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("message/rfc822");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{marker.email});
		startActivity(intent);
	}

	public void dial(final View ignored){
		final Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + marker.phone));
		startActivity(intent);
	}
}
