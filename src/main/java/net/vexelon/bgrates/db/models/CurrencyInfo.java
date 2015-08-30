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

@Deprecated
public class CurrencyInfo implements Parcelable {

	public enum Tendency {
		TendencyUnknown, TendencyEqual, TendencyUp, TendencyDown,
	}

	private String name = "";
	private String code = "";
	private String ratio = "0";
	private String rate = "0";
	private String reverseRate = "0";
	private String extraInfo = "";
	private Tendency tendency = Tendency.TendencyUnknown;

	public CurrencyInfo() {

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

	public Tendency getTendency() {
		return tendency;
	}

	public void setTendency(Tendency tendency) {
		this.tendency = tendency;
	}

	//// Parcelable implementation ////

	public static final Parcelable.Creator<CurrencyInfo> CREATOR = new Parcelable.Creator<CurrencyInfo>() {
		public CurrencyInfo createFromParcel(Parcel in) {
			return new CurrencyInfo(in);
		}

		public CurrencyInfo[] newArray(int size) {
			return new CurrencyInfo[size];
		}
	};

	public CurrencyInfo(Parcel in) {
		this.name = in.readString();
		this.code = in.readString();
		this.ratio = in.readString();
		this.rate = in.readString();
		this.reverseRate = in.readString();
		this.extraInfo = in.readString();
		this.tendency = (Tendency) in.readValue(Tendency.class.getClassLoader());
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
		dest.writeValue(tendency);
	}
}
