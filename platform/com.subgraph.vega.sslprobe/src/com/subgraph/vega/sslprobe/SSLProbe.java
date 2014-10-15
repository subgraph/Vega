package com.subgraph.vega.sslprobe;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.internal.sslprobe.CertificateAnalyzer;
import com.subgraph.vega.internal.sslprobe.SSLScanTaskManager;
import com.subgraph.vega.internal.sslprobe.SSLServerScanResult;
import com.subgraph.vega.internal.sslprobe.SSLv2CipherSpec;
import com.subgraph.vega.internal.sslprobe.TLSCipherSpec;
import com.subgraph.vega.internal.sslprobe.SSLServerScanResult.Flag;

public class SSLProbe implements Runnable {
	
	private final int NTHREADS = 10;
    private final ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
    private SSLServerScanResult result;
    
    private final String host;
    private final int port;
    private final IScanInstance scanInstance;
    private final String hostString;
    
	private final Logger logger = Logger.getLogger("scanner");

    
    public SSLProbe(IScanInstance instance, String hostname, int serverPort, String httpHostString) {
    	host = hostname;
    	port = serverPort;
    	scanInstance = instance;
    	hostString = httpHostString;
    }
    
    public void run() {
    
    	SSLScanTaskManager taskmanager = new SSLScanTaskManager(executor);
    	
    	try {
    		result = taskmanager.scanServer(host, port);
    		summarizeResults(result);
			taskmanager.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
 
    }
    
    private void summarizeResults(SSLServerScanResult res) {
    	
    	String output = "SSL Server Configuration Probe\n";
    	
    	output += "Target: "+res.getTargetHost() + ":" + res.getTargetPort() + "\n\n";
    	
    	if(res.getTLSProbeFailure()) {
    		output += "Scan Failed ("+ res.getErrorMessage() + ")\n";
    	//	return;
    	}
    
    	output += "SSL/TLS Version Enumeration\n";
    	output += "---------------------------------------------------------\n";

    	boolean val = res.isSet(Flag.SSLV2);
    	if (val) {
    		
    		/* Alert here */
    		
    		IScanAlert alert = scanInstance.createAlert("ssl-v2-support", host+"-sslv2");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    		output += "  [ BAD ]  SSL v2 Support: "+val + "\n";
    		
    	} else {
    		output += "  [ GOOD ]  SSL v2 Support: "+ val + "\n";
    	}
    	val = res.isSet(Flag.SSLV3);
    	if (val) {
    		output += "  [ BAD ]  SSL v3 Support: "+ val + "\n";
    		IScanAlert alert = scanInstance.createAlert("ssl-v3-support",host + "-sslv3");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	} else {
    		output += "  [ GOOD ]  SSL v3 Support: "+ val + "\n";
    	}
    	output += "  [ INFO ] TLS 1.0 Support: "+res.isSet(Flag.TLS1) + "\n";
    	output += "  [ INFO ] TLS 1.1 Support: "+res.isSet(Flag.TLS11) + "\n";
    	output += "  [ INFO ] TLS 1.2 Support: "+res.isSet(Flag.TLS12) + "\n";
    	
    	output += "\nTLS Configuration Summary\n";
    	output += "--------------------------------------------------------\n";
    	
    	if (res.isSet(Flag.TLS_COMPRESSION)) {
    		output += "  [ BAD ]  TLS compression supported (CRIME attack).\n";
    		IScanAlert alert = scanInstance.createAlert("ssl-compression",host + "-compression");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	} else  {
    		output += "  [ GOOD ] TLS compression not supported.\n";
    	}
    	if (res.isSet(Flag.ANON_DH)) {
    		output += "  [ INFO ] Supported cipher spec found with Anon-DH key exchange.\n";
    		IScanAlert alert = scanInstance.createAlert("ssl-anonymous-dh",host + "-anonymous-dh");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	}

    	if (res.isSet(Flag.RC4_SUPPORTED)) {
    		output += "  [ INFO ] Supported ciphers using RC4 found (common).\n";
    	}
    	
    	/* Certificate analyzer results */
    	
    	if (res.isSet(Flag.SMALL_RSA_KEY)) {
    		output += "  [ BAD ]  Small RSA modulus (<= 1024 bit) found in a server certificate.\n";
    		IScanAlert alert = scanInstance.createAlert("ssl-small-key",host + "-small-key");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	}
    	if (res.isSet(Flag.SELF_SIGNED_CERTIFICATE)) {
    		output += "  [ INFO ] Self-signed certificate found.\n";
    		IScanAlert alert = scanInstance.createAlert("ssl-self-signed",host + "-self-signed");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	}
    	
    	if (res.isSet(Flag.MD5_SIGNED_CERTIFICATE)) {
    		output += "  [ BAD ] Certificate signed using MD5. \n";
    		IScanAlert alert = scanInstance.createAlert("ssl-md5-cert", host + "-md5-cert");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	}
    	
    	if (res.isSet(Flag.SHA1_SIGNED_CERTIFICATE)) {
    		output += "  [ BAD ] Certificate signed using SHA1. \n";
    		IScanAlert alert = scanInstance.createAlert("ssl-sha1-cert", host + "-sha1-cert");
    		alert.setDiscretionaryHostname(hostString);
    		alert.setResource(hostString);
    		scanInstance.addAlert(alert);
    	}
    	
    	/* End certificate analyzer results */
    	
    	if (res.isSet(Flag.SERVER_PREFERENCE)) {
    		output += "\n\n  [ GOOD ] Server cipher spec preference detected. Server preference order: \n";
    		int i = 1;
    		for (TLSCipherSpec c : res.getServerTLSCiphersServerPreferenceOrder()) {
    			output += "    ["+i+"] "+c.getName() + "\n";
    			if (c.getStrength() == TLSCipherSpec.keyStrength.EXPORT) {
    				output += "      [ BAD ] Cipher spec is export grade.\n";
    			} else if (c.getStrength() == TLSCipherSpec.keyStrength.LOW) {
    				output += "      [ BAD ] Cipher spec is low security.\n";
    			} else if (c.getStrength() == TLSCipherSpec.keyStrength.NONE) {
    				output += "      [ BAD ] Cipher spec does not include encryption.\n";
    			}
    			if (c.isAnonDH() == true) {
    				output += "      [ BAD ] Cipher spec supports Anonymous DH.\n";    				
    			}
    			if (c.isPFS() == true) {
    				output += "      [ GOOD ] Cipher spec offers PFS.\n";    				
    			}
    			if (c.isRC4() == true) {
    				output += "      [ BAD ] Cipher spec uses RC4.\n";    				
    			}
    			
    			i++;
    		}
    		if (res.getServerTLSCiphersServerPreferenceOrder().get(0).isPFS() == false) {
    			output += "  [ BAD ]  Server most preferred cipher does not offer PFS: "+res.getServerTLSCiphersServerPreferenceOrder().get(0).getName() + "\n";
        		IScanAlert alert = scanInstance.createAlert("ssl-pfs-not-preferred",host + "-pfs-not-preferred");
        		alert.setDiscretionaryHostname(hostString);
        		alert.setResource(hostString);
        		scanInstance.addAlert(alert);
    		} else if (res.getServerTLSCiphersServerPreferenceOrder().get(0).isPFS() == true) {
    			output += "  [ GOOD ] Server most preferred cipher offers PFS: "+res.getServerTLSCiphersServerPreferenceOrder().get(0).getName() + "\n";
    		}
    		if (res.getServerTLSCiphersServerPreferenceOrder().get(0).isRC4() == true) {
    			output += "  [ BAD ]  Server most preferred cipher uses RC4: "+res.getServerTLSCiphersServerPreferenceOrder().get(0).getName() + "\n";
        		IScanAlert alert = scanInstance.createAlert("ssl-rc4-preference",host + "-rc4-preference");
        		alert.setResource(hostString);
        		alert.setDiscretionaryHostname(hostString);
        		scanInstance.addAlert(alert);
    		}
    		
    	} else
    	{
    		output += "\n  [ BAD ]  Client cipher spec preference detected. Supported cipher specs:\n";
    		if (res.getServerTLSCiphers().isEmpty() == false) {
    			
    			IScanAlert clientPreferenceAlert = scanInstance.createAlert("ssl-client-preference",host + "-client-preference");
    			clientPreferenceAlert.setDiscretionaryHostname(hostString);
        		clientPreferenceAlert.setResource(hostString);
    			scanInstance.addAlert(clientPreferenceAlert);
    			
    			if (res.isSet(Flag.PFS_SUPPORTED)) {
    	    		output += "  [ GOOD ]  Ciphers offering PFS supported.\n";
    	    	} else {
    	    		output += "  [ BAD ]  No ciphers offering PFS supported.\n";
    	    		IScanAlert noPFSAlert = scanInstance.createAlert("ssl-no-pfs", host + "-no-pfs");
    	    		noPFSAlert.setDiscretionaryHostname(hostString);
    	    		noPFSAlert.setResource(hostString);
    	    		scanInstance.addAlert(noPFSAlert);
    	    	}
    			for (TLSCipherSpec c : res.getServerTLSCiphers()) {
    				output += "    [*] "+c.getName() + "\n";
    				if (c.getStrength() == TLSCipherSpec.keyStrength.EXPORT) {
    					output += "      [ BAD ] Cipher spec is export grade.\n";
    				} else if (c.getStrength() == TLSCipherSpec.keyStrength.LOW) {
    					output += "      [ BAD ] Cipher spec is low security.\n";
    				} else if (c.getStrength() == TLSCipherSpec.keyStrength.NONE) {
    					output += "      [ BAD ] Cipher spec does not include encryption.\n";
    				}
    				if (c.isAnonDH() == true) {
    					output += "      [ BAD ] Cipher spec supports Anonymous DH.\n";    				
    				}
    				if (c.isPFS() == true) {
    					output += "      [ GOOD ] Cipher spec offers PFS.\n";    				
    				}
    				if (c.isRC4() == true) {
    					output += "      [ BAD ] Cipher spec uses RC4.\n";    				
    				}
    			}
    		}
    		
    	} if (res.isSet(Flag.SSLV2)) {
    		output += "\nSSLv2 Ciphers Supported:\n";
    		for (SSLv2CipherSpec c : res.getServerSSLv2Ciphers()) {
    			output += "    [*] "+c.getName() + "\n";
    			if (c.getStrength() == SSLv2CipherSpec.keyStrength.EXPORT) {
    				output += "      [ BAD ] Cipher spec is export grade.\n";
    			} else if (c.getStrength() == SSLv2CipherSpec.keyStrength.LOW) {
    				output += "      [ BAD ] Cipher spec is low security.\n";
    			} else if (c.getStrength() == SSLv2CipherSpec.keyStrength.NONE) {
    				output += "      [ BAD ] Cipher spec does not include encryption.\n";
    			}
    			if (c.isRC4() == true) {
    				output += "      [ BAD ] Cipher spec uses RC4.\n";    				
    			}
    			
    		}
    	}
    	
    	output += "\nCertificate Analysis\n";
    	output += "--------------------------------------------------------\n";
    	
        for (CertificateAnalyzer ca : res.getServerCertificates()) {
        	output += "  [*] Certificate: "+ca.getCertificate().getSerialNumber() + "\n";
        	if (ca.isRSA()) {
        		if (ca.getRSAModulusBitLength() <= 1024) {
        			output += "    [ BAD ]  RSA modulus <= 1024 found.\n";
        		}
        		else {
        			output += "    [ GOOD ] RSA modulus size: " +ca.getRSAModulusBitLength() + " bits.\n";
        		}
        	}
        	if (ca.selfSigned()) {
        		output += "    [ INFO ] Self-signed certificate found.\n";
        	} else {
        		output += "    [ INFO ] Not self signed.\n";
        	}
        }
        output += "\nSSL Probe completed.\n";
        logger.log(Level.INFO, output);
    }
}
	
	