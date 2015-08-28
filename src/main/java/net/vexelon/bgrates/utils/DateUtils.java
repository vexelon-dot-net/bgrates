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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;

public class DateUtils {

	public static final long MILLIS_PER_DAY = (24 * 60 * 60 * 1000);

	protected static final Calendar CALENDAR = Calendar.getInstance();
	protected static DateFormat DT_FORMAT = null;

	private static void initDateFormat(Context context) {
		if (DT_FORMAT == null) {
			DT_FORMAT = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
					context.getResources().getConfiguration().locale);
		}
	}

	/**
	 * Millisecond representation of the given time
	 * 
	 * @return milliseconds representing the given time
	 */
	public static long calculateTime(int hours, int minutes, int seconds, int ms) {
		return (((((hours * 60) + minutes) * 60) + seconds) * 1000) + ms;
	}

	/**
	 * @see {@link #calculateTime(int, int, int, int)}
	 */
	public static long calculateTime(int hours, int minutes, int seconds) {
		return calculateTime(hours, minutes, seconds, 0);
	}

	public static long getTimeFromDate(Date date) {
		Date now = Calendar.getInstance().getTime();
		long timePortion = now.getTime() % MILLIS_PER_DAY;

		return timePortion;
	}

	/**
	 * Checks if year, month, date of two <code>Date</code> objects match.
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean equalDates(Date first, Date second) {
		Calendar calFirst = Calendar.getInstance();
		Calendar calSecond = Calendar.getInstance();

		calFirst.setTime(first);
		calSecond.setTime(second);

		return calFirst.get(Calendar.YEAR) == calSecond.get(Calendar.YEAR)
				&& calFirst.get(Calendar.MONTH) == calSecond.get(Calendar.MONTH)
				&& calFirst.get(Calendar.DAY_OF_MONTH) == calSecond.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Reset <code>Date</code> object to start 00:00:00 time of the day.
	 * 
	 * @param date
	 * @return
	 */
	public static Date zeroTimePortionOfADate(Date date) {
		// Calendar cal =
		// GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC +0"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.AM_PM, Calendar.AM);
		return cal.getTime();
	}

	/**
	 * @return the first date in 1970 with millisSinceMidnight added to it
	 */
	public static Date getDateFromTime(long millisSinceMidnight) {

		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC +0"));
		Date date = new Date();
		date.setTime(millisSinceMidnight);
		cal.setTime(date);

		return cal.getTime();
	}

	/**
	 * Get the last hour, minute, second of the date specified
	 * 
	 * @param date
	 * @return
	 */
	public static Date endOfDay(Date date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MILLISECOND, 999);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MINUTE, 59);
			return calendar.getTime();
		}
	}

	/**
	 * 
	 * @return
	 * @see #endOfDay(Date)
	 */
	public static Date endOfToday() {
		return endOfDay(new Date());
	}

	/**
	 * Get the first hour, minute, second of the date specified
	 * 
	 * @param date
	 * @return
	 */
	public static Date startOfDay(Date date) {
		Calendar calendar = CALENDAR;
		synchronized (calendar) {
			calendar.setTime(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			return calendar.getTime();
		}
	}

	/**
	 * 
	 * @return
	 * @see #startOfDay(Date)
	 */
	public static Date startOfToday() {
		return startOfDay(new Date());
	}

	public static String toString(Context context, Date date) {
		initDateFormat(context);
		return DT_FORMAT.format(date);
	}

}
