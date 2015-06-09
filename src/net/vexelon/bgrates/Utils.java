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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Xml;
import android.view.KeyEvent;

public class Utils {

	private final static String TAG = Defs.LOG_TAG;

	public static String scaleNumber(BigDecimal number, int n) {
		return number.setScale(n, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	public static String roundNumber(BigDecimal number, int n) {
		return number.round(new MathContext(n, RoundingMode.HALF_UP)).toPlainString();
	}

	/**
	 * Downloads a file given URL to specified destination
	 * 
	 * @param url
	 * @param destFile
	 * @return
	 */
	// public static boolean downloadFile(Context context, String url, String
	// destFile) {
	public static boolean downloadFile(Context context, String url, File destFile) {
		// Log.v(TAG, "@downloadFile()");
		// Log.d(TAG, "Downloading " + url);
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
			while ((n = bis.read()) != -1)
				baf.append((byte) n);

			// save to internal storage
			// Log.v(TAG, "Saving downloaded file ...");
			fos = new FileOutputStream(destFile);
			// context.openFileOutput(destFile, context.MODE_PRIVATE);
			fos.write(baf.toByteArray());
			fos.close();
			// Log.v(TAG, "File saved successfully.");

			ret = true;
		} catch (Exception e) {
			// Log.e(TAG, "Error while downloading and saving file !", e);
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
			try {
				if (bis != null)
					bis.close();
			} catch (IOException e) {
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}

		return ret;
	}

	/**
	 * Move a file stored in the cache to the internal storage of the specified
	 * context
	 * 
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
			while ((read = fis.read(buffer)) != -1) {
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
		} catch (Exception e) {
			// Log.e(TAG, "Error saving previous rates!");
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
		}

		return ret;
	}

	/**
	 * Display an alert dialog
	 * 
	 * @param context
	 * @param messageResId
	 * @param titleResId
	 */
	public static void showAlertDialog(Context context, int messageResId, int titleResId) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
		alertBuilder.setTitle(context.getResources().getString(titleResId))
				.setMessage(context.getResources().getString(messageResId)).setIcon(R.drawable.alert)
				.setOnKeyListener(new DialogInterface.OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						dialog.dismiss();
						return false;
					}
				}).create().show();
	}

	/**
	 * Rounds a precision to a certain length
	 * 
	 * @param value
	 * @param maxlen
	 * @return
	 */
	// @Deprecated
	// public static String roundPrecision(String value, int maxlen) {
	//
	// StringBuffer sb = new StringBuffer(value.length());
	// boolean reminder = false;
	//
	// for( int i = value.length() - 1; i > maxlen; i-- ) {
	//
	// char c = value.charAt(i);
	//
	// if ( c == '.' || c == ',' ) {
	// sb.insert(0, c);
	// continue;
	// }
	// else if ( c >= '0' && c <= '9' ) { // skip anything that's not a number
	//
	// int n = (int)c;
	//
	// if ( reminder )
	// n += 1;
	//
	// sb.insert(0, n);
	// reminder = n > 4;
	// }
	// }
	//
	// String result = value.split(".")[0] + sb.toString();
	//
	// return result;
	// }

	/**
	 * Serialize an XML element recursively
	 * 
	 * @param node
	 * @param serializer
	 * @throws IOException
	 */
	private static void serializeXmlElement(Node node, XmlSerializer serializer) throws IOException {

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node current = children.item(i);

			if (current.getNodeType() == Node.ELEMENT_NODE) {
				Element child = (Element) current;
				serializer.startTag("", child.getNodeName());
				serializeXmlElement(child, serializer);
				serializer.endTag("", child.getNodeName());
			} else if (current.getNodeType() == Node.TEXT_NODE) {
				Text child = (Text) current;
				serializer.text(child.getData());
			} else if (current.getNodeType() == Node.CDATA_SECTION_NODE) {
				CDATASection child = (CDATASection) current;
				serializer.cdsect(child.getData());
			} else if (current.getNodeType() == Node.COMMENT_NODE) {
				Comment child = (Comment) current;
				serializer.comment(child.getData());
			}
		}
	}

	/**
	 * Serialize a Root element and all it's descendants
	 * 
	 * @param document
	 *            - org.w3c.dom Xml Document
	 * @param serializer
	 * @throws Exception
	 */
	private static void serializeXml(Document document, XmlSerializer serializer) throws Exception {
		serializer.startDocument("UTF-8", true);
		document.getDocumentElement().normalize();
		serializeXmlElement(document, serializer);
		serializer.endDocument();
	}

	/**
	 * Parse org.w3c.dom Document and serialized to a String using Android
	 * Util.xml
	 * 
	 * @param document
	 *            - org.w3c.dom Xml Document
	 * @return
	 * @throws RuntimeException
	 */
	public static String getXmlDoc(Document document) throws RuntimeException {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter(1024);

		try {
			serializer.setOutput(writer);
			serializeXml(document, serializer);
		} catch (Exception e) {
			throw new RuntimeException("Failed converting Xml to String!", e);
		}

		return writer.toString();
	}

	/**
	 * Parse org.w3c.dom Document and serialized to a file using Android
	 * Util.xml
	 * 
	 * @param document
	 *            - org.w3c.dom Xml Document
	 * @param file
	 * @throws RuntimeException
	 */
	public static void saveXmlDoc(Document document, File file) throws RuntimeException {
		XmlSerializer serializer = Xml.newSerializer();

		try {
			FileWriter writer = new FileWriter(file);
			serializer.setOutput(writer);
			serializeXml(document, serializer);
		} catch (Exception e) {
			throw new RuntimeException("Failed save Xml to " + file.getName(), e);
		}
	}
}
