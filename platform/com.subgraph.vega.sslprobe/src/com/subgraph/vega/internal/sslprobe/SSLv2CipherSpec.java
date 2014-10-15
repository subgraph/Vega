package com.subgraph.vega.internal.sslprobe;

public class SSLv2CipherSpec {

	public enum keyStrength { HIGH, LOW, EXPORT, NONE };

	private String name;
	private byte[] number;
	private int num;
	private keyStrength strength;
	private boolean RC4;
	
	SSLv2CipherSpec(String name, byte[] number, keyStrength strength, boolean RC4) {
		this.setNumber(number);
		this.setName(name);
		this.setStrength(strength);
		this.setNum((number[2] & 0xFF) | ((number[1] & 0xFF) << 8) | ((number[0] & 0x0F) << 16));
		this.setRC4(RC4);
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public byte[] getNumber() {
		return number;
	}

	public void setNumber(byte[] number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public keyStrength getStrength() {
		return strength;
	}

	public void setStrength(keyStrength strength) {
		this.strength = strength;
	}

	public boolean isRC4() {
		return RC4;
	}

	public void setRC4(boolean rC4) {
		RC4 = rC4;
	}
}
