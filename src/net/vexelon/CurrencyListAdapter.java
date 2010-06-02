package net.vexelon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CurrencyListAdapter extends ArrayAdapter<CurrencyInfo> {
	
	private List<CurrencyInfo> _items;
	
	public CurrencyListAdapter(Context context, int textViewResId, List<CurrencyInfo> items) {
		super(context, textViewResId, items);
		this._items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
		if ( v == null ) {
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(R.layout.currency_row_layout, null);
		}
		
		CurrencyInfo ci = _items.get(position);
		if ( ci != null ) {
			TextView tvCode = (TextView) v.findViewById(R.id.code);
			if ( tvCode != null )
				tvCode.setText(ci.getCode());
			TextView tvRate = (TextView) v.findViewById(R.id.rate);
			if ( tvRate != null )
				tvRate.setText(ci.getRate());
		}
		
		return v;
	}

}
