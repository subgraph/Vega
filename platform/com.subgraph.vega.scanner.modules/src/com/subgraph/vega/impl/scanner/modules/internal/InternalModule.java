package com.subgraph.vega.impl.scanner.modules.internal;

import com.subgraph.vega.api.scanner.modules.IEnableableModule;
import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRunningTime;

public class InternalModule implements IScannerModule, IEnableableModule {

	private final String categoryName;
	private final String moduleName;
	private final IScannerModuleRunningTime dummyRunningTime;
	private boolean isEnabled;
	
	InternalModule(String categoryName, String moduleName) {
		this.categoryName = categoryName;
		this.moduleName = moduleName;
		dummyRunningTime = createDummyRunningTime();
		isEnabled = true;
	}
	
	@Override
	public void setEnabled(boolean flag) {
		isEnabled = flag;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public String getModuleName() {
		return moduleName;
	}

	@Override
	public String getModuleCategoryName() {
		return categoryName;
	}

	@Override
	public IScannerModuleRunningTime getRunningTimeProfile() {
		return dummyRunningTime;
	}
	
	private IScannerModuleRunningTime createDummyRunningTime() {
		return new IScannerModuleRunningTime() {
			@Override
			public void reset() {
			}
			
			@Override
			public int getWorstTime() {
				return 0;
			}
			
			@Override
			public int getTotalTime() {
				return 0;
			}
			
			@Override
			public int getInvocationCount() {
				return 0;
			}
			
			@Override
			public double getAverageTime() {
				return 0;
			}
		};
		
	}
}
