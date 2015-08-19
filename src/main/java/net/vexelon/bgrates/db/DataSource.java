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
package net.vexelon.bgrates.db;

import java.util.Date;
import java.util.List;

import net.vexelon.bgrates.db.models.CurrencyData;

/**
 * Encapsulates the available read-write operations to and from an underlying
 * data source implementation.
 */
public interface DataSource {

	/**
	 * Fetches a list of dates for which exchange rates were downloaded and
	 * available in the underlying data source.
	 * 
	 * @return A {@link List} of {@link Date} objects or an empty {@link List},
	 *         if no dates are available.
	 * @throws DataSourceException
	 */
	List<Date> getAvailableRatesDates() throws DataSourceException;

	/**
	 * Fetches the latest exchange rates from the underlying data source.
	 * 
	 * @return {@link List} or <code>null</code>, if no rates are
	 *         available.
	 * @throws DataSourceException
	 */
	List<CurrencyData> getRates() throws DataSourceException;

	/**
	 * Fetches exchange rates for a given date.
	 * 
	 * @param date
	 * @return {@link List} or <code>null</code>, if no rates are
	 *         available for the given date.
	 * @throws DataSourceException
	 */
	List<CurrencyData> getRates(Date date) throws DataSourceException;

	/**
	 * Adds exchange rates data for given download {@link Date}.
	 * 
	 * @param date
	 * @param rates
	 * @throws DataSourceException
	 */
	void addRates(Date date, List<CurrencyData> rates) throws DataSourceException;

}
