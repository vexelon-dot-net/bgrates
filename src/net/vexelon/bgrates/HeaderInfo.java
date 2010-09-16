package net.vexelon.bgrates;

import android.os.Parcel;
import android.os.Parcelable;

public class HeaderInfo implements Parcelable {
	
	private String name = "";
	private String code = "";
	private String ratio = "";
	private String rate = "";
	private String reverseRate = "";
	private String extraInfo = "";
	private String curDate = "";
	private String title = "";
	
	public static final Parcelable.Creator<HeaderInfo> CREATOR = new Parcelable.Creator<HeaderInfo>() {
		public HeaderInfo createFromParcel(Parcel in) {
			return new HeaderInfo(in);
		}

		public HeaderInfo[] newArray(int size) {
			return new HeaderInfo[size];
		}
	};	
	
	public HeaderInfo() {
		
	}
	
	public HeaderInfo(Parcel in) {
		name = in.readString();
		code = in.readString();
		ratio = in.readString();
		rate = in.readString();
		reverseRate = in.readString();
		extraInfo = in.readString();
		curDate = in.readString();
		title = in.readString();
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
	public String getCurDate() {
		return curDate;
	}
	public void setCurDate(String curDate) {
		this.curDate = curDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public int describeContents() {
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
		dest.writeString(curDate);
		dest.writeString(curDate);
	}
}
