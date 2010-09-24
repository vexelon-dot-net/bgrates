package net.vexelon.bgrates;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class Utils {
	
	public static String scaleNumber(BigDecimal number, int n ) {
		return number.setScale(n, BigDecimal.ROUND_HALF_UP).toPlainString();
	}
	
	public static String roundNumber(BigDecimal number, int n) {
		return number.round(new MathContext(n, RoundingMode.HALF_UP)).toPlainString();
		//new BigDecimal(ci.getRate(), new MathContext(Defs.SCALE_SHOW_SHORT)).toPlainString();
	}
	
	/**
	 * Rounds a precision to a certain length
	 * @param value
	 * @param maxlen
	 * @return
	 */
	public static String roundPrecision(String value, int maxlen) {
		
		StringBuffer sb = new StringBuffer(value.length());
		boolean reminder = false;
		
		for( int i = value.length() - 1; i > maxlen; i-- ) {
			
			char c = value.charAt(i);
			
			if ( c == '.' || c == ',' ) {
				sb.insert(0, c);
				continue;
			}
			else if ( c >= '0' && c <= '9' ) { // skip anything that's not a number
			
				int n = (int)c;
				
				if ( reminder )
					n += 1;
					
				sb.insert(0, n);
				reminder = n > 4;
			}
		}
		
		String result = value.split(".")[0] + sb.toString();
		
		return result;
	}
	
}
