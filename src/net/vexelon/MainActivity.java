package net.vexelon;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	ListView _listView;
	private String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		_listView = (ListView)findViewById(R.id.ListView01);
		_listView.setAdapter(
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr ));
	}

}
