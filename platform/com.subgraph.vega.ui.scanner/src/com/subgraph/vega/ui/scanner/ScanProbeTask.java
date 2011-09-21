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
package com.subgraph.vega.ui.scanner;

import java.net.URI;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.scanner.IScanProbeResult;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.api.scanner.IScanProbeResult.ProbeResultType;

public class ScanProbeTask implements Runnable {

	private final Shell shell;
	private final URI targetURI;
	private final IScanner scanner;
	private final IScannerConfig scannerConfig;
	
	
	ScanProbeTask(Shell shell, URI targetURI, IScanner scanner, IScannerConfig scannerConfig) {
		this.shell = shell;
		this.targetURI = targetURI;
		this.scanner = scanner;
		this.scannerConfig = scannerConfig;
	}

	@Override
	public void run() {
		final IScanProbeResult probeResult = scanner.probeTargetURI(targetURI);
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(processProbeResult(probeResult)) {
					scanner.startScanner();
				} else {
					scanner.unlock();
				}
			}
		});
	}

	private boolean processProbeResult(IScanProbeResult probeResult) {
		if(probeResult.getProbeResultType() == ProbeResultType.PROBE_CONNECT_FAILED) {
			MessageDialog.openError(shell, "Failed to connect to target", probeResult.getFailureMessage());
			return false;
		} else if(probeResult.getProbeResultType() == ProbeResultType.PROBE_REDIRECT) {
			final URI redirectURI = probeResult.getRedirectTarget();
			if(!isTrivialRedirect(targetURI, redirectURI)) {
				String message = "Target address "+ targetURI + " redirects to address "+ redirectURI + "\n\n"+
						"Would you like to scan "+ redirectURI +" instead?";
				boolean doit = MessageDialog.openQuestion(shell, "Follow Redirect?", message);
				if(!doit) {
					return false;
				}
			}
			scannerConfig.setBaseURI(probeResult.getRedirectTarget());
			return true;
		} else if(probeResult.getProbeResultType() == ProbeResultType.PROBE_REDIRECT_FAILED) {
			MessageDialog.openError(shell, "Redirect failure", probeResult.getFailureMessage());
			return false;
		}
		return true;
	}

	private boolean isTrivialRedirect(URI original, URI redirect) {
		final String originalStr = original.toString();
		if(originalStr.endsWith("/")) {
			return false;
		}
		return (redirect.toString().equals(originalStr + "/"));
	}

}
