package net.vexelon;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	ListView _listView;
	private String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople"};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		downloadFile(Defs.URL_BNB_RATES, Defs.INTERNAL_STORAGE_FILE);
		
		_listView = (ListView)findViewById(R.id.ListView01);
		_listView.setAdapter(
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr ));
	}
	
	private void parseRates(String resource) throws XmlPullParserException {

		Log.d(MainActivity.class.getName(), "Parsing XML file ...");
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(false);
		factory.setValidating(false);
		XmlPullParser xpp = factory.newPullParser();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(resource.getBytes());
		xpp.setInput(bais, null);
		
		try {
			//TODO:
		}
		finally {
			try { bais.close(); } catch (IOException e) { }
		}		
		
	}
	
	private void downloadFile(String url, String destFile) {
		
		Log.d(MainActivity.class.getName(), "Downloading file " + url);
		
		try {
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			
			InputStream is = connection.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);
			
			int n = 0;
			while( (n = bis.read()) != -1 )
				baf.append((byte) n);
			
			bis.close();
			is.close();
			
			// save to internal storage
			Log.d(MainActivity.class.getName(), "Saving downloaded file ...");
			
			FileOutputStream fos = openFileOutput(destFile, Context.MODE_PRIVATE);
			fos.write(baf.toByteArray());
			fos.close();
		}
		catch(Exception e) {
			
		}
		finally {
			
		}
	}

}
