/*
 * The MIT License
 * 
 * Copyright (c) 2015 Petar Petrov
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
package net.vexelon.bgrates.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;

public class IOUtils {

	public static final int BUFFER_PAGE_SIZE = 4096; // 4k

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
	 * Write input stream data to PRIVATE internal storage file.
	 * 
	 * @param context
	 * @param source
	 * @param internalStorageName
	 * @throws IOException
	 */
	public static void writeToInternalStorage(Context context, InputStream source, String internalStorageName)
			throws IOException {

		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(internalStorageName, Context.MODE_PRIVATE);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			BufferedInputStream bis = new BufferedInputStream(source);
			byte[] buffer = new byte[4096];
			int read = -1;
			while ((read = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, read);
			}
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
			}
			try {
				if (source != null)
					source.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Reads an input stream into a byte array
	 * 
	 * @param source
	 * @return Byte array of input stream data
	 * @throws IOException
	 */
	public static byte[] read(InputStream source) throws IOException {
		ReadableByteChannel srcChannel = Channels.newChannel(source);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(source.available() > 0 ? source.available()
				: BUFFER_PAGE_SIZE);
		WritableByteChannel destination = Channels.newChannel(baos);

		try {
			ByteBuffer buffer = ByteBuffer.allocate(BUFFER_PAGE_SIZE);
			while (srcChannel.read(buffer) > 0) {
				buffer.flip();
				while (buffer.hasRemaining()) {
					destination.write(buffer);
				}
				buffer.clear();
			}
			return baos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (srcChannel != null)
					srcChannel.close();
			} catch (IOException e) {
			}
			try {
				if (source != null)
					source.close();
			} catch (IOException e) {
			}
			try {
				if (destination != null)
					destination.close();
			} catch (IOException e) {
			}
		}
	}

}
