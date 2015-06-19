/*
 * The MIT License
 * 
 * Copyright (c) 2010 Petar Petrov
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
package net.vexelon.bgrates.ui.activities;

import net.vexelon.bgrates.R;
import net.vexelon.bgrates.R.id;
import net.vexelon.bgrates.R.layout;
import net.vexelon.bgrates.db.models.old.ExchangeRate;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConvertCurrencyAdapter extends ArrayAdapter<String> {
	
	private String[] _items = null;
	
	public ConvertCurrencyAdapter(Context context, int textViewResId, String[] items) {
		super(context, textViewResId, items);
		this._items = items;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if ( v == null ) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.convert_row_layout, null);
		}
		
		// set texts
		String code = _items[position];

		// country ID icon		
		ImageView icon = (ImageView) v.findViewById(R.id.IconConvert);
		int imgId = ExchangeRate.getResourceFromCode(code);
		if ( imgId != -1 ) {
			icon.setImageResource(imgId);		
		}
		
		setResText(v, R.id.TextConvertCode, code);
		
		return v;
	}
	
	private void setResText(View v, int id, CharSequence text) {
		TextView tx = (TextView)v.findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}	
	
}
