/*
 * MyGlob Android Application
 * 
 * Copyright (C) 2013 Petar Petrov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.vexelon.bgrates.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils {

	public static final long MILLIS_PER_DAY = (24 * 60 * 60 * 1000);

	protected static Calendar CALENDAR = Calendar.getInstance();

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

}
