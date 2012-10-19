package com.subgraph.vega.ui.scanner.commands;

import org.eclipse.core.commands.ExecutionEvent;

import com.subgraph.vega.api.scanner.IScan;

public class UnpauseScanHandler extends AbstractScanHandler {

	@Override
	protected void runCommand(ExecutionEvent event, IScan selectedScan) {
		selectedScan.unpauseScan();
	}
}
