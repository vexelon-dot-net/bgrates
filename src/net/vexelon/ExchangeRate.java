package net.vexelon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExchangeRate {
	
	private List<CurrencyInfo> _currencies = null;
	private CurrencyInfo _header = null;
	
	public ExchangeRate() {
		_currencies =  new ArrayList<CurrencyInfo>(20);
	}
	
	public void add(CurrencyInfo currency) {
		this._currencies.add(currency);
	}
	
	public void addHeader(CurrencyInfo header) {
		this._header = header;
	}
	
	public boolean isHeaderAvailable() {
		return (this._header != null);
	}
	
	public void sort() {
		Collections.sort(_currencies, new Comparator<CurrencyInfo>() {
			@Override
			public int compare(CurrencyInfo object1, CurrencyInfo object2) {
				return object1.getCode().compareTo(object2.getCode());
			}
		});
	}
	
	public int count() {
		return _currencies.size();
	}
	
	public void clear() {
		_currencies.clear();
	}

}
