/*
 * This file is part of Mapdroid.
 * Copyright 2009 Ted Percival <ted@tedp.id.au>.
 *
 * Mapdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Mapdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mapdroid. If not, see <http://www.gnu.org/licenses/>.
 */
package au.id.tedp.mapdroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TileServer extends Thread {
    private Handler mHandler; // XXX: Example code makes this public
    private TileSet tileset;
	private ExecutorService executor;
	private final DiskTileCache dtc;

	public void resetExecutor(){
		executor.shutdownNow();
		executor = Executors.newFixedThreadPool(4);
	}

	public void run() {
        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj == null)
                    return;

                // XXX: Do stuff.
            }
        };

        Looper.loop();
    }

    public TileServer(final Context context) {
		dtc = new DiskTileCache(context);
        tileset = new TileSet();
        tileset.addServer("a.tiles.mapbox.com");
		executor = Executors.newFixedThreadPool(4);
    }

    public int getMaxZoom() {
        return 19; /* umm... */
    }

    public void requestTile(int zoom, float latitude, float longitude, Messenger notify) {
        requestTile(
                zoom,
                TileSet.getXTileNumber(zoom, longitude),
                TileSet.getYTileNumber(zoom, latitude),
                notify);
    }

    protected boolean provideTileFromCache(int zoom, int x, int y, Messenger notify) {
        Tile tile = dtc.getTile(zoom, x, y);

        if (tile == null)
            return false;

        Log.d("Mapdroid", String.format("Requesting download of tile %d,%d", x, y));

        Message response = TileDownloader.buildNotification(tile, true);
        try {
            notify.send(response);
        }
        catch (android.os.RemoteException e) {
            Log.e("Mapdroid", e.toString());
            return false;
        }
        return true;
    }

    public void requestTile(int zoom, int x, int y, Messenger notify) {
        if (provideTileFromCache(zoom, x, y, notify))
            return;

        Tile tile = new Tile(
                tileset.getUriForTile(zoom, x, y),
                zoom, x, y);

        TileDownloader downloader = new TileDownloader(tile, notify, dtc);
		executor.execute(downloader);
    }

    public int getTileSize() {
        return tileset.getTileSize();
    }
}

/* vim: set ts=4 sw=4 et :*/
