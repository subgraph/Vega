/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.http.proxy.ssl;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509KeyManager;

/**
 * SSLContextRepository is the public interface to the subsystem which generates certificates
 * for intercepting SSL connections through the proxy.  This class will create suitable 
 * <code>SSLContext</code> instances on demand for a requested hostname, including dynamically
 * generating a validly signed SSL certificate, and caches the <code>SSLContext</code> objects
 * for future requests for SSL connections to the same hostname.
 */
public class SSLContextRepository {
	/**
	 * Creates a new instance of this class configured to persistently store the proxy CA certificate
	 * in the given directory.  If a <code>KeyStore</code> container already exists in this directory,
	 * the proxy CA certificate will be loaded from the container, otherwise a new CA certificate will 
	 * be generated and stored in a container in this directory.
	 * 
	 * @param storeDirectory The path to store a <code>KeyStore</code> containing the proxy CA certificate.
	 * @return A new instance of this class.
	 * 
	 * @throws ProxySSLInitializationException If the SSL subsystem could not be initialized, indicating that
	 *         SSL support must be disabled in the proxy.
	 */
	public static SSLContextRepository createInstance(File storeDirectory) throws ProxySSLInitializationException {
		return new SSLContextRepository(storeDirectory);
	}

	private final static Logger logger = Logger.getLogger("proxy");
	private final File storeDirectory;
	private final Map<String, SSLContext> contextMap;
	private final CertificateCreator certificateCreator;
	private CertificateStore certificateStore;
	
	/**
	 * Returns the proxy certificate authority certificate in a PEM encoded format.
	 * 
	 * @return The CA certificate for the proxy PEM encoded as a String.
	 */
	public String getCaCertificatePem() {
		return certificateCreator.getCaCertificatePem();
	}
	
	/**
	 * Returns an <code>SSLContext</code> instance for a connection identified by the
	 * specified hostname.  The returned <code>SSLContext</code> is configured to present
	 * a validly signed <code>X509Certificate</code> chain for the given hostname to the 
	 * connecting client.
	 * 
	 * @param hostname The hostname for which a signed certificate chain will be generated.
	 * 
	 * @return An <code>SSLContext</code> which is suitable for intercepting an SSL connection to
	 *         <code>hostname</code>, or <code>null</code> if an error occurs while creating a new
	 *         context.
	 *        
	 */
	public synchronized SSLContext getContextForName(String hostname) {
		if(contextMap.containsKey(hostname))
			return contextMap.get(hostname);

		try {
			final SSLContext ctx = createContextForName(hostname);
			contextMap.put(hostname, ctx);
			return ctx;
		} catch (GeneralSecurityException e) {
			logger.log(Level.WARNING, "Could not create security context for SSL connection", e);
			return null;
		}
	}

	private SSLContextRepository(File storeDirectory) throws ProxySSLInitializationException {
		this.storeDirectory = storeDirectory;
		contextMap = new HashMap<String, SSLContext>();
		certificateCreator = createCertificateCreator();
	}

	private CertificateCreator createCertificateCreator() throws ProxySSLInitializationException {
		try {
			certificateStore = new CertificateStore(storeDirectory, "foo");
			return new CertificateCreator(certificateStore);
		} catch (GeneralSecurityException e) {
			throw new ProxySSLInitializationException("Failed to create certificate creator: "+ e.getMessage(), e);
		} catch (IOException e) {
			throw new ProxySSLInitializationException("I/O error creating certificate creator: "+ e.getMessage(), e);
		}
	}

	private SSLContext createContextForName(String name) throws GeneralSecurityException {
		final HostCertificateData hostCertificateData = certificateCreator.createCertificateDataFor(name);
		final X509KeyManager km = new VegaX509KeyManager(hostCertificateData);
		final SSLContext ctx = SSLContext.getInstance("SSLv3");
		ctx.init(new KeyManager[] {km}, null, null);
		return ctx;
	}
}
