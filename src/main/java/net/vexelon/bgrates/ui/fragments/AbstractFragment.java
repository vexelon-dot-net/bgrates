/*
 * The MIT License
 * 
 * Copyright (c) 2015 Petar Petrov
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.vexelon.bgrates.ui.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import android.app.Fragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import net.vexelon.bgrates.AppSettings;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.db.models.CurrencyLocales;
import net.vexelon.bgrates.ui.Notifications;
import net.vexelon.bgrates.ui.NotificationsListener;

public class AbstractFragment extends Fragment {

	protected Menu mMenu;
	protected List<NotificationsListener> listeners = new ArrayList<NotificationsListener>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mMenu = menu;
		super.onCreateOptionsMenu(menu, inflater);
	}

	public void addListener(NotificationsListener listner) {
		listeners.add(listner);
	}

	public void removeListener(NotificationsListener listener) {
		listeners.remove(listener);
	}

	public void notifyListeners(Notifications notification) {
		for (NotificationsListener listener : listeners) {
			listener.onNotification(notification);
		}
	}

	protected void setRefreshActionButtonState(final boolean isRefreshing) {
		if (mMenu != null) {
			MenuItem menuItem = mMenu.findItem(R.id.action_refresh);
			if (menuItem != null) {
				if (isRefreshing) {
					menuItem.setActionView(isRefreshing ? R.layout.actionbar_indeterminate_progress : null);
				} else {
					menuItem.setActionView(null);
				}
			}
		}
	}

	protected CurrencyLocales getSelectedCurrenciesLocale() {
		return new AppSettings(getActivity()).getCurrenciesLanguage();
	}

	protected int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	protected Map<String, CurrencyData> getCurreniesMap(List<CurrencyData> currenciesList) {
		Map<String, CurrencyData> currenciesMap = Maps.newHashMap();
		for (CurrencyData currencyData : currenciesList) {
			currenciesMap.put(currencyData.getCode(), currencyData);
		}
		return currenciesMap;
	}

}
