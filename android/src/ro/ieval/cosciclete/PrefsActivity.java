package ro.ieval.cosciclete;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/*
 * Copyright © 2013 Marius Gavrilescu
 * 
 * This file is part of CosCiclete.
 *
 * FonBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FonBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FonBot.  If not, see <http://www.gnu.org/licenses/>. 
 */

public final class PrefsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.prefs);
	}

}
