package net.vexelon.bgrates.db.models;

import java.util.Date;

public class CurrencyData {
	private int gold;
	private String name;
	private String code;
	private int ratio;
	private float reverserate;
	private float rate;
	private String extraInfo;
	private Date currDate;
	private String title;
	private int fStar;

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
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

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public float getReverseRate() {
		return reverserate;
	}

	public void setReverseRate(float reverserate) {
		this.reverserate = reverserate;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public Date getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getfStar() {
		return fStar;
	}

	public void setfStar(int fStar) {
		this.fStar = fStar;
	}

	@Override
	public String toString() {
		return "CurrencyData [gold=" + gold + ", name=" + name + ", code=" + code + ", ratio=" + ratio
				+ ", reverserate=" + reverserate + ", rate=" + rate + ", extraInfo=" + extraInfo + ", currDate="
				+ currDate + ", title=" + title + ", fStar=" + fStar + "]";
	}

}
