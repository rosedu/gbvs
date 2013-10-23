package ro.ieval.cosciclete;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.location.Location;
import android.widget.TextView;
import android.widget.ImageView;

import au.id.tedp.mapdroid.OSMMapView;

public class LoginActivity extends Activity
{
	@Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

	public void login(final View ignored){

	}
}
