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

class TileIdentifier {
	private int zoom, x, y;

	public TileIdentifier(int zoom, int x, int y) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		TileIdentifier ti = (TileIdentifier) obj;

		if (ti.x == x && ti.y == y && ti.zoom == zoom)
			return true;

		return false;
	}

	public int hashCode() {
		return zoom + 100 * ((x+180) + (y+180) * 1000);
	}

	@Override
	public String toString(){
		return "zoom: " + zoom + "x: " + x + "y: " + y;
	}
}
