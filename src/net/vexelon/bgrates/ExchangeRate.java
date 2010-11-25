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
package net.vexelon.bgrates;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import net.vexelon.bgrates.CurrencyInfo.Tendency;

import android.os.Parcel;
import android.os.Parcelable;

public class ExchangeRate implements Parcelable {
	
	private final static int DEFAULT_SIZE = 30;
	
	private List<CurrencyInfo> _currencies = null;
	private ArrayList<String> _currencyCodesCache = null;
	private HeaderInfo _header = null;
	private static final Hashtable<String, Integer> _flagIds = new Hashtable<String, Integer>(10);
	
	static {
		_flagIds.put("au", R.drawable.au);
		_flagIds.put("bg", R.drawable.bg);
		_flagIds.put("br", R.drawable.br);
		_flagIds.put("ca", R.drawable.ca);
		_flagIds.put("ch", R.drawable.ch);
		_flagIds.put("ca", R.drawable.ca);
		_flagIds.put("cn", R.drawable.cn);
		_flagIds.put("cz", R.drawable.cz);
		_flagIds.put("dk", R.drawable.dk);
		_flagIds.put("ee", R.drawable.ee);
		_flagIds.put("gb", R.drawable.gb);
		_flagIds.put("hk", R.drawable.hk);
		_flagIds.put("hr", R.drawable.hr);
		_flagIds.put("hu", R.drawable.hu);
		_flagIds.put("id", R.drawable.id);
		_flagIds.put("in", R.drawable.in);
		_flagIds.put("is", R.drawable.is);
		_flagIds.put("jp", R.drawable.jp);
		_flagIds.put("kr", R.drawable.kr);
		_flagIds.put("lt", R.drawable.lt);
		_flagIds.put("lv", R.drawable.lv);
		_flagIds.put("mx", R.drawable.mx);
		_flagIds.put("my", R.drawable.my);
		_flagIds.put("no", R.drawable.no);
		_flagIds.put("nz", R.drawable.nz);
		_flagIds.put("ph", R.drawable.ph);
		_flagIds.put("pl", R.drawable.pl);
		_flagIds.put("ro", R.drawable.ro);
		_flagIds.put("ru", R.drawable.ru);
		_flagIds.put("se", R.drawable.se);
		_flagIds.put("sg", R.drawable.sg);
		_flagIds.put("th", R.drawable.th);
		_flagIds.put("tr", R.drawable.tr);
		_flagIds.put("us", R.drawable.us);
		_flagIds.put("xa", R.drawable.xa);
		_flagIds.put("za", R.drawable.za);
	}
	
	public ExchangeRate() {
		_currencies =  new ArrayList<CurrencyInfo>(DEFAULT_SIZE);
		_currencyCodesCache = new ArrayList<String>(DEFAULT_SIZE);
	}
	
//	public ExchangeRate(HeaderInfo header, CurrencyInfo[] currencies ) {
//		_header = header;
//		_currencies = new ArrayList<CurrencyInfo>(currencies.length);
//		for (CurrencyInfo currencyInfo : currencies) {
//			_currencies.add(currencyInfo);
//		}
//	}
	
	public String getTimeStamp() {
		// still looks like hack :(
		
		String result = this.getHeader().getTitle().substring(
				this.getHeader().getTitle().length() - 10,
				this.getHeader().getTitle().length()
				);
		return result;
	}
	
	public void evaluateTendencies(ExchangeRate olderRates) {
		for(CurrencyInfo currency : _currencies) {
			CurrencyInfo oldCurrency = olderRates.getCurrencyByCode(currency.getCode());
			if ( oldCurrency != null ) {
				BigDecimal rate = new BigDecimal(currency.getRate());
				BigDecimal ratio = new BigDecimal(currency.getRatio());
				BigDecimal oldRate = new BigDecimal(oldCurrency.getRate());
				BigDecimal oldRatio = new BigDecimal(oldCurrency.getRatio());
				
				rate = rate.multiply(ratio);
				oldRate = oldRate.multiply(oldRatio);
				
				int res = rate.compareTo(oldRate);
				if ( res < 0 ) {
					currency.setTendency(Tendency.TendencyDown);
				}
				else if ( res > 0 ) {
					currency.setTendency(Tendency.TendencyUp);
				}
				else {
					currency.setTendency(Tendency.TendencyEqual);
				}
			}
		}
	}
	
	public static int getResourceFromCode(CurrencyInfo ci) {
		return _flagIds.get(ci.getCountryCode()) != null ?
				_flagIds.get(ci.getCountryCode()) : R.drawable.money;
	}
	
	public static int getResourceFromCode(String code) {
		String smallCode = code.substring(0, 2).toLowerCase();
		return _flagIds.get(smallCode) != null ?
				_flagIds.get(smallCode) : R.drawable.money;
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
		String[] results = new String[ _currencyCodesCache.size() ];
		_currencyCodesCache.toArray(results);
		return results;
	}
	
	public CurrencyInfo getCurrencyByCode(String code) {
		for (CurrencyInfo ci : _currencies) {
			if ( ci.getCode().equalsIgnoreCase(code) )
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
	
	//// Parcelable implementation ////
	
	public ExchangeRate(Parcel in) {
		_header = in.readParcelable(HeaderInfo.class.getClassLoader());
		
		_currencies =  new ArrayList<CurrencyInfo>(DEFAULT_SIZE);
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
	
	public static final Parcelable.Creator<ExchangeRate> CREATOR = new Creator<ExchangeRate>() {
		
		@Override
		public ExchangeRate[] newArray(int size) {
			return new ExchangeRate[size];
		}
		
		@Override
		public ExchangeRate createFromParcel(Parcel source) {
			return new ExchangeRate(source);
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
