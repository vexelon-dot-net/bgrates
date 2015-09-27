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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import android.content.Context;

public class IOUtils {

	public static final int BUFFER_PAGE_SIZE = 4096; // 4k

	/**
	 * Downloads a file given URL to specified destination.
	 * 
	 * @param url
	 * @param destFile
	 * @throws IOException
	 */
	public static void downloadFile(String url, File destFile) throws IOException {
		InputStream input = null;
		FileOutputStream output = null;
		try {
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			input = connection.getInputStream();
			byte[] fileData = read(input);
			output = new FileOutputStream(destFile);
			output.write(fileData);
		} finally {
			closeQuitely(input);
			closeQuitely(output);
		}
	}

	/**
	 * Moves a file stored in the cache to the internal storage of the specified
	 * context.
	 * 
	 * @param context
	 * @param cacheFile
	 * @param internalStorageName
	 * @throws IOException
	 */
	public static void moveCacheFile(Context context, File cacheFile, String internalStorageName) throws IOException {
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(cacheFile);
			byte[] fileData = read(input);
			output = context.openFileOutput(internalStorageName, Context.MODE_PRIVATE);
			output.write(fileData);
			// delete cache
			cacheFile.delete();
		} finally {
			closeQuitely(input);
			closeQuitely(output);
		}
	}

	/**
	 * Writes input stream data to PRIVATE internal storage file.
	 * 
	 * @param context
	 * @param source
	 * @param internalStorageName
	 * @throws IOException
	 */
	public static void writeToInternalStorage(Context context, InputStream source, String internalStorageName)
			throws IOException {
		BufferedOutputStream bos = null;
		try {
			byte[] fileData = read(source);
			bos = new BufferedOutputStream(context.openFileOutput(internalStorageName, Context.MODE_PRIVATE));
			bos.write(fileData);
			bos.flush();
		} finally {
			closeQuitely(source);
			closeQuitely(bos);
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				source.available() > 0 ? source.available() : BUFFER_PAGE_SIZE);
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
			closeQuitely(srcChannel);
			closeQuitely(source);
			closeQuitely(destination);
		}
	}

	public static void closeQuitely(Closeable source) {
		try {
			if (source != null) {
				source.close();
			}
		} catch (IOException e) {
		}
	}
}
