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

import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.api.scanner.IScanProbeResult;
import com.subgraph.vega.api.scanner.IScanProbeResult.ProbeResultType;

public class ScanProbeTask implements Runnable {

	private final Shell shell;
	private final URI targetURI;
	private final IScan scan;
	
	
	ScanProbeTask(Shell shell, URI targetURI, IScan scan) {
		this.shell = shell;
		this.targetURI = targetURI;
		this.scan = scan;
	}

	@Override
	public void run() {
		final IScanProbeResult probeResult = scan.probeTargetUri(targetURI);
		shell.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(processProbeResult(probeResult)) {
					scan.startScan();
				} else {
					scan.stopScan();
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
			
			return true;
		} else if(probeResult.getProbeResultType() == ProbeResultType.PROBE_REDIRECT_FAILED) {
			MessageDialog.openError(shell, "Redirect failure", probeResult.getFailureMessage());
			return false;
		}
		return true;
	}

	private boolean isTrivialRedirect(URI original, URI redirect) {
		final String originalStr = original.toString();
		/* Do we ask the user or not? I will assume yes for now
		 * if (original.getHost().equals(redirect.getHost()) && (original.getPort()  == redirect.getPort())) {
			return true;
		}
		*/
		if(originalStr.endsWith("/")) {
			return false;
		}
		return (redirect.toString().equals(originalStr + "/"));
	}

}
