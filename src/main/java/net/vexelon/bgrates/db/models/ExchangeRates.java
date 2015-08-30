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
package net.vexelon.bgrates.db.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.CurrencyInfo.Tendency;

@Deprecated
public class ExchangeRates implements Parcelable {

	private final static int DEFAULT_SIZE = 30;

	private List<CurrencyInfo> _currencies = null;
	private ArrayList<String> _currencyCodesCache = null;
	private HeaderInfo _header = null;

	public ExchangeRates() {
		_currencies = new ArrayList<CurrencyInfo>(DEFAULT_SIZE);
		_currencyCodesCache = new ArrayList<String>(DEFAULT_SIZE);
	}

	// public ExchangeRate(HeaderInfo header, CurrencyInfo[] currencies ) {
	// _header = header;
	// _currencies = new ArrayList<CurrencyInfo>(currencies.length);
	// for (CurrencyInfo currencyInfo : currencies) {
	// _currencies.add(currencyInfo);
	// }
	// }

	public String getTimeStamp() {
		// still looks like hack :(

		String result = this.getHeader().getTitle().substring(this.getHeader().getTitle().length() - 10,
				this.getHeader().getTitle().length());
		return result;
	}

	public void evaluateTendencies(ExchangeRates olderRates) {
		for (CurrencyInfo currency : _currencies) {
			CurrencyInfo oldCurrency = olderRates.getCurrencyByCode(currency.getCode());
			if (oldCurrency != null) {
				BigDecimal rate = new BigDecimal(currency.getRate());
				BigDecimal ratio = new BigDecimal(currency.getRatio());
				BigDecimal oldRate = new BigDecimal(oldCurrency.getRate());
				BigDecimal oldRatio = new BigDecimal(oldCurrency.getRatio());

				rate = rate.multiply(ratio);
				oldRate = oldRate.multiply(oldRatio);

				int res = rate.compareTo(oldRate);
				if (res < 0) {
					currency.setTendency(Tendency.TendencyDown);
				} else if (res > 0) {
					currency.setTendency(Tendency.TendencyUp);
				} else {
					currency.setTendency(Tendency.TendencyEqual);
				}
			}
		}
	}

	public static int getResourceFromTendency(Tendency tendency) {
		switch (tendency) {
		case TendencyUp:
			return R.drawable.arrow_up;
		case TendencyDown:
			return R.drawable.arrow_down;
		case TendencyEqual:
		case TendencyUnknown:
		default:
			break;
		}

		return 0; // 0 sets image resouce to nothing
	}

	public void add(CurrencyInfo currency) {
		_currencies.add(currency);
		_currencyCodesCache.add(currency.getCode());
	}

	public void addHeader(HeaderInfo header) {
		_header = header;
	}

	public HeaderInfo getHeader() {
		return _header;
	}

	public List<CurrencyInfo> getItems() {
		return _currencies;
	}

	public boolean isHeaderAvailable() {
		return (_header != null);
	}

	public void sort() {
		Collections.sort(_currencies, new Comparator<CurrencyInfo>() {
			@Override
			public int compare(CurrencyInfo object1, CurrencyInfo object2) {
				return object1.getCode().compareTo(object2.getCode());
			}
		});
	}

	public CurrencyInfo[] getCurrencies() {
		CurrencyInfo[] currencies = new CurrencyInfo[_currencies.size()];
		_currencies.toArray(currencies);
		return currencies;
	}

	public String[] currenciesToStringArray() {
		String[] results = new String[_currencyCodesCache.size()];
		_currencyCodesCache.toArray(results);
		return results;
	}

	public CurrencyInfo getCurrencyByCode(String code) {
		for (CurrencyInfo ci : _currencies) {
			if (ci.getCode().equalsIgnoreCase(code))
				return ci;
		}
		return null;
	}

	public int count() {
		return _currencies.size();
	}

	public void clear() {
		_currencies.clear();
	}

	// // Parcelable implementation ////

	public ExchangeRates(Parcel in) {
		_header = in.readParcelable(HeaderInfo.class.getClassLoader());

		_currencies = new ArrayList<CurrencyInfo>(DEFAULT_SIZE);
		in.readTypedList(this._currencies, new Parcelable.Creator<CurrencyInfo>() {
			@Override
			public CurrencyInfo createFromParcel(Parcel source) {
				return new CurrencyInfo(source);
			}

			@Override
			public CurrencyInfo[] newArray(int size) {
				return new CurrencyInfo[size];
			}
		});

		_currencyCodesCache = new ArrayList<String>(DEFAULT_SIZE);
		in.readList(_currencyCodesCache, null);
	}

	public static final Parcelable.Creator<ExchangeRates> CREATOR = new Creator<ExchangeRates>() {

		@Override
		public ExchangeRates[] newArray(int size) {
			return new ExchangeRates[size];
		}

		@Override
		public ExchangeRates createFromParcel(Parcel source) {
			return new ExchangeRates(source);
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(_header, flags);
		dest.writeTypedList(_currencies);
		dest.writeList(_currencyCodesCache);
	}

}
