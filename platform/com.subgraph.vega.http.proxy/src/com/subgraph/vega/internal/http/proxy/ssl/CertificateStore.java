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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * A CertificateStore object manages creation of and access to a KeyStore
 * object which stores an authority certificate which will be used to sign
 * certificates which are used to intercept SSL connections made though
 * the proxy. 
 */
public class CertificateStore {
	private final static String STORE_FILENAME = "ca.p12";
	private final static String STORE_TYPE = "PKCS12";
	private final static String STORE_KEY = "CA";

	private final File storeFile;

	private final char[] password;
	private final KeyStore keyStore;
	private PrivateKey caPrivateKey;
	private X509Certificate caCertificate;

	CertificateStore(File storageDirectory, String password) throws GeneralSecurityException, IOException {
		storeFile = new File(storageDirectory, STORE_FILENAME);
		this.password = password.toCharArray();
		keyStore = KeyStore.getInstance(STORE_TYPE);
		initKeyStore();
	}

	private void initKeyStore() throws GeneralSecurityException, IOException {
		if(!storeFile.exists() || !loadFromFile()) {
			keyStore.load(null, password);
		}
	}

	private boolean loadFromFile() throws GeneralSecurityException, IOException {
		InputStream input = null;
		try {
			input = new FileInputStream(storeFile);
			keyStore.load(input, password);
			caPrivateKey = (PrivateKey) keyStore.getKey(STORE_KEY, password);
			final Certificate[] chain = keyStore.getCertificateChain(STORE_KEY);
			caCertificate = (X509Certificate) chain[0];
			return true;
		} catch (FileNotFoundException e) {
			return false;
		} finally {
			if(input != null)
				input.close();
		}
	}

	public boolean containsCaCertificate() {
		return (caPrivateKey != null && caCertificate != null);		
	}

	public PrivateKey getCaPrivateKey() {
		return caPrivateKey;
	}

	public X509Certificate getCaCertificate() {
		return caCertificate;
	}

	public void saveCaCertificate(X509Certificate certificate, PrivateKey privateKey) throws CertificateException {
		final Certificate[] chain = new Certificate[1];
		chain[0] = certificate;
		try {
			keyStore.setKeyEntry(STORE_KEY, privateKey, password, chain);
			final OutputStream output = new FileOutputStream(storeFile);
			writeKeyStore(output);
			storeFile.setWritable(false, false);
			storeFile.setReadable(false, false);
			storeFile.setReadable(true, true);
			caCertificate = certificate;
			caPrivateKey = privateKey;
		} catch (KeyStoreException e) {
			throw new CertificateException("Failed to store CA certificate in key store: "+ e.getMessage());
		} catch (FileNotFoundException e) {
			throw new CertificateException("Could not open key store file '" + storeFile + "' for writing.");
		}
	}

	private void writeKeyStore(OutputStream output) throws CertificateException {
		try {
			keyStore.store(output, password);
		} catch (KeyStoreException e) {
			throw new CertificateException("Failed to store CA certificate in key store: "+ e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new CertificateException("Failed to find algorithm for serializing certificate data: "+ e.getMessage());
		} catch (CertificateException e) {
			throw new CertificateException("Attempt to store invalid certificate: "+ e.getMessage());
		} catch (IOException e) {
			throw new CertificateException("I/O error writing to certificate store file: "+ e.getMessage());
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				throw new CertificateException("I/O error closing certificate store", e);
			}
		}
	}
}
