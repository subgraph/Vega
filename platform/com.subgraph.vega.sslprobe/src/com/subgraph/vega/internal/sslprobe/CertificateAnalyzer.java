package com.subgraph.vega.internal.sslprobe;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class CertificateAnalyzer {

	private X509Certificate certificate;
	private boolean error = false;
	
	CertificateAnalyzer () {
		
	}
	
	public void addCert(X509Certificate certificate) {
		this.certificate = certificate;
	}
	
	
	public boolean selfSigned() {
		
		PublicKey k = certificate.getPublicKey();
		try { 
			certificate.verify(k);
		} catch (InvalidKeyException e) {
			System.out.println("Invalid key, not self signed");
			return false;
		} catch (NoSuchAlgorithmException e) {
			return false;
		} catch (NoSuchProviderException e) {
			return false;
		} catch (SignatureException e) {
			return false;
		} catch (CertificateException e) {
			return false;
		}
		return true;
	}
	
	public boolean isSignedSHA1() {
		return certificate.getSigAlgName().startsWith("SHA1");
	}
	
	public boolean isSignedMD5() {
		return certificate.getSigAlgName().startsWith("MD5");
	}
	
	public boolean isRSA() {
		return "RSA".equals(certificate.getPublicKey().getAlgorithm());
	}

	public int getRSAModulusBitLength() {
		if (this.isRSA()) {
			RSAPublicKey r = (RSAPublicKey) certificate.getPublicKey();
			return r.getModulus().bitLength();
		}
		else return -1;
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public X509Certificate getCertificate() {
		return this.certificate;
	}
	
	public boolean isError() {
		return this.error;
	}
	
	@Override
	public int hashCode() {
		return (certificate == null) ? 0 : certificate.getSerialNumber().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || getClass() != obj.getClass()) {
			return false;
		} 
		
		final CertificateAnalyzer other = (CertificateAnalyzer) obj;
		
		if (certificate == null) {
			return other.certificate == null;
		}
		
		if(certificate.getSerialNumber() == null) {
			return other.certificate.getSerialNumber() == null;
		}
		
		return certificate.getSerialNumber().equals(other.certificate.getSerialNumber());
	}
}
