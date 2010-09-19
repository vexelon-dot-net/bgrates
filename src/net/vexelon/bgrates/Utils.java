package net.vexelon.bgrates;


public class Utils {
	
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
