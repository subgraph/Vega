package com.subgraph.vega.application;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String INITIAL_PERSPECTIVE_ID = "com.subgraph.vega.perspectives.scanner";

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return INITIAL_PERSPECTIVE_ID;
	}

	public void initialize(final IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
	}
}
