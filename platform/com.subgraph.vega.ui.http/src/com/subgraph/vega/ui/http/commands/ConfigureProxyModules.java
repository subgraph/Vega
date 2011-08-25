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

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;

import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.proxy.ConfigureProxyModulesContent;
import com.subgraph.vega.ui.util.dialogs.ConfigDialogCreator;

public class ConfigureProxyModules extends AbstractHandler {
	private Window dialog;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if(dialog != null && dialog.getShell() != null) {
			dialog.close();
			dialog = null;
			return null;
		}
		List<IResponseProcessingModule> modules = Activator.getDefault().getProxyService().getResponseProcessingModules();
		dialog = ConfigDialogCreator.createDialog(event, new ConfigureProxyModulesContent(modules));
		dialog.setBlockOnOpen(true);
		dialog.open();
		return null;
	}
}
