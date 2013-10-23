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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import java.lang.Float;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import ro.ieval.cosciclete.Loc;
import ro.ieval.cosciclete.ShowActivity;
import java.util.Map;
import android.graphics.LightingColorFilter;
import ro.ieval.cosciclete.EditActivity;

public class OSMMapView extends View implements GpsStatus.Listener, LocationListener {
    private TileServer tileServer;
    private int tileSize;
    public static volatile int zoom = 17;
    // These are floats on purpose so we can derive the center *pixel*
    // from the fractional tile number
    private float centerTileX, centerTileY;
    private float mLat, mLong;
    private MotionHandler motionHandler;
    private GestureDetector gestureDetector;
    private LinkedHashMap<TileIdentifier, Tile> visibleTiles;
	private HashSet<TileIdentifier> tilesBeingDownloaded;
    private MapViewHandler handler;
    public Messenger messenger;

	private Loc[] markers;
	private Point[] markerCenters;

	private Bitmap greenbike, redbike, yellowbike;

    public OSMMapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        visibleTiles = new LinkedHashMap<TileIdentifier, Tile>(400, 0.7f, true) {
			@Override
			protected boolean removeEldestEntry(final Map.Entry<TileIdentifier, Tile> ignored){
				return size() > 400;
			}
		};
		tilesBeingDownloaded = new HashSet<TileIdentifier>(400);
        tileServer = new TileServer(context);
        tileSize = tileServer.getTileSize();

        motionHandler = new MotionHandler(this);
        gestureDetector = new GestureDetector(motionHandler);
        gestureDetector.setIsLongpressEnabled(false);
        handler = new MapViewHandler();
        messenger = new Messenger(handler);
		greenbike = BitmapFactory.decodeResource(context.getResources(), ro.ieval.cosciclete.R.drawable.greenbike);
		redbike = BitmapFactory.decodeResource(context.getResources(), ro.ieval.cosciclete.R.drawable.redbike);
		yellowbike = BitmapFactory.decodeResource(context.getResources(), ro.ieval.cosciclete.R.drawable.yellowbike);
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int newzoom) {
        if (newzoom < 0 || newzoom > tileServer.getMaxZoom() || newzoom == zoom)
            return;

        zoom = newzoom;
        recalculateCenterPixel();
		visibleTiles.clear();
		tilesBeingDownloaded.clear();
		tileServer.resetExecutor();
        getVisibleTiles();
    }

    public float getLatitude() {
        return mLat;
    }

    public float getLongitude() {
        return mLong;
    }

	public void setMarkers(final Loc[] markers){
		this.markers = markers;
		if(markers == null)
			this.markerCenters = null;
		else
			this.markerCenters = new Point[markers.length];
		invalidate();
	}

    class MotionHandler extends GestureDetector.SimpleOnGestureListener {
        OSMMapView owner;

        public MotionHandler(OSMMapView owner) {
            super();
            this.owner = owner;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY)
        {
            owner.onMove(velocityX, velocityY);
            return true;
        }

		public boolean onSingleTapConfirmed(MotionEvent e){
			if(markers != null && markerCenters != null)
				for(int i=0;i<markers.length;i++){
					final Point center = markerCenters[i];
					if(center != null && Math.abs(e.getX() - center.x) <= getMarkerRadius() && Math.abs(e.getY() - center.y) <= getMarkerRadius()){
						final Intent intent = new Intent(getContext(), ShowActivity.class);
						intent.putExtra(ShowActivity.EXTRA_MARKER, markers[i]);
						getContext().startActivity(intent);
						return true;
					}
				}
			return false;
		}

		public boolean onDoubleTap(final MotionEvent e){
			final float lon = mLong + TileSet.getLongitude(zoom, e.getX());
			final float lat = mLat + TileSet.getLatitude(zoom, e.getY());

			final Intent intent = new Intent(getContext(), EditActivity.class);
			intent.putExtra(EditActivity.EXTRA_LON, lon);
			intent.putExtra(EditActivity.EXTRA_LAT, lat);
			getContext().startActivity(intent);
			return true;
		}
    }

    // XXX: Pretty sure this math is bogus
    public int getLeftTileNumber() {
        return ((int) centerTileX) - getWidth() / 2 / tileSize - 1;
    }

    public int getRightTileNumber() {
        return ((int) centerTileX) + getWidth() / 2 / tileSize + 1;
    }

    public int getTopTileNumber() {
        return ((int) centerTileY) - getHeight() / 2 / tileSize - 1;
    }

    public int getBottomTileNumber() {
        return ((int) centerTileY) + getHeight() / 2 / tileSize + 1;
    }

    // XXX: OK for this to be non-static? I guess.
    public class MapViewHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.d("Mapdroid", String.format("Received message: %s", msg.toString()));

            if (msg.what == TileDownloader.RESULT_OK) {
				final Tile tile = (Tile) msg.obj;
				final TileIdentifier ident = new TileIdentifier(tile.getZoom(), tile.getXTileNumber(), tile.getYTileNumber());

                visibleTiles.put(ident, tile);
				tilesBeingDownloaded.remove(ident);
                invalidate();
            }

            // Causes error.
            //msg.recycle();
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Just be whatever size we're told is available.
        // It might be nicer to adhere to the SuggestedMinimum dimensions.
        setMeasuredDimension(
                View.MeasureSpec.getSize(widthMeasureSpec),
                View.MeasureSpec.getSize(heightMeasureSpec));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);

        return true;
    }

    public void recalculateCenterPixel() {
        centerTileX = TileSet.getXTileNumberAsFloat(zoom, mLong);
        centerTileY = TileSet.getYTileNumberAsFloat(zoom, mLat);

        Log.d("Mapdroid", String.format("recalculated X pixel %f from longitude %f, Y pixel %f from latitude %f",
                    centerTileX, mLong, centerTileY, mLat));
    }

    public void recalculateCoords() {
        mLat = TileSet.getLatitude(zoom, centerTileY);
        mLong = TileSet.getLongitude(zoom, centerTileX);

        Log.d("Mapdroid", String.format("recalculated latitude %f from %fpx, longitude %f from %fpx",
                    mLat, centerTileY, mLong, centerTileX));
    }

    public void onMove(float pixelsX, float pixelsY) {
        // FIXME: (1) Don't allow scrolling so far off the  that it disappears
        // FIXME: (2) Wrap the map to the left/right so it is continuous
        Log.d("Mapdroid", String.format("onMove called,X: %fpx, Y: %fpx", pixelsX, pixelsY));
        centerTileX += pixelsX / tileServer.getTileSize();
        centerTileY += pixelsY / tileServer.getTileSize();
        recalculateCoords();

        Log.d("Mapdroid", String.format("Left tile number: %d, right tile number: %d",
                    getLeftTileNumber(), getRightTileNumber()));
        Log.d("Mapdroid", String.format("Top tile number: %d, bottom tile number: %d",
                    getTopTileNumber(), getBottomTileNumber()));

        getVisibleTiles();
    }

    public void setCenterPixels(float pixelX, float pixelY) {
        Log.d("Mapdroid", "setCenterPixels called (currently does nothing)");
    }

    public void setCenterCoords(float lat, float lon) {
        Log.d("Mapdroid", String.format("setCenterCoords: %f %f", lon, lat));
        mLat = lat;
        mLong = lon;
        recalculateCenterPixel();
        getVisibleTiles();
    }

	private void requestTile(final int zoom, final int x, final int y, final Messenger messenger){
		final TileIdentifier ident = new TileIdentifier(zoom, x, y);
		if(visibleTiles.get(ident) == null && !tilesBeingDownloaded.contains(ident)){
			tilesBeingDownloaded.add(ident);
			tileServer.requestTile(zoom, x, y, messenger);
		}
	}

	private void getVisibleTiles() {
        int centerx, centery, x, y;

        centerx = TileSet.getXTileNumber(zoom, mLong);
        centery = TileSet.getYTileNumber(zoom, mLat);

        // Center tile is most important; grab it first
        requestTile(zoom, centerx, centery, messenger);

        for (x = getLeftTileNumber() - 1; x <= getRightTileNumber() + 1; ++x) {
            for (y = getTopTileNumber() - 1; y <= getBottomTileNumber() + 1; ++y) {
                // Don't re-request the center tile
                if (x != centerx || y != centery)
                    requestTile(zoom, x, y, messenger);
            }
        }

        for (x = getLeftTileNumber() - 5; x <= getRightTileNumber() + 5; ++x)
            for (y = getTopTileNumber() - 5; y <= getBottomTileNumber() + 5; ++y)
				if(x < getLeftTileNumber() - 1 || x > getRightTileNumber() + 1 || y < getTopTileNumber() - 1 || y > getBottomTileNumber() + 1)
					requestTile(zoom, x, y, messenger);

		invalidate();
    }

    // XXX: Only recalculate this when setCenter() is called, or the Canvas size changes
    // X Coordinate on the Canvas where (0,0) (top left) of the Center tile should be drawn
    protected Point getCenterTileOrigin(Canvas c) {
        Rect clipBounds = c.getClipBounds();
        int x, y;
        // Center of visible canvas:
        x = (clipBounds.right - clipBounds.left) / 2 + clipBounds.left;
        y = (clipBounds.bottom - clipBounds.top) / 2 + clipBounds.top;
        x -= TileSet.getXPixel(zoom, mLong, tileServer.getTileSize());
        y -= TileSet.getYPixel(zoom, mLat, tileServer.getTileSize());
        return new Point(x, y);
    }

    /**
      Returns true if the tile was drawn on the canvas, else false.
FIXME: use exceptions, dummy
      */
    protected boolean drawTileOnCanvas(Tile tile, Canvas canvas) {
        if (tile.getZoom() != zoom) {
            // Probably an old request. Don't draw it.
            Log.d("Mapdroid",
                    String.format("Ignoring tile with zoom level %d, current zoom level is %d",
                        tile.getZoom(), zoom));
            return false;
        }

        Bitmap bmp = tile.getBitmap();

        if (bmp == null) {
            Log.d("Mapdroid", "Ignoring Tile without a Bitmap");
            return false;
        }

        Point centerTileOrigin = getCenterTileOrigin(canvas);

        int thisTileOriginX = centerTileOrigin.x +
            (tile.getXTileNumber() - TileSet.getXTileNumber(zoom, mLong)) * tileSize;

        int thisTileOriginY = centerTileOrigin.y +
            (tile.getYTileNumber() - TileSet.getYTileNumber(zoom, mLat)) * tileSize;

        canvas.drawBitmap(bmp, null,
                new Rect(thisTileOriginX, thisTileOriginY,
                    thisTileOriginX + tileSize, thisTileOriginY + tileSize), null);

        return true;
    }

	private int getMarkerRadius(){
		return (1 << (zoom - 16)) * 10;
	}

	private float getXOfMarker(final Canvas canvas, final Loc marker){
		Point p = getCenterTileOrigin(canvas);
		return getCenterTileOrigin(canvas).x +
			(TileSet.getXTileNumberAsFloat(zoom, (float)marker.lon) - TileSet.getXTileNumber(zoom, mLong)) * tileSize;
	}

	private float getYOfMarker(final Canvas canvas, final Loc marker){
		return getCenterTileOrigin(canvas).y +
			(TileSet.getYTileNumberAsFloat(zoom, (float)marker.lat) - TileSet.getYTileNumber(zoom, mLat)) * tileSize;
	}

	private Bitmap getBitmapByBikes(final int bikes){
		//		if(bikes == -1)
		//			return Color.GRAY;
		if(bikes == 0)
			return redbike;
		if(bikes <= 5)
			return yellowbike;
		return greenbike;
	}

	protected void drawMarkerOnCanvas(final int i, final Loc marker, final Canvas canvas){
		final int x = (int) getXOfMarker(canvas, marker);
		final int y = (int) getYOfMarker(canvas, marker);
		markerCenters[i] = new Point(x, y);
		canvas.drawBitmap(getBitmapByBikes(marker.bikes), null, new RectF(x - getMarkerRadius(), y - getMarkerRadius(), x + getMarkerRadius(), y + getMarkerRadius()), null);
	}

    protected void drawVisibleTiles(Canvas canvas) {
        int i;

        for (Tile tile : visibleTiles.values()) {
            drawTileOnCanvas(tile, canvas);
        }

		if(markers!=null && zoom > 15)
			for (i = 0; i < markers.length; ++i) {
				drawMarkerOnCanvas(i, markers[i], canvas);
			}
    }

    public void onDraw(Canvas canvas) {
        if (visibleTiles.isEmpty()) {
            Log.w("Mapdroid", "No tiles, requesting visible tiles...");
            getVisibleTiles();
        } else {
            drawVisibleTiles(canvas);
        }
    }

    public void onGpsStatusChanged(int event) {
        Log.d("Mapdroid", String.format("GPS status changed, event: %d", event));
    }


    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        Log.d("Mapdroid", "onLocationChanged()");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Mapdroid", "onProviderDisabled()");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Mapdroid", "onProviderEnabled()");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Mapdroid", "onStatusChanged()");
    }

    public void startLocationUpdates(Context context) {
        LocationManager locmgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L, 0.0f, this);
		//commented by me
		//		locmgr.requestLocationUpdates(
		//                LocationManager.NETWORK_PROVIDER,
		//                0L, 0.0f, this);

        // XXX: Unnecessary
//        locmgr.addGpsStatusListener(this);
    }

    public void stopLocationUpdates(Context context) {
        LocationManager locmgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locmgr.removeUpdates(this);
    }
}

/* vim: set ts=4 sw=4 et :*/
