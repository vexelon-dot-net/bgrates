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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Random;

import android.util.Log;
import net.vexelon.bgrates.Defs;

public class NumberUtils {

	private static Random _random = null;

	/**
	 * Get random integer value
	 * 
	 * @param max
	 * @return
	 */
	public static int getRandomInt(int max) {
		if (_random == null) {
			_random = new SecureRandom();
		}
		return _random.nextInt(max);
	}

	public static BigDecimal zeroIfNull(String value) {
		BigDecimal result = new BigDecimal("0.00");
		if (value != null) {
			try {
				result = new BigDecimal(value);
			} catch (NumberFormatException e) {
				Log.w(Defs.LOG_TAG, "Failed to get decimal value from " + value + "!", e);
			}
		}
		return result;
	}

	public static String scaleNumber(BigDecimal number, int n) {
		return number.setScale(n, RoundingMode.HALF_UP).toPlainString();
	}

	public static String roundNumber(BigDecimal number, int n) {
		return number.round(new MathContext(n, RoundingMode.HALF_UP)).toPlainString();
	}

	public static String scaleCurrency(BigDecimal number, String code) {
		try {
			Currency currency = Currency.getInstance(code);
			NumberFormat format = NumberFormat.getCurrencyInstance();
			format.setCurrency(currency);
			return format.format(number.doubleValue());
		} catch (IllegalArgumentException e) {
			// default
		}
		return number.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
	}

	public static String scaleCurrency(BigDecimal number, int n) {
		return number.setScale(n, RoundingMode.HALF_EVEN).toPlainString();
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
}
