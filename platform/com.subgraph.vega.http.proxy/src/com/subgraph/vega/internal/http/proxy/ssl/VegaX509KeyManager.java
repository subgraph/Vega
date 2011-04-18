package com.subgraph.vega.internal.http.proxy.ssl;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * An <code>X509KeyManager</code> implementation which always returns a single certificate
 * chain. An instance of this class is used to initialize an <code>SSLContext</code> for an
 * intercepted SSL connection to a single host.
 *  
 *  @see SSLContext
 *  @see HostCertificateDatas
 *
 */
public class VegaX509KeyManager implements X509KeyManager {

	private final HostCertificateData data;

	public VegaX509KeyManager(HostCertificateData data) {
		this.data = data;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers,
			Socket socket) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public String chooseServerAlias(String keyType, Principal[] issuers,
			Socket socket) {
		return data.getHostname();
	}

	public X509Certificate[] getCertificateChain(String alias) {
		return copy(data.getCertificateChain());
	}

	public String[] getClientAliases(String keyType, Principal[] issuers) {
		throw new UnsupportedOperationException("Not implemented");
	}

	public PrivateKey getPrivateKey(String alias) {
		return data.getPrivateKey();
	}

	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return new String[] { data.getHostname() };
	}

	private X509Certificate[] copy(X509Certificate[] certs) {
		if (certs == null)
			return null;
		X509Certificate[] copy = new X509Certificate[certs.length];
		System.arraycopy(certs, 0, copy, 0, certs.length);
		return copy;
	}
}
