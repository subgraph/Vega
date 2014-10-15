package com.subgraph.vega.internal.sslprobe;

import java.util.ArrayList;
import java.util.List;

public class TLSProbeResult {
	
	private List<TLSCipherSpec> rejectedCiphers;
	private TLSCipherSpec acceptedCipher;
	private ArrayList<CertificateAnalyzer> serverCertificates;
	private boolean TLScompressionSupport;
	private boolean error;
	private String errorMessage = "";
	

	TLSProbeResult() {
		serverCertificates = new ArrayList<CertificateAnalyzer>();
	}
	
	public void addCiphers(List<TLSCipherSpec> rejected, TLSCipherSpec accepted) {
		this.setRejectedCiphers(rejected);
		this.acceptedCipher = accepted;
	}
	
	public void addRejectedCiphersOnly(List<TLSCipherSpec> rejected) {
		this.setRejectedCiphers(rejected);
	}
	
	public void addServerCertificate(CertificateAnalyzer cert) {
		serverCertificates.add(cert);
	}
	
	public void setTLSCompressionSupport(boolean tf) {
		this.TLScompressionSupport = tf;
	}
	
	public void setError(boolean error, String message) {
		this.error = error;
		this.errorMessage = message;
	}

	public void addCertificate(CertificateAnalyzer ca) {
		this.serverCertificates.add(ca);
	}

	public boolean isError() {
		return this.error;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public TLSCipherSpec getAcceptedCipher() {
		return this.acceptedCipher;
	}

	public ArrayList<CertificateAnalyzer> getServerCertificates() {
		return this.serverCertificates;
	}

	public List<TLSCipherSpec> getRejectedCiphers() {
		return rejectedCiphers;
	}

	public void setRejectedCiphers(List<TLSCipherSpec> rejectedCiphers) {
		this.rejectedCiphers = rejectedCiphers;
	}
	
	public boolean getTLSCompressionSupport() {
		return this.TLScompressionSupport;
	}
	
}
