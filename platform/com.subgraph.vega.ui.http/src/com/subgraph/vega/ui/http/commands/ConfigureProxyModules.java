package com.subgraph.vega.ui.http.commands;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;

import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;
import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.dialogs.ConfigDialogCreator;
import com.subgraph.vega.ui.http.proxy.ConfigureProxyModulesContent;

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
