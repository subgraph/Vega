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

import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.scanner.IScan;
import com.subgraph.vega.api.scanner.IScanProbeResult;
import com.subgraph.vega.api.scanner.IScanProbeResult.ProbeResultType;

public class ScanProbeTask implements Runnable {

	private final Shell shell;
	private final IScan scan;
	private volatile boolean cancelScan;
	
	
	ScanProbeTask(Shell shell, IScan scan) {
		this.shell = shell;
		this.scan = scan;
	}

	@Override
	public void run() {
		final ITargetScope scanScope = scan.getConfig().getScanTargetScope();
		for(URI uri: scanScope.getScopeURIs()) {
			if(cancelScan) {
				scan.stopScan();
				return;
			}
			processTargetURI(uri);
		}
		if(cancelScan) {
			scan.stopScan();
		} else {
			scan.startScan();
		}
	}

	private void processTargetURI(final URI uri) {
		final IScanProbeResult probeResult = scan.probeTargetUri(uri);
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(!processProbeResult(uri, probeResult)) {
					cancelScan = true;
				}
			}
		});
	}

	private boolean processProbeResult(URI uri, IScanProbeResult probeResult) {
		if(probeResult.getProbeResultType() == ProbeResultType.PROBE_CONNECT_FAILED) {
			MessageDialog.openError(shell, "Failed to connect to target", probeResult.getFailureMessage());
			return false;
		} else if(probeResult.getProbeResultType() == ProbeResultType.PROBE_REDIRECT) {
			final URI redirectURI = probeResult.getRedirectTarget();
			if(!isTrivialRedirect(uri, redirectURI)) {
				String message = "Target address "+ uri + " redirects to address "+ redirectURI + "\n\n"+
						"Would you like to add "+ redirectURI +" to the scope?";
				boolean doit = MessageDialog.openQuestion(shell, "Follow Redirect?", message);
				if(!doit) {
					return false;
				}
			}
			
			// replaceScopeURI(scan.getConfig().getScanTargetScope(), uri, redirectURI);
			scan.getConfig().getScanTargetScope().addScopeURI(redirectURI);
			
			return true;
		} else if(probeResult.getProbeResultType() == ProbeResultType.PROBE_REDIRECT_FAILED) {
			MessageDialog.openError(shell, "Redirect failure", probeResult.getFailureMessage());
			return false;
		}
		return true;
	}
	
	private void replaceScopeURI(ITargetScope scope, URI remove, URI add) {
		scope.removeScopeURI(remove, false);
		scope.addScopeURI(add);
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
