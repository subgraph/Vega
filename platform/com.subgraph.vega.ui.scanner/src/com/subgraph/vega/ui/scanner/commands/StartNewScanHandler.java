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
package com.subgraph.vega.ui.scanner.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.scanner.ScanExecutor;

public class StartNewScanHandler extends AbstractHandler {
	private String lastTargetValue = null;
	private final ScanExecutor scanExecutor = new ScanExecutor();
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		lastTargetValue = scanExecutor.runScan(HandlerUtil.getActiveShell(event), lastTargetValue);
		return null;
	}

}
