package net.vexelon.bgrates;

public class CurrencyInfo {
	
	private String name = "";
	private String code = "";
	private String ratio = "0";
	private String rate = "0";
	private String reverseRate = "0";
	private String extraInfo = "";
	
	public CurrencyInfo() {
		
	}
	
//	public CurrencyInfo( String name, String code, String ratio, String rate, String reverseRate, String extraInfo ) {
//		this.name = name;
//		this.code = code;
//		this.ratio = ratio;
//		this.rate = rate;
//		this.reverseRate = reverseRate;
//		this.extraInfo = extraInfo;
//	}
	
	public String getCountryCode() {
		return this.code.toLowerCase().substring(0, 2); 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRatio() {
		return ratio;
	}

	public void setRatio(String ratio) {
		this.ratio = ratio;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getReverseRate() {
		return reverseRate;
	}

	public void setReverseRate(String reverseRate) {
		this.reverseRate = reverseRate;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
}
