package com.subgraph.vega.application;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.subgraph.vega.application.console.VegaConsoleView;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1200, 900));
        configurer.setShowCoolBar(true);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true);
        configurer.setShowFastViewBars(true);
        PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
		PlatformUI.getPreferenceStore().setValue(
				IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, "com.subgraph.vega.perspectives.scanner, com.subgraph.vega.perspectives.proxy");

		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_OPEN_ON_PERSPECTIVE_BAR, false);
		// For some reason this seems to do nothing
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_SIZE, 100);


    }
  
    @Override
    public void postWindowOpen() {
    	final IWorkbenchWindow window = getWindowConfigurer().getWindow();
    	if(window == null) {
    		return;
    	}

    	final IWorkbenchPage activePage = window.getActivePage();
    	if(activePage == null) {
    		return;
    	}

    	final IViewReference consoleReference = activePage.findViewReference(VegaConsoleView.ID);
    	if(consoleReference != null) {
    		// force activation
    		consoleReference.getView(true);
    	}
    }
}
