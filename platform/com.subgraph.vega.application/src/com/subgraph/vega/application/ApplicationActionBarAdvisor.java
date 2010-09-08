package com.subgraph.vega.application;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IContributionItem viewList;
	private IWorkbenchAction preferenceAction;
		
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	viewList = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    	preferenceAction = ActionFactory.PREFERENCES.create(window);
    	register(preferenceAction);
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager winMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
    	winMenu.add(preferenceAction);
    	
    	MenuManager viewMenu = new MenuManager("Show View");
    	viewMenu.add(viewList);
    	winMenu.add(viewMenu);
    	menuBar.add(winMenu);
    }
    
}
