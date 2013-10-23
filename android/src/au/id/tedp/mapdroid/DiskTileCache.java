package au.id.tedp.mapdroid;

import android.content.Context;
import java.io.File;
import android.graphics.BitmapFactory;
import java.io.OutputStream;
import java.io.FileOutputStream;
import android.graphics.Bitmap;

public class DiskTileCache implements TileCache {
	private final Context context;

	DiskTileCache(final Context context){
		this.context = context;
	}

	public void add(final Tile tile){
		final String path = context.getCacheDir().getPath() + "/tiles/" + tile.getZoom() + "/" + tile.getXTileNumber() + "/" + tile.getYTileNumber() + ".png";
		new File(context.getCacheDir().getPath() + "/tiles/" + tile.getZoom() + "/" + tile.getXTileNumber()).mkdirs();

		try {
			final OutputStream os = new FileOutputStream(path);
			if(tile.getBitmap() != null)
				tile.getBitmap().compress(Bitmap.CompressFormat.PNG, 0, os);
			os.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	public Tile getTile(int zoom, int x, int y){
		final String path = context.getCacheDir().getPath() + "/tiles/" + zoom + "/" + x + "/" + y + ".png";
		if(!new File(path).exists())
			return null;
		return new Tile(BitmapFactory.decodeFile(path), zoom, x, y);
	}
}
