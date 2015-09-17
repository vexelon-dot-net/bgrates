/*
 * MyGlob Android Application
 * 
 * Copyright (C) 2010 Petar Petrov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.vexelon.bgrates;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSettings {

	private SharedPreferences generalPrefs = null;

	public AppSettings(Context context) {
		generalPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	/**
	 * Gets currencies sorting
	 * 
	 * @return
	 * 		<ul>
	 *         <li>-1 (Default)
	 *         <li>0 (Name)
	 *         <li>1 (Code)
	 */
	public int getCurrenciesSortSelection() {
		return generalPrefs.getInt("pref_currencies_sortby", -1);
	}

	/**
	 * 
	 * @param value
	 */
	public void setCurrenciesSortSelection(int value) {
		generalPrefs.edit().putInt("pref_currencies_sortby", value).commit();
	}

	/**
	 * Gets currencies language selection
	 * 
	 * @return
	 * 		<ul>
	 *         <li>0 (Default)
	 *         <li>1 (Bulgarian)
	 *         <li>2 (English)
	 */
	public int getCurrenciesLanguage() {
		return generalPrefs.getInt("pref_currencies_language", 0);
	}

}
