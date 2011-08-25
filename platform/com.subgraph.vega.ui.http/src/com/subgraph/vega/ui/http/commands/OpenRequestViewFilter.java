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
package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.window.Window;

import com.subgraph.vega.ui.http.requestfilters.RequestFilterConfigContent;
import com.subgraph.vega.ui.util.dialogs.ConfigDialogCreator;

public class OpenRequestViewFilter extends AbstractHandler implements IHandler {
	private Window dialog;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(dialog != null && dialog.getShell() != null) {
			dialog.close();
			dialog = null;
			return null;
		}
		dialog = ConfigDialogCreator.createDialog(event, new RequestFilterConfigContent());
		dialog.open();
		return null;
	}
}
