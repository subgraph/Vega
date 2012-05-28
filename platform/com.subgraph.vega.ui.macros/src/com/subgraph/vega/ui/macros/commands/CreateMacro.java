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
package com.subgraph.vega.ui.macros.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.ui.macros.macrodialog.MacroDialog;

public class CreateMacro extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MacroDialog dialog = MacroDialog.createDialog(HandlerUtil.getActiveWorkbenchWindow(event).getShell());
		dialog.open();
		return null;
	}

}
