package net.vexelon.bgrates.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import net.vexelon.bgrates.Defs;
import android.util.Log;

public class NumberUtils {

	private static Random _random = null;

	/**
	 * Get random integer value
	 * 
	 * @param max
	 * @return
	 */
	public static int getRandomInt(int max) {
		if (_random == null)
			_random = new Random(System.currentTimeMillis());

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
		return number.setScale(n, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	public static String roundNumber(BigDecimal number, int n) {
		return number.round(new MathContext(n, RoundingMode.HALF_UP)).toPlainString();
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
