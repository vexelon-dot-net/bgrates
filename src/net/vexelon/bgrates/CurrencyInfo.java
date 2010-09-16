package net.vexelon.bgrates;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrencyInfo implements Parcelable {
	
	private String name = "";
	private String code = "";
	private String ratio = "0";
	private String rate = "0";
	private String reverseRate = "0";
	private String extraInfo = "";
	
	public static final Parcelable.Creator<CurrencyInfo> CREATOR = new Parcelable.Creator<CurrencyInfo>() {
		public CurrencyInfo createFromParcel(Parcel in) {
			return new CurrencyInfo(in);
		}

		public CurrencyInfo[] newArray(int size) {
			return new CurrencyInfo[size];
		}
	};	
	
	public CurrencyInfo() {
		
	}
	
	public CurrencyInfo(Parcel in) {
		this.name = in.readString();
		this.code = in.readString();
		this.ratio = in.readString();
		this.rate = in.readString();
		this.reverseRate = in.readString();
		this.extraInfo = in.readString();
	}
	
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
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(code);
		dest.writeString(ratio);
		dest.writeString(rate);
		dest.writeString(reverseRate);
		dest.writeString(extraInfo);
	}
}
