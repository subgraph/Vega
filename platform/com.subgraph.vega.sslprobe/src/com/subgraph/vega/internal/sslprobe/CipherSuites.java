package com.subgraph.vega.internal.sslprobe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class CipherSuites {

	static private final List<TLSCipherSpec> TLS_CIPHERS = new ArrayList<TLSCipherSpec>();
	static private final Map<Integer, TLSCipherSpec> TLS_CIPHER_MAP = new HashMap<Integer, TLSCipherSpec>();
	static private final List<SSLv2CipherSpec> SSLv2_CIPHERS = new ArrayList<SSLv2CipherSpec>();
	static private final Map<Integer, SSLv2CipherSpec> SSLv2_CIPHER_MAP = new HashMap<Integer, SSLv2CipherSpec>();

	
	static public List<List<TLSCipherSpec>> paritionTLSCiphers(int maxSize) {
		return Lists.partition(TLS_CIPHERS, maxSize);
	}
	
	static TLSCipherSpec lookupTLSCipher(int constant) {
		return TLS_CIPHER_MAP.get(constant);
	}
	
	
	static public List<SSLv2CipherSpec> getV2CipherSuites() {
		return Collections.unmodifiableList(new ArrayList<SSLv2CipherSpec>(SSLv2_CIPHERS));
	}

	static public SSLv2CipherSpec lookupSSLv2Cipher(int constant) {
		return SSLv2_CIPHER_MAP.get(constant);
	}

	static private void addCipher(String name, int number, TLSCipherSpec.keyStrength strength, boolean anonDH, boolean pfs, boolean rc4) {
		final TLSCipherSpec cipherSpec = new TLSCipherSpec(name, number, strength, anonDH, pfs, rc4);
		TLS_CIPHER_MAP.put(number, cipherSpec);
		TLS_CIPHERS.add(cipherSpec);
	}
	
	static private void addCipher(String name, byte[] number, SSLv2CipherSpec.keyStrength strength, boolean rc4) {
		final SSLv2CipherSpec spec = new SSLv2CipherSpec(name, number, strength, rc4);
		SSLv2_CIPHER_MAP.put(spec.getNum(), spec);
		SSLv2_CIPHERS.add(spec);
	}
	
	
	
	static {
		
		/* Initialize cipher list: http://www.iana.org/assignments/tls-parameters/tls-parameters.xhtml */

		addCipher("TLS_NULL_WITH_NULL_NULL", 0x00, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_WITH_NULL_MD5", 0x01, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_WITH_NULL_SHA", 0x02, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_EXPORT_WITH_RC4_40_MD5", 0x03, TLSCipherSpec.keyStrength.EXPORT, false, false, true);
		addCipher("TLS_RSA_WITH_RC4_128_MD5", 0x04, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_RSA_WITH_RC4_128_SHA", 0x05, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_RSA_EXPORT_WITH_RC2_CBC_40_MD5", 0x06, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_RSA_WITH_IDEA_CBC_SHA", 0x07, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_EXPORT_WITH_DES40_CBC_SHA", 0x08, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_RSA_WITH_DES_CBC_SHA", 0x09, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_RSA_WITH_3DES_EDE_CBC_SHA", 0x0A, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_EXPORT_WITH_DES40_CBC_SHA", 0x0B, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_DH_DSS_WITH_DES_CBC_SHA", 0x0C, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_DH_DSS_WITH_3DES_EDE_CBC_SHA", 0x0D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_EXPORT_WITH_DES40_CBC_SHA", 0x0E, TLSCipherSpec.keyStrength.EXPORT, false, false,false);
		addCipher("TLS_DH_RSA_WITH_DES_CBC_SHA", 0x0F, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_DH_RSA_WITH_3DES_EDE_CBC_SHA", 0x10, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", 0x11, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_DES_CBC_SHA", 0x12, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA", 0x013, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 0x14, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_DES_CBC_SHA", 0x15, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA", 0x16, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_EXPORT_WITH_RC4_40_MD5", 0x17, TLSCipherSpec.keyStrength.EXPORT, true, false, true);
		addCipher("TLS_DH_Anon_WITH_RC4_128_MD5", 0x18, TLSCipherSpec.keyStrength.HIGH, true, false, true);
		addCipher("TLS_DH_Anon_EXPORT_WITH_DES40_CBC_SHA", 0x19, TLSCipherSpec.keyStrength.EXPORT, true, false, false);
		addCipher("TLS_DH_Anon_WITH_DES_CBC_SHA", 0x1A, TLSCipherSpec.keyStrength.LOW, true, false, false);
		addCipher("TLS_DH_Anon_WITH_3DES_EDE_CBC_SHA", 0x1B, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		addCipher("TLS_KRB5_WITH_DES_CBC_SHA", 0x1E, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_KRB5_WITH_3DES_EDE_CBC_SHA", 0x1F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_KRB5_WITH_RC4_128_SHA", 0x20, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_KRB5_WITH_IDEA_CBC_SHA", 0x21, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_KRB5_WITH_DES_CBC_MD5", 0x22, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_KRB5_WITH_3DES_EDE_CBC_MD5", 0x23, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_KRB5_WITH_RC4_128_MD5", 0x24, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_KRB5_WITH_IDEA_CBC_MD5", 0x25, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", 0x26, TLSCipherSpec.keyStrength.LOW, false, false, false);
		addCipher("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA", 0x27, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_KRB5_EXPORT_WITH_RC4_40_SHA", 0x28, TLSCipherSpec.keyStrength.EXPORT, false, false, true);
		addCipher("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", 0x29, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5", 0x2A, TLSCipherSpec.keyStrength.EXPORT, false, false, false);
		addCipher("TLS_KRB5_EXPORT_WITH_RC4_40_MD5", 0x2B, TLSCipherSpec.keyStrength.EXPORT, false, false, true);
		addCipher("TLS_PSK_WITH_NULL_SHA", 0x2C, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_NULL_SHA", 0x2D, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_NULL_SHA", 0x2E, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_WITH_AES_128_CBC_SHA", 0x2F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_AES_128_CBC_SHA", 0x30, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_AES_128_CBC_SHA", 0x31, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_AES_128_CBC_SHA", 0x32, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", 0x33, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_AES_128_CBC_SHA", 0x34, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_RSA_WITH_AES_256_CBC_SHA", 0x35, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_AES_256_CBC_SHA", 0x36, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_AES_256_CBC_SHA", 0x37, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_AES_265_CBC_SHA", 0x38, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_AES_256_CBC_SHA", 0x39, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_AES_256_CBC_SHA", 0x3A, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_RSA_WITH_NULL_SHA256", 0x3B, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_WITH_AES_128_CBC_SHA256",  0x3C, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_WITH_AES_256_CBC_SHA256", 0x3D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_AES_128_CBC_SHA256", 0x3E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_AES_128_CBC_SHA256", 0x3F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", 0x40, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA", 0x41, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA", 0x42, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA", 0x43, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA", 0x44, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA", 0x45, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_CAMELLIA_128_CBC_SHA", 0x46, TLSCipherSpec.keyStrength.HIGH, true, false, false);
	
		addCipher("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", 0x67, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_DSS_WITH_AES_256_CBC_SHA256", 0x68, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_AES_256_CBC_SHA256", 0x69, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", 0x6A, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", 0x6B, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_AES_128_CBC_SHA256", 0x6C, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_DH_Anon_WITH_AES_256_CBC_SHA256", 0x6D, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		addCipher("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA", 0x84, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA", 0x85, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA", 0x86, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA", 0x87, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA", 0x88, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_CAMELLIA_256_CBC_SHA", 0x89, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		addCipher("TLS_PSK_WITH_RC4_128_SHA", 0x8A, TLSCipherSpec.keyStrength.HIGH, false, false, true); /* pre-shared key */
		addCipher("TLS_PSK_WITH_3DES_EDE_CBC_SHA", 0x8B, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_128_CBC_SHA", 0x8C, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_256_CBC_SHA", 0x8D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_RC4_128_SHA", 0x8E, TLSCipherSpec.keyStrength.HIGH, false, true, true);
		addCipher("DHE_PSK_WITH_3DES_EDE_CBC_SHA", 0x8F, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_AES_128_CBC_SHA", 0x90, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_AES_256_CBC_SHA", 0x91, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_RSA_PSK_WITH_RC4_128_SHA", 0x92, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA", 0x93, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_AES_128_CBC_SHA", 0x94, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_AES_256_CBC_SHA", 0x95, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		
		addCipher("TLS_RSA_WITH_SEED_CBC_SHA", 0x96, TLSCipherSpec.keyStrength.HIGH, false, false, false); /* SEED / 128-bit key block cipher */
		addCipher("TLS_DH_DSS_WITH_SEED_CBC_SHA", 0x97, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_SEED_CBC_SHA", 0x98, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_SEED_CBC_SHA", 0x99, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_SEED_CBC_SHA", 0x9A, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_SEED_CBC_SHA", 0x9B, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		addCipher("TLS_RSA_WITH_AES_128_GCM_SHA256", 0x9C, TLSCipherSpec.keyStrength.HIGH, false, false, false); /* Galois counter mode */
		addCipher("TLS_RSA_WITH_AES_256_GCM_SHA384", 0x9D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", 0x9E, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", 0x9F, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_RSA_WITH_AES_128_GCM_SHA256", 0xA0, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_AES_256_GCM_SHA384", 0xA1, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", 0xA2, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", 0xA3, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_DSS_WITH_AES_128_GCM_SHA256", 0xA4, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_AES_256_GCM_SHA384", 0xA5, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_Anon_WITH_AES_128_GCM_SHA256", 0xA6, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_DH_anon_WITH_AES_256_GCM_SHA384", 0xA7, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_PSK_WITH_AES_128_GCM_SHA256", 0xA8, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_256_GCM_SHA384", 0xA9, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_AES_128_GCM_SHA256", 0xAA, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_AES_256_GCM_SHA384", 0xAB, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_RSA_PSK_WITH_AES_128_GCM_SHA256", 0xAC, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_AES_256_GCM_SHA384", 0xAD, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_128_CBC_SHA256", 0xAE, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_256_CBC_SHA384", 0xAF, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		
		addCipher("TLS_PSK_WITH_NULL_SHA256", 0xB0, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_PSK_WITH_NULL_SHA384", 0xB1, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_AES_128_CBC_SHA256", 0xB2, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_AES_256_CBC_SHA384", 0xB3, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_NULL_SHA256", 0xB4, TLSCipherSpec.keyStrength.NONE, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_NULL_SHA384", 0xB5, TLSCipherSpec.keyStrength.NONE, false, true, false);
		addCipher("TLS_RSA_PSK_WITH_AES_128_CBC_SHA256", 0xB6, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_AES_256_CBC_SHA384", 0xB7, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_NULL_SHA256", 0xB8, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_NULL_SHA384", 0xB9, TLSCipherSpec.keyStrength.NONE, false, false, false);
		
		addCipher("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256", 0xBA, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256", 0xBB, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA256", 0xBC, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256", 0xBD, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", 0xBE, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_Anon_WITH_CAMELLIA_128_CBC_SHA256", 0xBF, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA256", 0xC0, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256", 0xC1, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256", 0xC2, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256", 0xC3, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256", 0xC4, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256", 0xC5, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		/* TODO: 0xFF TLS_EMPTY_RENEGOTIATION_INFO_SCSV */
		
		addCipher("TLS_ECDH_ECDSA_WITH_NULL_SHA", 0xC001, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_RC4_128_SHA", 0xC002, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", 0xC003, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", 0xC004, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 0xC005, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_NULL_SHA", 0xC006, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", 0xC007, TLSCipherSpec.keyStrength.HIGH, false, true, true);
		addCipher("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", 0xC008, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", 0xC009, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", 0xC00A, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_NULL_SHA", 0xC00B, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_RC4_128_SHA", 0xC00C, TLSCipherSpec.keyStrength.HIGH, false, false, true);
		addCipher("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", 0xC00D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", 0xC00E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", 0xC00F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_NULL_SHA", 0xC010, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_RC4_128_SHA", 0xC011, TLSCipherSpec.keyStrength.HIGH, false, true, true);
		addCipher("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", 0xC012, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", 0xC013, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", 0xC014, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_Anon_WITH_NULL_SHA", 0xC015, TLSCipherSpec.keyStrength.NONE, true, false, false);
		addCipher("TLS_ECDH_Anon_WITH_RC4_128_SHA", 0xC016, TLSCipherSpec.keyStrength.HIGH, true, false, true);
		addCipher("TLS_ECDH_Anon_WITH_3DES_EDE_CBC_SHA", 0xC017, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_ECDH_Anon_WITH_AES_128_CBC_SHA", 0xC018, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_ECDH_Anon_WITH_AES_256_CBC_SHA", 0xC019, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_SRP_SHA_WITH_3DES_EDE_CBC_SHA", 0xC01A, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_RSA_WITH_3DES_EDE_CBC_SHA", 0xC01B, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_DSS_WITH_3DES_EDE_CBC_SHA", 0xC01C, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_WITH_AES_128_CBC_SHA", 0xC01D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_RSA_WITH_AES_128_CBC_SHA", 0xC01E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_DSS_WITH_AES_128_CBC_SHA", 0xC01F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_WITH_AES_256_CBC_SHA", 0xC020, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_RSA_WITH_AES_256_CBC_SHA", 0xC021, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_SRP_SHA_DSS_WITH_AES_256_CBC_SHA", 0xC022, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 0xC023, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", 0xC024, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 0xC025, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", 0xC026, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", 0xC027, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 0xC028, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", 0xC029, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", 0xC02A, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 0xC02B, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", 0xC02C, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", 0xC02D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", 0xC02E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", 0xC02F, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", 0xC030, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", 0xC031, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", 0xC032, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_PSK_WITH_RC4_128_SHA", 0xC033, TLSCipherSpec.keyStrength.HIGH, false, true, true);
		addCipher("TLS_ECDHE_PSK_WITH_3DES_EDE_CBC_SHA", 0xC034, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA", 0xC035, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA", 0xC036, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256", 0xC037, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA384", 0xC038, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		
		addCipher("TLS_ECDHE_PSK_WITH_NULL_SHA", 0xC039, TLSCipherSpec.keyStrength.NONE, false, false, false);    /* EC DHE/PSK + null cipher */
		addCipher("TLS_ECDHE_PSK_WITH_NULL_SHA256", 0xC03A, TLSCipherSpec.keyStrength.NONE, false, false, false);
		addCipher("TLS_ECDHE_PSK_WITH_NULL_SHA384", 0xC03B, TLSCipherSpec.keyStrength.NONE, false, false, false);
		
		addCipher("TLS_RSA_WITH_ARIA_128_CBC_SHA256", 0xC03C, TLSCipherSpec.keyStrength.HIGH, false, false, false); /* ARIA */
		addCipher("TLS_RSA_WITH_ARIA_256_CBC_SHA384", 0xC03D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_ARIA_128_CBC_SHA256", 0xC03E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_ARIA_256_CBC_SHA384", 0xC03F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_ARIA_128_CBC_SHA256", 0xC040, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_ARIA_256_CBC_SHA384", 0xC041, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_ARIA_128_CBC_SHA256", 0xC042, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_DSS_WITH_ARIA_256_CBC_SHA384", 0xC043, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_ARIA_128_CBC_SHA256", 0xC044, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_ARIA_256_CBC_SHA384", 0xC045, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		
		addCipher("TLS_DH_Anon_WITH_ARIA_128_CBC_SHA256", 0xC046, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_DH_Anon_WITH_ARIA_256_CBC_SHA384", 0xC047, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		addCipher("TLS_ECDHE_ECDSA_WITH_ARIA_128_CBC_SHA256", 0xC048, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_ARIA_256_CBC_SHA384", 0xC049, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_ECDSA_WITH_ARIA_128_CBC_SHA256", 0xC04A, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_ARIA_256_CBC_SHA384", 0xC04B, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_ARIA_128_CBC_SHA256", 0xC04C, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_ARIA_256_CBC_SHA384", 0xC04D, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_ARIA_128_CBC_SHA256", 0xC04E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_ARIA_256_CBC_SHA384", 0xC04F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_WITH_ARIA_128_GCM_SHA256", 0xC050, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_WITH_ARIA_256_GCM_SHA384", 0xC051, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_ARIA_128_GCM_SHA256", 0xC052, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_ARIA_256_GCM_SHA384", 0xC053, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_RSA_WITH_ARIA_128_GCM_SHA256", 0xC054, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_ARIA_256_GCM_SHA384", 0xC055, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_ARIA_128_GCM_SHA256", 0xC056, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_DSS_WITH_ARIA_256_GCM_SHA384", 0xC057, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_DSS_WITH_ARIA_128_GCM_SHA256", 0xC058, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_ARIA_256_GCM_SHA384", 0xC059, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		
		addCipher("TLS_DH_Anon_WITH_ARIA_128_GCM_SHA256", 0xC05A, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_DH_Anon_WITH_ARIA_256_GCM_SHA384", 0xC05B, TLSCipherSpec.keyStrength.HIGH, true, false, false);
	
		addCipher("TLS_ECDHE_ECDSA_WITH_ARIA_128_GCM_SHA256", 0xC05C, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_ARIA_256_GCM_SHA384", 0xC05D, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_ECDSA_WITH_ARIA_128_GCM_SHA256", 0xC05E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_ARIA_256_GCM_SHA384", 0xC05F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_ARIA_128_GCM_SHA256", 0xC060, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_ARIA_256_GCM_SHA384", 0xC061, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_ARIA_128_GCM_SHA256", 0xC062, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_ARIA_256_GCM_SHA384", 0xC063, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_ARIA_128_CBC_SHA256", 0xC064, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_ARIA_256_CBC_SHA384", 0xC065, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_ARIA_128_CBC_SHA256", 0xC066, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_ARIA_256_CBC_SHA384", 0xC067, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_RSA_PSK_WITH_ARIA_128_CBC_SHA256", 0xC068, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_ARIA_256_CBC_SHA384", 0xC069, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_ARIA_128_GCM_SHA256", 0xC06A, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_ARIA_256_GCM_SHA384", 0xC06B, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_ARIA_128_GCM_SHA256", 0xC06C, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_ARIA_256_GCM_SHA384", 0xC06D, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_RSA_PSK_WITH_ARIA_128_GCM_SHA256", 0xC06E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_ARIA_256_GCM_SHA384", 0xC06F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_PSK_WITH_ARIA_128_CBC_SHA256", 0xC070, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_PSK_WITH_ARIA_256_CBC_SHA384", 0xC071, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		
		addCipher("TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_CBC_SHA256", 0xC072, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_CBC_SHA384", 0xC073, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_ECDSA_WITH_CAMELLIA_128_CBC_SHA256", 0xC074, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_CAMELLIA_256_CBC_SHA384", 0xC075, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", 0xc076, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_CAMELLIA_256_CBC_SHA384", 0xC077, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_CAMELLIA_128_CBC_SHA256", 0xC078, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_CAMELLIA_256_CBC_SHA384", 0xC079, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC07A, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC07B, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC07C, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC07D, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_RSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC07E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_RSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC07F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_DSS_WITH_CAMELLIA_128_GCM_SHA256", 0xC080, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_DSS_WITH_CAMELLIA_256_GCM_SHA384", 0xC081, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DH_DSS_WITH_CAMELLIA_128_GCM_SHA256", 0xC082, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DH_DSS_WITH_CAMELLIA_256_GCM_SHA384", 0xC083, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		
		addCipher("TLS_DH_Anon_WITH_CAMELLIA_128_GCM_SHA256", 0xC084, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		addCipher("TLS_DH_Anon_WITH_CAMELLIA_256_GCM_SHA384", 0xC085, TLSCipherSpec.keyStrength.HIGH, true, false, false);
		
		addCipher("TLS_ECDHE_ECDSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC086, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_ECDSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC087, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_ECDSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC088, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_ECDSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC089, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_RSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC08A, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_RSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC08B, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDH_RSA_WITH_CAMELLIA_128_GCM_SHA256", 0xC08C, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDH_RSA_WITH_CAMELLIA_256_GCM_SHA384", 0xC08D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_CAMELLIA_128_GCM_SHA256", 0xC08E, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_CAMELLIA_256_GCM_SHA384", 0xC08F, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_CAMELLIA_128_GCM_SHA256", 0xC090, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_CAMELLIA_256_GCM_SHA384", 0xC091, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_RSA_PSK_WITH_CAMELLIA_128_GCM_SHA256", 0xC092, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_CAMELLIA_256_GCM_SHA384", 0xC093, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_CAMELLIA_128_CBC_SHA256", 0xC094, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_CAMELLIA_256_CBC_SHA384", 0xC095, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_CAMELLIA_128_CBC_SHA256", 0xC096, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_CAMELLIA_256_CBC_SHA384", 0xC097, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_CAMELLIA_128_CBC_SHA256", 0xC098, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_PSK_WITH_CAMELLIA_256_CBC_SHA384", 0xC099, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_ECDHE_PSK_WITH_CAMELLIA_128_CBC_SHA256", 0xC09A, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_ECDHE_PSK_WITH_CAMELLIA_256_CBC_SHA384", 0xC09B, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		
		addCipher("TLS_RSA_WITH_AES_128_CCM", 0xC09C, TLSCipherSpec.keyStrength.HIGH, false, false, false); /* AES CCM */
		addCipher("TLS_RSA_WITH_AES_256_CCM", 0xC09D, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_AES_128_CCM", 0xC09E, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_AES_256_CCM", 0xC09F, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		
		addCipher("TLS_RSA_WITH_AES_128_CCM_8", 0xC0A0, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_RSA_WITH_AES_256_CCM_8", 0xC0A1, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_RSA_WITH_AES_128_CCM_8", 0xC0A2, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_RSA_WITH_AES_256_CCM_8", 0xC0A3, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_PSK_WITH_AES_128_CCM", 0xC0A4, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_256_CCM", 0xC0A5, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_DHE_PSK_WITH_AES_128_CCM", 0xC0A6, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_DHE_PSK_WITH_AES_256_CCM", 0xC0A7, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_PSK_WITH_AES_128_CCM_8", 0xC0A8, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_WITH_AES_256_CCM_8", 0xC0A9, TLSCipherSpec.keyStrength.HIGH, false, false, false);
		addCipher("TLS_PSK_DHE_WITH_AES_128_CCM_8", 0xC0AA, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		addCipher("TLS_PSK_DHE_WITH_AES_256_CCM_8", 0xC0AB, TLSCipherSpec.keyStrength.HIGH, false, true, false);
		
		/* 0xC0, 0xAC-0xFF unassigned */
		/* 0xC1-FD, * unassigned */
		/* 0xFE, 0xFE-FF reserved to avoid conflicts */
		/* 0xFF, 0x00-FF reserved for private use */
	    addCipher("SSL2_DES_192_EDE3_CBC_WITH_MD5", new byte[]{0x07, 0x00, (byte)0xc0}, SSLv2CipherSpec.keyStrength.HIGH, false);
	    addCipher("SSL2_DES_192_EDE3_CBC_WITH_SHA", new byte[]{0x07, 0x01, (byte)0xc0}, SSLv2CipherSpec.keyStrength.HIGH, false);
	    addCipher("SSL2_DES_64_CBC_WITH_MD5", new byte[]{0x06, 0x00, 0x40}, SSLv2CipherSpec.keyStrength.LOW, false);
	    addCipher("SSL2_DES_64_CBC_WITH_SHA", new byte[]{0x06, 0x01, 0x40}, SSLv2CipherSpec.keyStrength.LOW, false);
	    addCipher("SSL2_IDEA_128_CBC_WITH_MD5", new byte[]{0x05, 0x00, (byte) 0x80}, SSLv2CipherSpec.keyStrength.HIGH, false);
	    addCipher("SSL2_RC2_CBC_WITH_MD5", new byte[]{0x03, 0x00, (byte) 0x80}, SSLv2CipherSpec.keyStrength.HIGH, false);
	    addCipher("SSL2_RC4_128_WITH_MD5", new byte[]{0x01, 0x00, (byte) 0x80}, SSLv2CipherSpec.keyStrength.HIGH, true);
	    addCipher("SSL2_DES_64_CBC_WITH_MD5", new byte[]{0x06, 0x00, 0x40}, SSLv2CipherSpec.keyStrength.LOW, false);
	    addCipher("SSL2_RC2_CBC_128_CBC_WITH_MD5", new byte[]{0x04, 0x00, (byte)0x80}, SSLv2CipherSpec.keyStrength.HIGH, false);
	    addCipher("SSL2_RC4_128_EXPORT40_WITH_MD5", new byte[]{0x02, 0x00, (byte)0x80}, SSLv2CipherSpec.keyStrength.EXPORT, true);
	    addCipher("SSL2_NULL_WITH_MD5", new byte[]{0x00, 0x00, 0x00}, SSLv2CipherSpec.keyStrength.NONE, false);
	    addCipher("SSL2_NULL", new byte[]{(byte)0xff, 0x08, 0x10}, SSLv2CipherSpec.keyStrength.NONE, false);
	    addCipher("SSL2_DES_64_CFB64_WITH_MD5_1", new byte[]{(byte)0xff, 0x08, 0x00}, SSLv2CipherSpec.keyStrength.LOW, false);
	    addCipher("SSL2_RC4_64_WITH_MD5", new byte[]{0x08, 0x00, (byte)0x80}, SSLv2CipherSpec.keyStrength.LOW, true);
	}
}
