package com.subgraph.vega.internal.sslprobe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.subgraph.vega.internal.sslprobe.TLSCipherSpec.keyStrength;
	
public class SSLServerScanResult {

	private final String targetHost;
	private final int targetPort;
	
	public enum Flag { 
		PFS_SUPPORTED, PFS_SERVER_PREFERENCE, RC4_SERVER_PREFERENCE, RC4_SUPPORTED, WEAK_CIPHERS_SUPPORTED, 
		EXPORT_CIPHERS_SUPPORTED, TLS_COMPRESSION, ANON_DH, SMALL_RSA_KEY, SELF_SIGNED_CERTIFICATE, 
		SERVER_PREFERENCE, SSLV2, SSLV3, TLS1, TLS11, TLS12, MD5_SIGNED_CERTIFICATE, SHA1_SIGNED_CERTIFICATE
	};
		
	private Set<Flag> flags = Collections.synchronizedSet(EnumSet.noneOf(Flag.class));
	
	private boolean TLSProbeFailure;
	private String errorMessage;
	
	private Set<CertificateAnalyzer> certs;
	private List<TLSCipherSpec> serverTLSCiphers;
	private List <SSLv2CipherSpec> serverSSLv2Ciphers;
	private List<TLSCipherSpec> serverPreferenceOrder;
	
	private int outstandingProbeCount;
	
	SSLServerScanResult(String targetHost, int targetPort) {
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.certs = new HashSet<CertificateAnalyzer>();
		this.serverTLSCiphers = new ArrayList<TLSCipherSpec>();
	}

	public String getTargetHost() {
		return targetHost;
	}
	
	public int getTargetPort() {
		return targetPort;
	}

	public synchronized void setTLSProbeFailure(String message) {
		errorMessage = message;
		TLSProbeFailure = true;
	}
	
	public synchronized void setServerTLSCiphersServerPreferenceOrder(List<TLSCipherSpec> ciphers) {
		this.serverPreferenceOrder = ciphers;
	}	
	public synchronized void setServerSSLv2Ciphers(List<SSLv2CipherSpec> ciphers) {
		this.serverSSLv2Ciphers = ciphers;
	}
	
	public void addServerTLSCipher(TLSCipherSpec cipher) {
		analyzeCipher(cipher);
		serverTLSCiphers.add(cipher);
	}
	
	public List<TLSCipherSpec> getServerTLSCiphers() {
		return Collections.unmodifiableList(new ArrayList<TLSCipherSpec>(serverTLSCiphers));
	}
	
	
	public void addServerCertificate(CertificateAnalyzer certificate) {
		if(certs.add(certificate)) {
			analyzeCertificate(certificate);
		}
	}

	private void analyzeCipher(TLSCipherSpec cipher) {
		if(cipher.getStrength() == keyStrength.EXPORT) {
			flags.add(Flag.EXPORT_CIPHERS_SUPPORTED);
		} else if(cipher.getStrength() == keyStrength.LOW) {
			flags.add(Flag.WEAK_CIPHERS_SUPPORTED);
		}
		if(cipher.isAnonDH()) {
			flags.add(Flag.ANON_DH);
		}
		if(cipher.isRC4()) {
			flags.add(Flag.RC4_SUPPORTED);
		}
		if(cipher.isPFS()){
			flags.add(Flag.PFS_SUPPORTED);
		}
	}

	private void analyzeCertificate(CertificateAnalyzer certificate) {
		if(certificate.selfSigned()) {
			flags.add(Flag.SELF_SIGNED_CERTIFICATE);
		}

		if(certificate.isRSA() && certificate.getRSAModulusBitLength() <= 1024) {
			flags.add(Flag.SMALL_RSA_KEY);
		}
		
		if(certificate.isSignedMD5()) {
			flags.add(Flag.MD5_SIGNED_CERTIFICATE);
		}
		
		if (certificate.isSignedSHA1()) {
			flags.add(Flag.SHA1_SIGNED_CERTIFICATE);
		}
	}
	
	public synchronized void setServerTLSCiphers(ArrayList<TLSCipherSpec> ciphers) {
		this.serverTLSCiphers = ciphers;
	}
	
	public void setFlag(Flag flag) {
		flags.add(flag);
	}

	public boolean isSet(Flag flag) {
		return flags.contains(flag);
	}
	
	public boolean getTLSProbeFailure() {
		return this.TLSProbeFailure;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public List<TLSCipherSpec> getServerTLSCiphersServerPreferenceOrder() {
		return serverPreferenceOrder;
	}
	
	public Collection<CertificateAnalyzer> getServerCertificates() {
		return certs;
	}
	
	public List<SSLv2CipherSpec> getServerSSLv2Ciphers() {
		return serverSSLv2Ciphers;
	}
	
	public synchronized void incrementOutstandingProbeCount() {
		outstandingProbeCount += 1;
	}
	
	public synchronized void decrementOutstandingProbeCount() {
		outstandingProbeCount -= 1;
		if(outstandingProbeCount == 0) {
			notifyAll();
		}			
	}
	
	public synchronized void waitForOutstandingProbes() throws InterruptedException {
		while(outstandingProbeCount > 0) {
			this.wait();
		}
	}
}

