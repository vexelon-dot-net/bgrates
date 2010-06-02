package net.vexelon;

public class CurrencyInfo {
	
	private String name;
	private String code;
	private String ratio;
	private String rate;
	private String reverseRate;
	private String extraInfo;
	
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
