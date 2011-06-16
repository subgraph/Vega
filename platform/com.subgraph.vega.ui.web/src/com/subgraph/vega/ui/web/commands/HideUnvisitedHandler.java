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
package com.subgraph.vega.ui.web.commands;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.web.views.WebsiteView;

public class HideUnvisitedHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if(event.getTrigger() instanceof Event) {
			final Event e = (Event) event.getTrigger();
			if(e.widget instanceof ToolItem) {
				ToolItem item = (ToolItem) e.widget;
				if(activePart instanceof WebsiteView) {
					((WebsiteView)activePart).setHideUnvisitedSites(item.getSelection());
				}
			}
		}
		return null;
	}
}
