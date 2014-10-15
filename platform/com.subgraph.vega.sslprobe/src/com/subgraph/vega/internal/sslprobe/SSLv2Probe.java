package com.subgraph.vega.internal.sslprobe;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import com.subgraph.vega.internal.sslprobe.SSLServerScanResult.Flag;

public class SSLv2Probe extends ProbeBase<Void> {

	private final static Logger logger = Logger.getLogger(SSLv2Probe.class.getName());
	
	SSLv2Probe(SSLServerScanResult scanResult) {
		super(scanResult);
	}
	
	public Void runProbe() throws IOException {
		final SSL2Protocol proto = new SSL2Protocol(getInputStream(), getOutputStream());
		proto.sendClientHello(CipherSuites.getV2CipherSuites());
		final ByteBuffer msg = proto.getNextHandshakeMessage();
		if(msg != null) {
			processResponse(msg);
		} else {
			logger.warning("Got EOF receiving SSLv2 Server Hello message");
		}
		return null;
	}
	
	private void processResponse(ByteBuffer msg) {
		int type = msg.get() & 0xFF;
		if(type != SSL2Protocol.SSLV2_SERVER_HELLO) {
			logger.warning("Expecting SSLv2 Server Hello message (type = 0x04), but got type = "+ type);
			return;
		}
		
		msg.get();      // session id hit
		msg.get();      // certificate type
		msg.getShort(); // version
		int certificateLength = msg.getShort() & 0xFFFF;
		int ciphersLength = msg.getShort() & 0xFFFF;
		msg.getShort(); // connection id length

		processCertificate(msg, certificateLength);
		processCiphers(msg, ciphersLength);
	}

	private void processCertificate(ByteBuffer msg, int length) {
		final byte[] certData = new byte[length];
		msg.get(certData);
		final CertificateAnalyzer cert = new CertificateAnalyzer();
		try {
			final X509Certificate serverCertificate = X509Certificate.getInstance(certData);
			cert.addCert(serverCertificate);
		} catch (CertificateException e) {
			cert.setError(true);
		}
		scanResult.addServerCertificate(cert);

	}
	
	private void processCiphers(ByteBuffer msg, int length) {
		final List<SSLv2CipherSpec> ciphers = new ArrayList<SSLv2CipherSpec>();
		for(int n = 0; n < length; n += 3) {
			int cc = unpackCipherConstant(msg);
			SSLv2CipherSpec spec = CipherSuites.lookupSSLv2Cipher(cc);
			if(spec != null) {
				ciphers.add(spec);
			}
		}
		scanResult.setFlag(Flag.SSLV2);
		scanResult.setServerSSLv2Ciphers(ciphers);
	}
	
	private int unpackCipherConstant(ByteBuffer msg) {
		byte[] bs = new byte[3];
		msg.get(bs);
		return ((bs[0] & 0xFF) << 16) | ((bs[1] & 0xFF) << 8) | (bs[2] & 0xFF); 
	}
}


