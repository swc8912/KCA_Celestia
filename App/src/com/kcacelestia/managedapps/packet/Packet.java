package com.kcacelestia.managedapps.packet;


public class Packet {
	private String cmd;
	private String regnum;
	private String passwd;
	private Lecture lec;
	private String qrcode;
	private int surprise;
	private int leccode;
	private String Url;
	private int force;
	
	public int getForce() {
		return force;
	}
	public void setForce(int force) {
		this.force = force;
	}
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
	public int getSurprise() {
		return surprise;
	}
	public void setSurprise(int surprise) {
		this.surprise = surprise;
	}
	public int getLeccode() {
		return leccode;
	}
	public void setLeccode(int leccode) {
		this.leccode = leccode;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getRegnum() {
		return regnum;
	}
	public void setRegnum(String regnum) {
		this.regnum = regnum;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public Lecture getLec() {
		return lec;
	}
	public void setLec(Lecture lec) {
		this.lec = lec;
	}	
	
}
