package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;

public class SSLSocketFactoryPlus extends SSLSocketFactory {
	
    public SSLSocketFactoryPlus(TrustStrategy trustStrategy,
			X509HostnameVerifier hostnameVerifier)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(trustStrategy, hostnameVerifier);
		// TODO Auto-generated constructor stub
	}

	protected void prepareSocket(final SSLSocket socket) throws IOException {
	    double ver = javaVersionAsFloat();

	    if (ver < 1.8) {
	    	
	    	String[] suites = new String[] {"TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA","TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",
	    									"TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_EMPTY_RENEGOTIATION_INFO_SCSV",
	    									"TLS_RSA_WITH_AES_128_CBC_SHA"};
	    	
	    	socket.setEnabledCipherSuites(suites);
	    	
	    }
	    	
	    
    }
	
	private double javaVersionAsFloat() {
		 
		int count = 0;
		int position = 0;
		double ver = 0;
		String versionProperty = System.getProperty("java.version");

	    for (; position < versionProperty.length() && count < 2; position++) {
	        if (versionProperty.charAt(position) == '.') {
	            count++;
	        }
	    }
	    position--;

	    ver = Double.parseDouble(versionProperty.substring(0, position));
	    
	    return ver;
	    
	}
}
