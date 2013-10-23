package ro.ieval.cosciclete;

import java.net.HttpURLConnection;
import android.net.Uri;
import java.net.URL;
import android.os.AsyncTask;
import java.nio.ByteBuffer;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.content.Context;

final class NetworkCall{
	static interface Callback{
		void handleResponse(final byte[] response);
	}

	private static final String baseUrl = "http://site.valexandru.biz/json";

	public static void call(final Context context, final String endpoint, final byte[] postData, final Callback callback, final String... params){
		new AsyncTask<Void, Void, ByteBuffer>(){
			protected ByteBuffer doInBackground(Void... ignored){
				try {
					final Uri.Builder builder =  Uri.parse("http://site.valexandru.biz/json" + endpoint + ".php").buildUpon();
					for(int i=0;i<params.length;i+=2)
						builder.appendQueryParameter(params[i], params[i+1]);
					final HttpURLConnection conn = (HttpURLConnection) new URL(builder.build().toString()).openConnection();
					final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

					conn.setRequestProperty("Authorization", "Basic "+Base64.encodeToString((sp.getString("username", "")+':'+sp.getString("password", "")).getBytes(), Base64.NO_WRAP));

					conn.connect();
					final int rc = conn.getResponseCode();
					if(rc == 401){
						MainActivity.unauthorized.show();
						return null;
					}
					final byte[] data = new byte[1024 * 1024 * 4];
					conn.getInputStream().read(data);
					return ByteBuffer.wrap(data);
				} catch (Exception ex){
					ex.printStackTrace();
					return null;
				}
			}

			protected void onPostExecute(ByteBuffer result){
				if(callback!=null && result != null)
					callback.handleResponse(result.array());
			}
		}.execute();
	}
}
