package ro.ieval.cosciclete;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;

public final class Loc implements Serializable{
	public final String name;
	public final String group;
	public final double lon;
	public final double lat;
	public final int bikes;
	public final String description;
	public final String email;
	public final String phone;
	public final Bitmap photo;

	public Loc(final String name, final String group, final double lon, final double lat, final int bikes, final String description, final String email, final String phone){
		this(name, group, lon, lat, bikes, description, email, phone, null);
	}

	public Loc(final String name, final String group, final double lon, final double lat, final int bikes, final String description, final String email, final String phone, final Bitmap photo){
		this.name = name;
		this.group = group;
		this.lon = lon;
		this.lat = lat;
		this.bikes = bikes;
		this.description = description;
		this.email = email;
		this.phone = phone;
		this.photo = photo;
	}

	public static Loc fromJSONObject(final JSONObject object){
		try {
/*			final String photo = object.optString("photo");
			final Bitmap bitmap;
			if(photo == null || photo.equals(""))
				bitmap = null;
			else {
				System.out.println("Loading bitmap from url \"" + photo + '"');
				bitmap = BitmapFactory.decodeStream(new URL(photo).openConnection().getInputStream());
				}*/
				
			return new Loc(object.getString("name"), object.getString("group"), object.getDouble("lon"), object.getDouble("lat"), object.optInt("bikes"), object.optString("description"), object.optString("email"), object.optString("phone"));
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
