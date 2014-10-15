package com.subgraph.vega.internal.sslprobe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.subgraph.vega.internal.sslprobe.SSLServerScanResult.Flag;


public class TLSServerCipherPreferenceProbe extends ProbeBase<Void> {
	private final static Logger logger = Logger.getLogger(TLSServerCipherPreferenceProbe.class.getName());

    TLSServerCipherPreferenceProbe(SSLServerScanResult scanResult) {
    	super(scanResult);
    }
	
    @Override
    public Void runProbe() {
    	final List<TLSCipherSpec> ciphers = scanResult.getServerTLSCiphers();
    	if(ciphers.size() < 2) {
    		return null;
    	}
    	
    	if(isServerPreference(ciphers)) {
    		scanResult.setFlag(Flag.SERVER_PREFERENCE);
    		final List<TLSCipherSpec> orderedCiphers = orderCiphersByServerPreference(ciphers);
    		if(orderedCiphers != null) {
    			scanResult.setServerTLSCiphersServerPreferenceOrder(orderedCiphers);
			}
    	}
    	return null;
    }

    private boolean isServerPreference(List<TLSCipherSpec> ciphers) {
  			return serverDemonstratePreference(ciphers.get(0), ciphers.get(1)) ||
					serverDemonstratePreference(ciphers.get(1), ciphers.get(0));
    }
    
    private boolean serverDemonstratePreference(TLSCipherSpec c1, TLSCipherSpec c2) {
    	closeConnection();
    	try {
    		final TLSProtocol tls = new TLSProtocol(getInputStream(), getOutputStream());
    		tls.sendTLSClientHello(Arrays.asList(c1, c2));
    		final ByteBuffer msg = tls.getNextHandshakeMessage();
    		final int c = tls.extractCipherFromServerHello(msg);
    		return c == c2.getNumber();
    	} catch (IOException e) {
    		logger.warning("I/O error sending server order preference probe: "+ e);
    	} catch (TLSAlertException e) {
    		logger.info("TLS alert received sending server order preference probe: "+ e);
    	}
    	return false;
    }
  
    private List<TLSCipherSpec> orderCiphersByServerPreference(List<TLSCipherSpec> ciphers) {
    	final List<TLSCipherSpec> preferenceList = new ArrayList<TLSCipherSpec>();
    	final List<TLSCipherSpec> workingList = new ArrayList<TLSCipherSpec>(ciphers);
    	while(!workingList.isEmpty()) {
    		TLSCipherSpec chosen = chooseCipher(workingList);
    		if(chosen == null) {
    			return null;
			} else {
				preferenceList.add(chosen);
				workingList.remove(chosen);
			}
    	}
    	return preferenceList;
    }
    
    private TLSCipherSpec chooseCipher(List<TLSCipherSpec> ciphers)  {
    	closeConnection();
    	try {
    		final TLSProtocol tls = new TLSProtocol(getInputStream(), getOutputStream());
    		tls.sendTLSClientHello(ciphers);
    		final int cipherConst = tls.extractCipherFromServerHello(tls.getNextHandshakeMessage());
    		return CipherSuites.lookupTLSCipher(cipherConst);
    	} catch (IOException e) {
    		logger.info("I/O error sending server cipher ordering probe: "+ e);
    	} catch (TLSAlertException e) {
    		logger.info("TLS alert received sending server cipher ordering probe: "+ e);
    	}
    	return null;
    }
}
