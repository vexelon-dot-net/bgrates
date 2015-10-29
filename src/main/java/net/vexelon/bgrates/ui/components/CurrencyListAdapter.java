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
package net.vexelon.bgrates.ui.components;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.ui.UIFlags;
import net.vexelon.bgrates.utils.NumberUtils;

public class CurrencyListAdapter extends ArrayAdapter<CurrencyData> {

	private List<CurrencyData> items;

	public CurrencyListAdapter(Context context, int textViewResId, List<CurrencyData> items) {
		super(context, textViewResId, items);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.currency_row_layout, null);
		}
		// set texts
		CurrencyData currencyData = items.get(position);
		if (currencyData != null) {
			setResText(v, R.id.name, currencyData.getName());
			setResText(v, R.id.code, currencyData.getCode());
			setResText(v, R.id.ratio, Integer.toString(currencyData.getRatio()));
			// rate
			BigDecimal rateDecimal = new BigDecimal(currencyData.getRate());
			String rate = NumberUtils.scaleNumber(rateDecimal, Defs.SCALE_SHOW_LONG);
			setResText(v, R.id.rate, rate.substring(0, rate.length() - 3));
			setResText(v, R.id.rate_decimals, rate.substring(rate.length() - 3));
			// country ID icon
			ImageView icon = (ImageView) v.findViewById(R.id.icon);
			int imageId = UIFlags.getResourceFromCode(currencyData.getCode());
			if (imageId != -1) {
				icon.setImageResource(imageId);
				// icon.setScaleType(ScaleType.FIT_XY);
				// icon.setAdjustViewBounds(true);
				// android.widget.RelativeLayout.LayoutParams lp = new
				// RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				// LayoutParams.WRAP_CONTENT);
				//
				// lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT |
				// RelativeLayout.CENTER_VERTICAL);
				// icon.setLayoutParams(lp);
			}
			// add tendency icon
			// ImageView tendencyIcon = (ImageView)
			// v.findViewById(R.id.tendency);
			// tendencyIcon.setImageResource(ExchangeRates.getResourceFromTendency(ci.getTendency()));
		}
		return v;
	}

	private void setResText(View v, int id, CharSequence text) {
		TextView tx = (TextView) v.findViewById(id);
		if (tx != null) {
			tx.setText(text);
		}
	}

}
