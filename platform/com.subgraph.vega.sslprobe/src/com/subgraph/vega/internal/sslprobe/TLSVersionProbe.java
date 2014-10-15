package com.subgraph.vega.internal.sslprobe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.subgraph.vega.internal.sslprobe.SSLServerScanResult.Flag;

public class TLSVersionProbe extends ProbeBase<Void> {
	private final static Logger logger = Logger.getLogger(TLSVersionProbe.class.getName());
	
	private final static byte[] VER_SSLv3  = {0x03, 0x00};
	private final static byte[] VER_TLS1   = {0x03, 0x01};
	private final static byte[] VER_TLS1_1 = {0x03, 0x02};
	private final static byte[] VER_TLS1_2 = {0x03, 0x03};
	
	private final static List<byte[]> ALL_VERSIONS = 
			Arrays.asList(VER_SSLv3, VER_TLS1, VER_TLS1_1, VER_TLS1_2);
	
	static List<TLSVersionProbe> getVersionProbes(SSLServerScanResult scanResult) {
		final List<TLSVersionProbe> probes = new ArrayList<TLSVersionProbe>();
		for(byte[] v: ALL_VERSIONS) {
			probes.add(new TLSVersionProbe(scanResult, v));
		}
		return probes;
	}
	
    private final byte[] version;
    
    TLSVersionProbe(SSLServerScanResult scanResult, byte[] version) {
    	super(scanResult);
    	this.version = version;
    }
	
    protected Void runProbe() throws IOException {
    	final TLSProtocol tls = new TLSProtocol(getInputStream(), getOutputStream());
    	tls.sendTLSClientHello(scanResult.getServerTLSCiphers(), version);
    	final byte[] responseBytes = new byte[3];
    	if(!readAll(responseBytes)) {
    		logger.warning("Failed to read response in TLSVersionProbe");
    	} else if(responseBytes[0] != TLSProtocol.TLS_RECORD_HANDSHAKE) {
    		logger.warning("Unexpected record type received in response in TLSVersionProbe.  type = "+ responseBytes[0]);
    	} else {
    		final boolean matches = responseBytes[1] == version[0] && responseBytes[2] == version[1];
    		if (matches == true) {
    			processProbeResult(matches);
    		}
    	}
    	return null;
    }
	
	private void processProbeResult(boolean supported) {
		if (version != null) {
			switch (version[1]) {
			case 0x00:
				scanResult.setFlag(Flag.SSLV3);
				break;
			case 0x01:
				scanResult.setFlag(Flag.TLS1);
				break;
			case 0x02:
				scanResult.setFlag(Flag.TLS11);
				break;
			case 0x03:
				scanResult.setFlag(Flag.TLS12);
				break;
			default:
				break;
			}
		}
	}
}


