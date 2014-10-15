package com.subgraph.vega.internal.sslprobe;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;


public class TLSCipherProbeTask extends ProbeBase<TLSProbeResult> implements Callable<TLSProbeResult> {
	private final Logger logger = Logger.getLogger(TLSCipherProbeTask.class.getName());

	
	private List<TLSCipherSpec> partition;  
    
	TLSCipherProbeTask(SSLServerScanResult scanResult, List<TLSCipherSpec> partition) {
		super(scanResult);
		this.partition = partition;
	}
	
	@Override
	public TLSProbeResult runProbe() throws Exception {
		try {
			TLSProtocol proto = new TLSProtocol(getInputStream(), getOutputStream());
			proto.sendTLSClientHello(partition);
			return analyzeServerResponses(proto);
		} catch (TLSAlertException e) {
			final TLSProbeResult result = new TLSProbeResult();
			result.addCiphers(partition, null);
			return result;
		} catch (UnknownHostException e) {
			return createErrorResult("Unknown host");
		} catch (IOException e) {
			return createErrorResult("I/O error: "+ e);
		}
	}

	
	private TLSProbeResult createErrorResult(String message) {
		final TLSProbeResult result = new TLSProbeResult();
		result.setError(true, message);
		return result;
	}

	private TLSProbeResult analyzeServerResponses(TLSProtocol proto) throws IOException, TLSAlertException {
		final TLSProbeResult result = new TLSProbeResult();
		while(true) {
			ByteBuffer msg = proto.getNextHandshakeMessage();
			if(msg == null) {
				//result.setError(true, "EOF received reading TLS handshake message, adding to rejected list");
				addDroppedRejected(result);
				return result;
			}
			final int type = msg.get() & 0xFF;
			final int len = proto.getInt24(msg);
			if(msg.remaining() < len) {
				logger.info("Ignoring short handshake message");
			} else {
				if(analyzeHandshakeResponse(proto, type, msg, result)) {
					return result;
				}
			} 
		}
	}
	
	private boolean analyzeHandshakeResponse(TLSProtocol proto, int type, ByteBuffer msg, TLSProbeResult result) {
		switch(type) {
		case 0x02:
			analyzeServerHello(proto, msg, result);
			break;
			
		case 0x0B:
			analyzeCertificateMessage(proto, msg, result);
			break;
			
		case 0x0E:
			return true;
			
		case 0x0C:
			break;
			
		default:
			logger.info("Unexpected handshake message received with type = "+ type);
			break;
		}
		return false;
	}

	private void addDroppedRejected(TLSProbeResult result) {
		final List<TLSCipherSpec> rejected = new ArrayList<TLSCipherSpec>();
		for(TLSCipherSpec c: partition) {
				rejected.add(c);
		}
		result.addRejectedCiphersOnly(rejected);
	}
	
	private void analyzeServerHello(TLSProtocol tls, ByteBuffer msg, TLSProbeResult result) {
		if(tls.extractCompressionFromServerHello(msg) != 0) {
			result.setTLSCompressionSupport(true);
		}
		final int cipherConst = tls.extractCipherFromServerHello(msg);
		TLSCipherSpec cipher = CipherSuites.lookupTLSCipher(cipherConst);
		if(cipher == null) {
			logger.warning("Could not find cipher constant "+ cipherConst);
			return;
		}
		final List<TLSCipherSpec> rejected = new ArrayList<TLSCipherSpec>();
		for(TLSCipherSpec c: partition) {
			if(cipher != c) {
				rejected.add(c);
			}
		}
		result.addCiphers(rejected, cipher);
	}
	
	private void analyzeCertificateMessage(TLSProtocol proto, ByteBuffer msg, TLSProbeResult result) {
		final int chainLength = proto.getInt24(msg);
		if(msg.remaining() < chainLength) {
			logger.warning("Message length is less than expected length of certificate chain");
			return;
		}
		
		final CertificateAnalyzer ca = new CertificateAnalyzer();

		while(msg.hasRemaining()) {
			X509Certificate certificate = readCertificate(proto, msg);
			ca.addCert(certificate);
		}
		result.addCertificate(ca);
	}
	
	private X509Certificate readCertificate(TLSProtocol proto, ByteBuffer msg) {
		final int certLength = proto.getInt24(msg);
		byte[] certBytes = new byte[certLength];
		msg.get(certBytes);
		try {
			return X509Certificate.getInstance(certBytes);
		} catch (CertificateException e) {
			logger.log(Level.WARNING, "Error creating certificate from message bytes", e);
			return null;
		}
	}
}
