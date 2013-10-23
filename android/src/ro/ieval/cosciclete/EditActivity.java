package ro.ieval.cosciclete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.location.Location;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.graphics.Bitmap;
import android.net.Uri;
import android.graphics.BitmapFactory;
import au.id.tedp.mapdroid.OSMMapView;

public class EditActivity extends Activity
{
	public static final String EXTRA_MARKER = "marker";
	public static final String EXTRA_LON = "lon";
	public static final String EXTRA_LAT = "lat";
	private EditText name, group, lon, lat, bikes, description, email, phone;
	private ImageButton picker;
	private Bitmap selectedBitmap;

	private void populateFields(final Loc marker){
		name.setText(marker.name);
		group.setText(marker.group);
		lon.setText(Double.toString(marker.lon));
		lat.setText(Double.toString(marker.lat));
		bikes.setText(Integer.toString(marker.bikes));
		description.setText(marker.description);
		email.setText(marker.email);
		phone.setText(marker.phone);
		if(marker.photo != null){
			selectedBitmap = marker.photo;
			picker.setImageBitmap(selectedBitmap);
		}
	}

	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
		name = (EditText) findViewById(R.id.name);
		group = (EditText) findViewById(R.id.group);
		lon = (EditText) findViewById(R.id.lon);
		lon.setText(Float.toString(getIntent().getFloatExtra(EXTRA_LON, 0)));
		lat = (EditText) findViewById(R.id.lat);
		lat.setText(Float.toString(getIntent().getFloatExtra(EXTRA_LAT, 0)));
		bikes = (EditText) findViewById(R.id.bikes);
		description = (EditText) findViewById(R.id.description);
		email = (EditText) findViewById(R.id.email);
		phone = (EditText) findViewById(R.id.phone);
		picker = (ImageButton) findViewById(R.id.picker);

		final Loc marker = (Loc) getIntent().getSerializableExtra(EXTRA_MARKER);
		if(marker == null)
			setTitle("Add point");
		else {
			setTitle("Edit " + marker.name);
			populateFields(marker);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(final Menu menu){
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item){
		if(item == null)
			return super.onOptionsItemSelected(null);

		switch(item.getItemId()){
		case R.id.saveItem:
			NetworkCall.call(this, "/delpoint", null, null, "name", name.getText().toString());
			NetworkCall.call(this, "/addpoint", null, null,
							 "name", name.getText().toString(),
							 "group", group.getText().toString(),
							 "lon", lon.getText().toString(),
							 "lat", lat.getText().toString(),
							 "bikes", bikes.getText().toString(),
							 "description", description.getText().toString(),
							 "email", email.getText().toString(),
							 "phone", phone.getText().toString());
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void pickImage(final View ignored){
		final Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(final int requestcode, final int resultcode, final Intent intent){
		super.onActivityResult(requestcode, resultcode, intent);

		switch(requestcode){
		case 0:
			if(resultcode == RESULT_OK){
				try {
					final Uri imageUri = intent.getData();
					final Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
					selectedBitmap = image;
					picker.setImageBitmap(image);
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
}
