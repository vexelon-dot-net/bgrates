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
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

import com.google.common.collect.Lists;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.vexelon.bgrates.AppSettings;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.ui.UIFlags;
import net.vexelon.bgrates.utils.NumberUtils;

public class ConvertTargetListAdapter extends ArrayAdapter<CurrencyData> {

	private List<CurrencyData> items;
	private List<BigDecimal> values;
	private boolean showValues = false;
	private int precisionMode = AppSettings.PRECISION_SIMPLE;

	public ConvertTargetListAdapter(Context context, int textViewResId, List<CurrencyData> items, boolean showValues, int precisionMode) {
		super(context, textViewResId, items);
		this.items = items;
		this.values = Lists.newArrayList();
		for (int i = 0; i < items.size(); i++) {
			values.add(BigDecimal.ZERO);
		}
		this.showValues = showValues;
		this.precisionMode = precisionMode;
	}

	private View _getView(int position, View convertView) {
		View v = convertView;
		if (v == null) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.convert_target_row_layout, null);
		}
		CurrencyData currencyData = items.get(position);
		ImageView icon = (ImageView) v.findViewById(R.id.icon);
		int imageId = UIFlags.getResourceFromCode(currencyData.getCode());
		if (imageId != -1) {
			icon.setImageResource(imageId);
		}
		setResText(v, R.id.name, currencyData.getName());
		setResText(v, R.id.code, currencyData.getCode());
		if (showValues) {
			BigDecimal value = values.get(position);
			if (value == null) {
				value = BigDecimal.ZERO;
			}
			switch (precisionMode) {
				case AppSettings.PRECISION_ADVANCED:
					String rate = NumberUtils.scaleCurrency(value, Defs.SCALE_SHOW_LONG);
					setResText(v, R.id.rate, rate);
//					setResText(v, R.id.rate, rate.substring(0, rate.length() - 3));
					//setResText(v, R.id.rate_decimals, rate.substring(rate.length() - 3));
					break;
				case AppSettings.PRECISION_SIMPLE:
				default:
					setResText(v, R.id.rate, NumberUtils.scaleCurrency(value, currencyData.getCode()));
					break;
			}
		}
		return v;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return _getView(position, convertView);
	}

	@Override
	public void remove(CurrencyData object) {
		items.remove(object);
	}

	public CurrencyData remove(int position) {
		return items.remove(position);
	}

	public void updateValues(CurrencyData sourceCurrency, BigDecimal value) {
		MathContext mathContext = new MathContext(Defs.SCALE_CALCULATIONS, RoundingMode.HALF_EVEN);
		// convert source currency to BGN value
		BigDecimal valueBGN;
		try {
			BigDecimal rate = new BigDecimal(sourceCurrency.getRate(), mathContext);
			BigDecimal ratio = new BigDecimal(sourceCurrency.getRatio(), mathContext);
			valueBGN = value.multiply(rate.divide(ratio, mathContext), mathContext);
		} catch (Exception e) {
			Log.e(Defs.LOG_TAG, "Failed to convert source currency to BGN!", e);
			return;
		}
		// convert each destination currency from BGN
		for (int i = 0; i < items.size(); i++) {
			CurrencyData currency = items.get(i);
			BigDecimal result = BigDecimal.ZERO;
			try {
				BigDecimal reverseRate;
				if ("0".equals(currency.getReverseRate())) {
					BigDecimal ratio0 = new BigDecimal(currency.getRatio());
					reverseRate = ratio0.divide(new BigDecimal(currency.getRate(), mathContext), mathContext);
				} else {
					reverseRate = new BigDecimal(currency.getReverseRate(), mathContext);
				}
				BigDecimal ratio = new BigDecimal(currency.getRatio(), mathContext);
				result = valueBGN.multiply(reverseRate, mathContext);
				// result = reverseRate.multiply(ratio,
				// mathContext).multiply(valueBGN, mathContext);
			} catch (Exception e) {
				Log.e(Defs.LOG_TAG, "Failed to calculate currency " + currency.getCode() + "!", e);
			}
			values.set(i, result);
		}
	}

	private void setResText(View v, int id, CharSequence text) {
		TextView tx = (TextView) v.findViewById(id);
		if (tx != null) {
			tx.setText(text);
		}
	}

}
