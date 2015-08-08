/*
 * The MIT License
 * 
 * Copyright (c) 2010 Petar Petrov
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
package net.vexelon.bgrates.db.models;

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
	
	//// Parcelable implementation ////	
	
	public static final Parcelable.Creator<HeaderInfo> CREATOR = new Parcelable.Creator<HeaderInfo>() {
		public HeaderInfo createFromParcel(Parcel in) {
			return new HeaderInfo(in);
		}

		public HeaderInfo[] newArray(int size) {
			return new HeaderInfo[size];
		}
	};		
	
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
