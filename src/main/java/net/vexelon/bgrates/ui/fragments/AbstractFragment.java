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

import android.app.Fragment;
import android.os.Bundle;
import net.vexelon.bgrates.ui.Notifications;
import net.vexelon.bgrates.ui.NotificationsListener;

public class AbstractFragment extends Fragment {

	protected List<NotificationsListener> listeners = new ArrayList<NotificationsListener>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	// @Override
	// public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	// // default menu
	// inflater.inflate(R.menu.main, menu);
	// super.onCreateOptionsMenu(menu, inflater);
	// }

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

}
