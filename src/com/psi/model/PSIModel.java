package com.psi.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.psi.utils.DateUtils;

public class PSIModel {
	private String name;
	private Date birthday;
	private Date currentDate;
	private List<String> XAxisLabels = new ArrayList<String>();
	private List<String> YAxisLabels = new ArrayList<String>();
	private String XAxisTitle;
	private String YAxisTitle;

	private List<Long> physicalDatas = new ArrayList<Long>();
	private List<Long> sensitiveDatas = new ArrayList<Long>();
	private List<Long> intellectualDatas = new ArrayList<Long>();

	// 波峰
	private int waveCrest = 100;
	private int YAxistPosition;
	private float scaling = 0.5f;
	private int realWaveCrest;

	public PSIModel(String name, Date birthday) {
		this.name = name;
		this.birthday = birthday;
	}

	public void calcPSIModel() {
		XAxisLabels.clear();
		physicalDatas.clear();
		sensitiveDatas.clear();
		intellectualDatas.clear();

		realWaveCrest = Math.round(2 * this.waveCrest * this.scaling);
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTime(this.currentDate);
		int dayOfCurrentMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i = 0; i < dayOfCurrentMonth; i++) {
			XAxisLabels.add(String.valueOf(i + 1));
			currentCalendar.set(Calendar.DATE, 1);
			currentCalendar.add(Calendar.DATE, i);
			long days = DateUtils.getDaysBetween(birthday, currentCalendar.getTime());
			if (days >= 0) {
				physicalDatas.add(physical(days));
				sensitiveDatas.add(sensitive(days));
				intellectualDatas.add(intellectual(days));
			} else {
				physicalDatas.add(null);
				sensitiveDatas.add(null);
				intellectualDatas.add(null);
			}
		}
	}

	private Long physical(long x) {
		return (long) ((waveCrest - getRealPhysical(verificationDay(x))) * scaling) + YAxistPosition;
	}

	private Long sensitive(long x) {
		return (long) ((waveCrest - getRealSensitive(verificationDay(x))) * scaling) + YAxistPosition;
	}

	private Long intellectual(long x) {
		return (long) ((waveCrest - getRealIntellectual(verificationDay(x))) * scaling) + YAxistPosition;
	}

	private long verificationDay(long x) {
		return (x <= 0) ? 0L : x;
	}

	public Long getRealPhysical(long x) {
		return Math.round(waveCrest * Math.sin(2 * verificationDay(x) * Math.PI / 23.0));
	}

	public Long getRealPhysical() {
		return getRealPhysical(DateUtils.getDaysBetween(birthday, this.currentDate));
	}

	public Long getRealSensitive(long x) {
		return Math.round(waveCrest * Math.sin(2 * verificationDay(x) * Math.PI / 28.0));
	}

	public Long getRealSensitive() {
		return getRealSensitive(DateUtils.getDaysBetween(birthday, this.currentDate));
	}

	public Long getRealIntellectual(long x) {
		return Math.round(waveCrest * Math.sin(2 * verificationDay(x) * Math.PI / 33.0));
	}

	public Long getRealIntellectual() {
		return getRealIntellectual(DateUtils.getDaysBetween(birthday, this.currentDate));
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public List<String> getXAxisLabels() {
		return XAxisLabels;
	}

	public void setXAxisLabels(List<String> xAxisLabels) {
		XAxisLabels = xAxisLabels;
	}

	public List<String> getYAxisLabels() {
		return YAxisLabels;
	}

	public void setYAxisLabels(List<String> yAxisLabels) {
		YAxisLabels = yAxisLabels;
	}

	public String getXAxisTitle() {
		return XAxisTitle;
	}

	public void setXAxisTitle(String xAxisTitle) {
		XAxisTitle = xAxisTitle;
	}

	public String getYAxisTitle() {
		return YAxisTitle;
	}

	public void setYAxisTitle(String yAxisTitle) {
		YAxisTitle = yAxisTitle;
	}

	public List<Long> getPhysicalDatas() {
		return physicalDatas;
	}

	public void setPhysicalDatas(List<Long> physicalDatas) {
		this.physicalDatas = physicalDatas;
	}

	public List<Long> getSensitiveDatas() {
		return sensitiveDatas;
	}

	public void setSensitiveDatas(List<Long> sensitiveDatas) {
		this.sensitiveDatas = sensitiveDatas;
	}

	public List<Long> getIntellectualDatas() {
		return intellectualDatas;
	}

	public void setIntellectualDatas(List<Long> intellectualDatas) {
		this.intellectualDatas = intellectualDatas;
	}

	public int getWaveCrest() {
		return waveCrest;
	}

	public void setWaveCrest(int waveCrest) {
		this.waveCrest = waveCrest;
	}

	public int getYAxistPosition() {
		return YAxistPosition;
	}

	public void setYAxistPosition(int yAxistPosition) {
		YAxistPosition = yAxistPosition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getScaling() {
		return scaling;
	}

	public void setScaling(float scaling) {
		this.scaling = scaling;
	}

	public int getRealWaveCrest() {
		return realWaveCrest;
	}

	public void setRealWaveCrest(int realWaveCrest) {
		this.realWaveCrest = realWaveCrest;
	}
}
