package com.subgraph.vega.internal.sslprobe;

public class TLSCipherSpec {

	public enum keyStrength { HIGH, LOW, EXPORT, NONE };
	
	private final String name;
	private final int number;
	private final keyStrength strength;
	private final boolean anonDH;
	private final boolean PFS;
	private final boolean RC4;

	TLSCipherSpec(String name, int number, keyStrength strength, boolean anonDH, boolean PFS, boolean RC4) {
		
		this.name = name;
		this.number = number;
		this.strength = strength;
		this.anonDH = anonDH;		
		this.PFS = PFS;
		this.RC4 = RC4;
	}

	public int getNumber() {
		return number;
	}

	public keyStrength getStrength() {
		return strength;
	}

	public boolean isAnonDH() {
		return anonDH;
	}

	public boolean isRC4() {
		return RC4;
	}

	public boolean isPFS() {
		return PFS;
	}

	public String getName() {
		return name;
	}
	
}
