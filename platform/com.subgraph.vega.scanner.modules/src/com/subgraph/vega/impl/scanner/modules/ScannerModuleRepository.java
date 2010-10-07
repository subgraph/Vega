package com.subgraph.vega.impl.scanner.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.subgraph.vega.api.scanner.modules.IPerDirectoryScannerModule;
import com.subgraph.vega.api.scanner.modules.IPerHostScannerModule;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.impl.scanner.modules.perdirectory.PerDirectoryTestModule;
import com.subgraph.vega.impl.scanner.modules.perhost.PerHostTestModule;
import com.subgraph.vega.impl.scanner.modules.responses.ServerBannerModule;

public class ScannerModuleRepository implements IScannerModuleRegistry {

	static private List<IPerHostScannerModule> perHostModules;
	static private List<IPerDirectoryScannerModule> perDirectoryModules;
	static private List<IResponseProcessingModule> responseProcessingModules;
	
	static {
		perHostModules = new ArrayList<IPerHostScannerModule>();
		perHostModules.add(new PerHostTestModule());
		
		perDirectoryModules = new ArrayList<IPerDirectoryScannerModule>();
		perDirectoryModules.add(new PerDirectoryTestModule());
		
		responseProcessingModules = new ArrayList<IResponseProcessingModule>();
		responseProcessingModules.add(new ServerBannerModule());
	}
	
	@Override
	public List<IPerHostScannerModule> getPerHostModules() {
		return Collections.unmodifiableList(perHostModules);
	}

	@Override
	public List<IPerDirectoryScannerModule> getPerDirectoryModules() {
		return Collections.unmodifiableList(perDirectoryModules);
	}

	@Override
	public List<IResponseProcessingModule> getResponseProcessingModules() {
		return Collections.unmodifiableList(responseProcessingModules);
	}

}
