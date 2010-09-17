package net.vexelon.bgrates;


public class Utils {
	
	public static String roundDouble(String value, int maxlen) {
		String result = value;
		
		// 14.4257 = 14.43
		// 6.9852 = 6.99
		
		StringBuffer sb = new StringBuffer(value.length());
		boolean reminder = false;
		
		for( int i = value.length(); i > maxlen; i-- ) {
			
			char c = value.charAt(i);
			
			if ( c == '.' ) {
				sb.insert(0, c);
				continue;
			}
			
			if ( reminder )
				c += 1;
				
			sb.insert(0, c);
			reminder = c > '4';			
		}
		
		for( int i = maxlen; i > 0; i-- ) {
			
		}
		
		return result;
	}
	
}
