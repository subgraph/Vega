package com.subgraph.vega.ui.http.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.ui.model.taggableeditor.TaggableEditorDialog;

public class TagEdit extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			final IRequestLogRecord record = (IRequestLogRecord)((IStructuredSelection) selection).getFirstElement();
			final Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			final Dialog dialog = new TaggableEditorDialog(shell, record);
			dialog.create();
			dialog.open();
		}
		return null;
	}

}
