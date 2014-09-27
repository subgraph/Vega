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

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Base64;

import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.NetscapeCertTypeExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;


/**
 * This class creates new certificates, optionally signing them with a CA certificate 
 * which is generated and stored persistently in a <code>KeyStore</code> through the 
 * <code>CertificateStore</code> class.
 */
public class CertificateCreator {
	private final static String CA_SUBJECT_NAME = "cn=Vega Proxy Certificate Authority,ou=Vega Web Vulnerability Scanner,o=Subgraph,l=Montreal,st=Quebec,c=CA";
	private final static String SIGNATURE_ALGORITHM = "SHA1withRSA";
	private final static long DEFAULT_VALIDITY = 60L * 60L * 24L * 365L * 5L * 1000L; // 5 years in microseconds

	private final CertificateStore certificateStore;
	private final KeyPairGenerator keyGenerator;
	private final Set<BigInteger> serials = new HashSet<BigInteger>();
	private final X500Principal caSubject;
	private KeyPair fixedKeyPair;

	private PrivateKey caPrivateKey;
	private PublicKey caPublicKey;
	private X509Certificate caCertificate;
	private String caPemCertificate;

	private boolean useFixedKeys = false;
	private boolean useSelfSigned = false;

	public CertificateCreator(CertificateStore certificateStore) throws CertificateException, IOException {
		this.certificateStore = certificateStore;
		keyGenerator = createKeyGenerator();
		caSubject = new X500Principal(CA_SUBJECT_NAME);
		initialize();
	}

	private void initialize() throws IOException, CertificateException {
		if(certificateStore.containsCaCertificate()) {
			caCertificate = certificateStore.getCaCertificate();
			caPrivateKey = certificateStore.getCaPrivateKey();
			caPublicKey = caCertificate.getPublicKey();
			caPemCertificate = createPemCertificate(caCertificate);
			return;
		}
		final KeyPair caKeyPair = keyGenerator.generateKeyPair();
		caPublicKey = caKeyPair.getPublic();
		caPrivateKey = caKeyPair.getPrivate();
		caCertificate = generateCertificate(caSubject, caKeyPair.getPublic(), caSubject, caPublicKey, caPrivateKey, true);
		certificateStore.saveCaCertificate(caCertificate, caPrivateKey);
		caPemCertificate = createPemCertificate(caCertificate);
	}

	private String createPemCertificate(X509Certificate certificate) throws CertificateException {
		final StringBuilder sb = new StringBuilder();
		final Base64 b64 = new Base64(64);

		sb.append("-----BEGIN CERTIFICATE-----\r\n");
		sb.append(b64.encodeToString(certificate.getEncoded()));
		sb.append("-----END CERTIFICATE-----\r\n");
		return sb.toString();
	}

	private KeyPairGenerator createKeyGenerator() throws CertificateException {
		try {
			final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			return kpg;
		} catch (NoSuchAlgorithmException e) {
			throw new CertificateException("Failed to create RSA key pair generator."+ e.getMessage());
		}
	}

	public String getCaCertificatePem() {
		return caPemCertificate;
	}

	public HostCertificateData createCertificateDataFor(String name) throws CertificateException {
		final X500Principal subject = getSubjectForCommonName(name);
		final KeyPair subjectKeys = getKeyPairForNewCertificate();
		if(useSelfSigned) {
			return createSelfSignedCertificateDataFor(subject, subjectKeys);
		} else {
			return createCaSignedCertificateDataFor(subject, subjectKeys);
		}
	}

	private HostCertificateData createCaSignedCertificateDataFor(X500Principal subject, KeyPair subjectKeys) throws CertificateException {
		final X509Certificate[] chain = new X509Certificate[2];
		chain[0] = generateCertificate(subject, subjectKeys.getPublic(), caSubject, caPublicKey, caPrivateKey, false);
		chain[1] = caCertificate;
		return new HostCertificateData(subject.getName(), subjectKeys.getPrivate(), chain);
	}

	private HostCertificateData createSelfSignedCertificateDataFor(X500Principal subject, KeyPair subjectKeys) throws CertificateException {
		final X509Certificate[] chain = new X509Certificate[1];
		chain[0] = generateCertificate(subject, subjectKeys.getPublic(), subject, subjectKeys.getPublic(), subjectKeys.getPrivate(), false);
		return new HostCertificateData(subject.getName(), subjectKeys.getPrivate(), chain);
	}

	private KeyPair getKeyPairForNewCertificate() {
		if(useFixedKeys)
			return getFixedKeyPair();
		else
			return keyGenerator.generateKeyPair();
	}

	private synchronized KeyPair getFixedKeyPair() {
		if(fixedKeyPair == null) {
			fixedKeyPair = keyGenerator.generateKeyPair();
		}
		return fixedKeyPair;
	}

	private X500Principal getSubjectForCommonName(String name) {
		return new X500Principal("cn="+ name + ",ou=Vega Generated Certificate,o=Subgraph");
	}

	private BigInteger getNextSerialNumber() {
		BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
		synchronized(serials) {
			while(serials.contains(serial))
				serial = serial.add(BigInteger.ONE);
			serials.add(serial);
			return serial;
		}
	}

	private X509Certificate generateCertificate(X500Principal subject, PublicKey subjectPublic, X500Principal issuer, PublicKey issuerPublicKey, PrivateKey issuerPrivateKey, boolean isCaCert) throws CertificateException {	
		try {
			final Date notBefore = new Date();
			final Date notAfter = new Date(notBefore.getTime() + DEFAULT_VALIDITY);
			final CertificateSigner signer = createCertificateSigner(issuer, issuerPrivateKey);
			final CertificateValidity validity = new CertificateValidity(notBefore, notAfter);
			final X509CertInfo info = createCertificateInfo(subject, subjectPublic, issuer, issuerPublicKey, validity, signer);
			final CertificateExtensions extensions = (isCaCert) ? (getCACertificateExtensions()) : (getCertificateExtensions(subjectPublic, issuerPublicKey));
			info.set(X509CertInfo.EXTENSIONS, extensions);
			final X509CertImpl cert = new X509CertImpl(info);
			cert.sign(issuerPrivateKey, SIGNATURE_ALGORITHM);
			return cert;
		} catch (Exception e) {
			throw new CertificateException("Failed to generate certificate: "+ e.getMessage(), e);
		}
	}

	private CertificateSigner createCertificateSigner(X500Principal issuer, PrivateKey issuerPrivate) throws IOException, GeneralSecurityException {
		final X500Name issuerName = new X500Name(issuer.getName());
		final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(issuerPrivate);
		return new CertificateSigner(signature, issuerName);
	}

	private X509CertInfo createCertificateInfo(X500Principal subject, PublicKey subjectPublic, X500Principal issuer, PublicKey issuerPublic, CertificateValidity validity, CertificateSigner signer) throws IOException, GeneralSecurityException {
		final BigInteger serialNumber = getNextSerialNumber();
		final X500Name subjectName = new X500Name(subject.getName());
		final X509CertInfo info = new X509CertInfo();
	    
		// Java 8 introduces more changes to the Sun X.509 classes
		// TODO: do this right
		
	    double ver = javaVersionAsFloat();

		// Add all mandatory attributes

		info.set(X509CertInfo.VERSION, new CertificateVersion(
				CertificateVersion.V3));
		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(
				serialNumber));
		AlgorithmId algID = signer.getAlgorithmId();
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(
				algID));


		if (ver >= 1.8) {
			info.set(X509CertInfo.SUBJECT, subjectName);
			info.set(X509CertInfo.ISSUER, signer.getSigner());
		} else {
			info.set(X509CertInfo.ISSUER, new CertificateIssuerName(signer.getSigner()));
			info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(subjectName));				
		}
		
		info.set(X509CertInfo.KEY, new CertificateX509Key(subjectPublic));
		info.set(X509CertInfo.VALIDITY, validity);
		
		return info;
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

	private static CertificateExtensions getCACertificateExtensions() throws IOException {
		CertificateExtensions ext = new CertificateExtensions();

		// Basic Constraints
		ext.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(
				/* isCritical */true, /* isCA */true, 0));

		return ext;
	}

	private static CertificateExtensions getCertificateExtensions(
			PublicKey pubKey, PublicKey caPubKey) throws IOException {
		CertificateExtensions ext = new CertificateExtensions();

		ext.set(SubjectKeyIdentifierExtension.NAME,
				new SubjectKeyIdentifierExtension(new KeyIdentifier(pubKey)
				.getIdentifier()));

		ext.set(AuthorityKeyIdentifierExtension.NAME,
				new AuthorityKeyIdentifierExtension(
						new KeyIdentifier(caPubKey), null, null));

		// Basic Constraints
		ext.set(BasicConstraintsExtension.NAME, new BasicConstraintsExtension(
				/* isCritical */true, /* isCA */false, /* pathLen */5));

		// Netscape Cert Type Extension
		boolean[] ncteOk = new boolean[8];
		ncteOk[0] = true; // SSL_CLIENT
		ncteOk[1] = true; // SSL_SERVER
		NetscapeCertTypeExtension ncte = new NetscapeCertTypeExtension(ncteOk);
		ncte = new NetscapeCertTypeExtension(false, ncte.getExtensionValue());
		ext.set(NetscapeCertTypeExtension.NAME, ncte);

		// Key Usage Extension
		boolean[] kueOk = new boolean[9];
		kueOk[0] = true;
		kueOk[2] = true;
		// "digitalSignature", // (0),
		// "nonRepudiation", // (1)
		// "keyEncipherment", // (2),
		// "dataEncipherment", // (3),
		// "keyAgreement", // (4),
		// "keyCertSign", // (5),
		// "cRLSign", // (6),
		// "encipherOnly", // (7),
		// "decipherOnly", // (8)
		// "contentCommitment" // also (1)
		KeyUsageExtension kue = new KeyUsageExtension(kueOk);
		ext.set(KeyUsageExtension.NAME, kue);

		// Extended Key Usage Extension
		int[] serverAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 1 };
		ObjectIdentifier serverAuthOid = new ObjectIdentifier(serverAuthOidData);
		int[] clientAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 2 };
		ObjectIdentifier clientAuthOid = new ObjectIdentifier(clientAuthOidData);
		Vector<ObjectIdentifier> v = new Vector<ObjectIdentifier>();
		v.add(serverAuthOid);
		v.add(clientAuthOid);
		ExtendedKeyUsageExtension ekue = new ExtendedKeyUsageExtension(false, v);
		ext.set(ExtendedKeyUsageExtension.NAME, ekue);

		return ext;
	}
}
