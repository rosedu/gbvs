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

public class RoutePoint extends Object {
    private String desc;

    public String getDescription() {
        return desc;
    }

    public void setDescription(String newDesc) {
        desc = newDesc;
    }

    public String toString() {
        return desc;
    }
}
