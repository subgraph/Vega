package com.subgraph.vega.application.about;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.subgraph.vega.application.Activator;

public class AboutDialogAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private Image subgraphLogo = Activator.getImageDescriptor("icons/subgraph.png").createImage();

	@Override
	public void run(IAction action) {
		AboutDialog dialog = new AboutDialog(window.getShell());
		dialog.setTitle("About Vega");
		dialog.setTitleImage(subgraphLogo);
		dialog.open();

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;

	}
	
	

}
