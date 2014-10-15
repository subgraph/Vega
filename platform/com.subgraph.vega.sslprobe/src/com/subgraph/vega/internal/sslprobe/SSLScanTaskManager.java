package com.subgraph.vega.internal.sslprobe;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.subgraph.vega.internal.sslprobe.SSLServerScanResult.Flag;


public class SSLScanTaskManager {
	private final static Logger logger = Logger.getLogger(SSLScanTaskManager.class.getName());
	
	private final ExecutorService executor;
	   
	private static int CIPHERS_PER_REQUEST = 300;
	
	public SSLScanTaskManager(ExecutorService executor) {
		this.executor = executor;
	}

	public void shutdown() throws InterruptedException {
		executor.shutdown();
		executor.awaitTermination(120,  TimeUnit.SECONDS);
	}

	public SSLServerScanResult scanServer(String host, int port) throws InterruptedException {
		logger.info("Starting scan of "+ host + ":" + port);
		final SSLServerScanResult scanResult = new SSLServerScanResult(host, port);
			
		if(probeServerSupportedTLSCiphers(scanResult)) {
			probeSupportedVersions(scanResult);
			probeCipherPreference(scanResult);
		}
		
		probeSSLv2Support(scanResult);

		scanResult.waitForOutstandingProbes();
		
		return scanResult;
	}
		
	private void probeSSLv2Support(SSLServerScanResult scanResult) {
		executor.execute(new SSLv2Probe(scanResult));
	}
	
	private boolean probeServerSupportedTLSCiphers(SSLServerScanResult scanResult) {
		
		final CompletionService<TLSProbeResult> completionService = new ExecutorCompletionService<TLSProbeResult>(executor);
		
		int outstandingProbes = sendInitialCipherProbes(completionService, scanResult);

		while(outstandingProbes > 0) {
			TLSProbeResult result = getNextCompletedCipherProbe(completionService);
			if(result == null) {
				return false;
			} else if(result.isError()) {
				scanResult.setTLSProbeFailure(result.getErrorMessage());
				return false;
			}
			if(result == null || result.isError()) {
				return false;
			}
			outstandingProbes -= 1;
			if(processTLSCipherProbeResult(completionService, scanResult, result)) {
				outstandingProbes += 1;
			}
		}
		return true;
	}
	
	private int sendInitialCipherProbes(CompletionService<TLSProbeResult> cs, SSLServerScanResult scanResult) {
		int taskCount = 0;
		for(List<TLSCipherSpec> ciphers: CipherSuites.paritionTLSCiphers(CIPHERS_PER_REQUEST)) {
			taskCount += 1;
			TLSCipherProbeTask task = new TLSCipherProbeTask(scanResult, ciphers);
			cs.submit(task);
		}
		return taskCount;
	}
	
	private TLSProbeResult getNextCompletedCipherProbe(CompletionService<TLSProbeResult> cs) {
		try {
			final Future<TLSProbeResult> future = cs.take();
			return future.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch (ExecutionException e) {
			logger.warning("Unexpected exception encountered sending TLS cipher probe: "+ e.getCause());
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean processTLSCipherProbeResult(CompletionService<TLSProbeResult> completionService, SSLServerScanResult scanResult, TLSProbeResult probeResult) {
		if(probeResult.getAcceptedCipher() == null) {
			return false;
		}
		
		scanResult.addServerTLSCipher(probeResult.getAcceptedCipher());
		for(CertificateAnalyzer probeCert: probeResult.getServerCertificates()) {
			scanResult.addServerCertificate(probeCert);
		}
			
		if(probeResult.getTLSCompressionSupport()) {
			scanResult.setFlag(Flag.TLS_COMPRESSION);
		}

		if(probeResult.getRejectedCiphers().size() == 0) {
			return false;
		}
		
		final TLSCipherProbeTask task = new TLSCipherProbeTask(scanResult, probeResult.getRejectedCiphers());
		completionService.submit(task);
		return true;
	}
	
	/* Enumerate supported versions of TLS + SSLv3 */
	private void probeSupportedVersions(SSLServerScanResult scanResult) {
		final List<TLSCipherSpec> serverTLSCiphers = scanResult.getServerTLSCiphers();
		
		if (!serverTLSCiphers.isEmpty()) {
			for(Runnable task: TLSVersionProbe.getVersionProbes(scanResult)) {
				executor.execute(task);
			}
		}
	}
	
	private void probeCipherPreference(SSLServerScanResult scanResult) {
		executor.execute(new TLSServerCipherPreferenceProbe(scanResult));
	}
}
