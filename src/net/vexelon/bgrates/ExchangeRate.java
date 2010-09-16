package net.vexelon.bgrates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ExchangeRate {
	
	private List<CurrencyInfo> _currencies = null;
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
		_currencies =  new ArrayList<CurrencyInfo>(20);
	}
	
	public ExchangeRate(HeaderInfo header, CurrencyInfo[] currencies ) {
		_header = header;
		_currencies = new ArrayList<CurrencyInfo>(currencies.length);
		for (CurrencyInfo currencyInfo : currencies) {
			_currencies.add(currencyInfo);
		}
	}
	
	public static int getResrouceFromCode(CurrencyInfo ci) {
		return _flagIds.get(ci.getCountryCode()) != null ?
				_flagIds.get(ci.getCountryCode()) : R.drawable.money;
	}
	
	public void add(CurrencyInfo currency) {
		this._currencies.add(currency);
	}
	
	public void addHeader(HeaderInfo header) {
		this._header = header;
	}
	
	public HeaderInfo getHeader() {
		return this._header;
	}
	
	public List<CurrencyInfo> items() {
		return this._currencies;
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
	
	public CurrencyInfo[] getCurrencies() {
		CurrencyInfo[] currencies = new CurrencyInfo[_currencies.size()];
		_currencies.toArray(currencies);
		return currencies;
	}
	
	public String[] currenciesToStringArray() {
		Vector<String> elements = new Vector<String>(_currencies.size());
		
		Iterator<?> i = _currencies.iterator();
		for(; i.hasNext(); ) {
			CurrencyInfo ci = (CurrencyInfo) i.next();
			elements.add(ci.getCode());
		}
		
		String[] results = new String[ elements.size() ];
		elements.toArray(results);
		return results;
	}	
	
	public int count() {
		return _currencies.size();
	}
	
	public void clear() {
		_currencies.clear();
	}

}
