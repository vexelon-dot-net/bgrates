package net.vexelon.bgrates;

import java.util.List;

import net.vexelon.bgrates.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

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
		
		// set texts
		CurrencyInfo ci = _items.get(position);
		if ( ci != null ) {
			
			setResText(v, R.id.name, ci.getName());
			setResText(v, R.id.code, ci.getCode());
			setResText(v, R.id.ratio, ci.getRatio());
			setResText(v, R.id.rate,
					ci.getRate().length() > Defs.MAX_RATE_CHARS_SIZE ? ci.getRate().subSequence(0, Defs.MAX_RATE_CHARS_SIZE) : ci.getRate()
					);
			
//			String text = String.format("%s %s", 
//					ci.getRate().length() > Defs.MAX_RATE_CHARS_SIZE ? ci.getRate().subSequence(0, Defs.MAX_RATE_CHARS_SIZE) : ci.getRate(),
//					getContext().getResources().getString(R.string.bg_currency_name)
//					);
			
			// add last
			ImageView icon = (ImageView) v.findViewById(R.id.icon);
			int imgId = ExchangeRate.getResrouceFromCode(ci);
			if ( imgId != -1 ) {
				icon.setImageResource(imgId);
//				icon.setScaleType(ScaleType.FIT_XY);
//				icon.setAdjustViewBounds(true);
//				android.widget.RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT|RelativeLayout.CENTER_VERTICAL);
//				icon.setLayoutParams(lp);
			}			
		}
		
		return v;
	}

	private void setResText(View v, int id, CharSequence text) {
		TextView tx = (TextView)v.findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}	
	
}
