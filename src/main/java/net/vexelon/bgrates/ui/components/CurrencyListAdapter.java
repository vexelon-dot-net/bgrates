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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import net.vexelon.bgrates.AppSettings;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.ui.UIFlags;
import net.vexelon.bgrates.utils.NumberUtils;

public class CurrencyListAdapter extends ArrayAdapter<CurrencyData> {

	private final List<CurrencyData> itemsImmutable;
	private List<CurrencyData> items;
	private int precisionMode = AppSettings.PRECISION_SIMPLE;
	private CurrencyFilter filter = null;

	public CurrencyListAdapter(Context context, int textViewResId, List<CurrencyData> items, int precisionMode) {
		super(context, textViewResId, items);
		this.itemsImmutable = Lists.newArrayList(items.iterator());
		this.items = items;
		this.precisionMode = precisionMode;
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
			switch (precisionMode) {
			case AppSettings.PRECISION_ADVANCED:
				String rate = NumberUtils.scaleCurrency(rateDecimal, Defs.SCALE_SHOW_LONG);
				setResText(v, R.id.rate, rate.substring(0, rate.length() - 3));
				setResText(v, R.id.rate_decimals, rate.substring(rate.length() - 3));
				break;
			case AppSettings.PRECISION_SIMPLE:
			default:
				setResText(v, R.id.rate, NumberUtils.scaleCurrency(rateDecimal, Defs.BGN_CODE));
				break;
			}
			// country ID icon
			ImageView icon = (ImageView) v.findViewById(R.id.icon);
			int imageId = UIFlags.getResourceFromCode(currencyData.getCode());
			if (imageId != -1) {
				icon.setImageResource(imageId);
			}
			// add tendency icon
			// ImageView tendencyIcon = (ImageView)
			// v.findViewById(R.id.tendency);
			// tendencyIcon.setImageResource(ExchangeRates.getResourceFromTendency(ci.getTendency()));
		}
		return v;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new CurrencyFilter();
		}
		return filter;
	}

	public void sortBy(final int sortBy, final boolean sortByAscending) {
		Collections.sort(items, new Comparator<CurrencyData>() {
			@Override
			public int compare(CurrencyData lhs, CurrencyData rhs) {
				switch (sortBy) {
				case AppSettings.SORTBY_CODE:
					if (sortByAscending) {
						return lhs.getCode().compareToIgnoreCase(rhs.getCode());
					}
					return rhs.getCode().compareToIgnoreCase(lhs.getCode());
				case AppSettings.SORTBY_NAME:
				default:
					if (sortByAscending) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}
					return rhs.getName().compareToIgnoreCase(lhs.getName());
				}
			}
		});
	}

	private void setResText(View v, int id, CharSequence text) {
		TextView tx = (TextView) v.findViewById(id);
		if (tx != null) {
			tx.setText(text);
		}
	}

	private class CurrencyFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			List<CurrencyData> currenciesFiltered = Lists.newArrayList();
			int filterBy = Integer.parseInt(constraint.toString());

			switch (filterBy) {
			case AppSettings.FILTERBY_ALL:
				currenciesFiltered.addAll(itemsImmutable);
				break;
			case AppSettings.FILTERBY_NONFIXED:
				for (CurrencyData currency : itemsImmutable) {
					if (!currency.isFixed()) {
						currenciesFiltered.add(currency);
					}
				}
				break;
			case AppSettings.FILTERBY_FIXED:
				for (CurrencyData currency : itemsImmutable) {
					if (currency.isFixed()) {
						currenciesFiltered.add(currency);
					}
				}
				break;
			}

			results.values = currenciesFiltered;
			results.count = currenciesFiltered.size();
			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count > 0) {
				items = (List<CurrencyData>) results.values;
			}
		}
	}
}
