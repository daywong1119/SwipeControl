package com.daywong.swipecontrol;

public class SmartPickObject {

	private String pkg;
	private String cls;
	private int time;
	private int count = 1;

	public SmartPickObject(String pkg, String cls, int time, int count) {
		super();
		this.pkg = pkg;
		this.time = time;
		this.cls = cls;
		this.count = count;
	}

	public String getPkg() {
		return pkg;
	}

	public int getTime() {
		return time;
	}

	public int getCount() {
		return count;
	}

	public void increase() {
		count++;
	}

	public void increase(int h) {
		count += h;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}
}
