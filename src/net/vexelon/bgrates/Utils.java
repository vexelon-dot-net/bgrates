package net.vexelon.bgrates;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;


public class Utils {
	
	private final static String TAG = Defs.LOG_TAG; 
	
	public static String scaleNumber(BigDecimal number, int n ) {
		return number.setScale(n, BigDecimal.ROUND_HALF_UP).toPlainString();
	}
	
	public static String roundNumber(BigDecimal number, int n) {
		return number.round(new MathContext(n, RoundingMode.HALF_UP)).toPlainString();
	}
	
	/**
	 * Downloads a file given URL to specified destination
	 * @param url
	 * @param destFile
	 * @return
	 */
	//public static boolean downloadFile(Context context, String url, String destFile) {
	public static boolean downloadFile(Context context, String url, File destFile) {
		//Log.v(TAG, "@downloadFile()");
		//Log.d(TAG, "Downloading " + url);
		
		boolean ret = false;
		
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		InputStream is = null;
		
		try {
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			
			is = connection.getInputStream();
			bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);
			
			int n = 0;
			while( (n = bis.read()) != -1 )
				baf.append((byte) n);
			
			// save to internal storage
			//Log.v(TAG, "Saving downloaded file ...");
			fos = new FileOutputStream(destFile); 
				//context.openFileOutput(destFile, context.MODE_PRIVATE);
			fos.write(baf.toByteArray());
			fos.close();
			//Log.v(TAG, "File saved successfully.");
			
			ret = true;
		}
		catch(Exception e) {
			//Log.e(TAG, "Error while downloading and saving file !", e);
		}
		finally {
			try {
				if ( fos != null ) fos.close();
			} catch( IOException e ) {}
			try {
				if ( bis != null ) bis.close();
			} catch( IOException e ) {}
			try {
				if ( is != null ) is.close();
			} catch( IOException e ) {}
		}
		
		return ret;
	}

	/**
	 * Move a file stored in the cache to the internal storage of the specified context
	 * @param context
	 * @param cacheFile
	 * @param internalStorageName
	 */
	public static boolean moveCacheFile(Context context, File cacheFile, String internalStorageName) {
		
		boolean ret = false;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			fis = new FileInputStream(cacheFile);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int read = -1;
			while( (read = fis.read(buffer) ) != -1 ) {
				baos.write(buffer, 0, read);
			}
			baos.close();
			fis.close();

			fos = context.openFileOutput(internalStorageName, Context.MODE_PRIVATE);
			baos.writeTo(fos);
			fos.close();
			
			// delete cache
			cacheFile.delete();
			
			ret = true;
		}
		catch(Exception e) {
			//Log.e(TAG, "Error saving previous rates!");
		}
		finally {
			try { if ( fis != null ) fis.close(); } catch (IOException e) { }
			try { if ( fos != null ) fos.close(); } catch (IOException e) { }
		}
		
		return ret;
	}
	
	/**
	 * Display an alert dialog
	 * @param context
	 * @param messageResId
	 * @param titleResId
	 */
	public static void showAlertDialog(Context context, int messageResId, int titleResId) {

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setTitle(
				context.getResources().getString(
						titleResId)).setMessage(
								context.getResources().getString(messageResId)).setIcon(
				R.drawable.alert).setOnKeyListener(
				new DialogInterface.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog,
							int keyCode, KeyEvent event) {
						dialog.dismiss();
						return false;
					}
				}).create().show();				
	}
	
	/**
	 * Rounds a precision to a certain length
	 * @param value
	 * @param maxlen
	 * @return
	 */
//	@Deprecated
//	public static String roundPrecision(String value, int maxlen) {
//		
//		StringBuffer sb = new StringBuffer(value.length());
//		boolean reminder = false;
//		
//		for( int i = value.length() - 1; i > maxlen; i-- ) {
//			
//			char c = value.charAt(i);
//			
//			if ( c == '.' || c == ',' ) {
//				sb.insert(0, c);
//				continue;
//			}
//			else if ( c >= '0' && c <= '9' ) { // skip anything that's not a number
//			
//				int n = (int)c;
//				
//				if ( reminder )
//					n += 1;
//					
//				sb.insert(0, n);
//				reminder = n > 4;
//			}
//		}
//		
//		String result = value.split(".")[0] + sb.toString();
//		
//		return result;
//	}
	
}
