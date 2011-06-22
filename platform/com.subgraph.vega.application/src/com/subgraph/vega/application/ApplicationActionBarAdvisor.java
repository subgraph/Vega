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
package com.subgraph.vega.application;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private ActionContributionItem preferenceItem;
	private ActionContributionItem quitItem;
	private ActionContributionItem resetPerspectiveItem;
	private ActionContributionItem aboutItem;
		
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	preferenceItem = createItem(ActionFactory.PREFERENCES, window);
    	quitItem = createItem(ActionFactory.QUIT, window);
    	resetPerspectiveItem = createItem(ActionFactory.RESET_PERSPECTIVE, window);
    	aboutItem = createItem(ActionFactory.ABOUT, window);
    }

    private ActionContributionItem createItem(ActionFactory factory, IWorkbenchWindow window) {
    	final IWorkbenchAction action = factory.create(window);
    	register(action);
    	return new ActionContributionItem(action);
    }
    
   
    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
    	MenuManager winMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
        
    	winMenu.add(preferenceItem);
    	
    	final Separator quitSeparator = new Separator("vega.quit");
    	fileMenu.add(quitSeparator);
    	fileMenu.add(quitItem);
    	
    	MenuManager viewMenu = new MenuManager("Show View");
    	winMenu.add(viewMenu);
    	winMenu.add(resetPerspectiveItem);
    	menuBar.add(fileMenu);
    	menuBar.insertAfter(IWorkbenchActionConstants.M_FILE, winMenu);
    	menuBar.insertAfter(IWorkbenchActionConstants.M_WINDOW, helpMenu);

    	final Separator helpSeparator = new Separator("vega.help");
    	helpMenu.add(helpSeparator);
    	helpMenu.add(aboutItem);
    	
    	if(Util.isMac()) {
    		preferenceItem.setVisible(false);
    		quitItem.setVisible(false);
    		quitSeparator.setVisible(false);
    		aboutItem.setVisible(false);
    		
    	}
    }
    
    protected void fillStatusLine(IStatusLineManager statusLine) {
    	ContributionItem statusLineItem = com.subgraph.vega.ui.http.Activator.getDefault().getStatusLineContribution();
    	statusLine.appendToGroup(StatusLineManager.END_GROUP, statusLineItem);
    }
    
}
