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
package com.subgraph.vega.application.workspaces;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;

import com.subgraph.vega.application.Activator;

public class NewWorkspaceWizard extends Wizard {
	private WorkspaceNamePage firstPage;
	private LastPage lastPage;
	private WorkspaceRecord workspaceRecord;
	private boolean restart = true;
	
	public void addPages() {
		setWindowTitle("Create a new Workspace");
		
		ImageDescriptor image = Activator.getImageDescriptor("icons/workspace_wiz.gif");

		firstPage = new WorkspaceNamePage();
		firstPage.setImageDescriptor(image);
		
		lastPage = new LastPage();
		lastPage.setImageDescriptor(image);
		
		addPage(firstPage);
		addPage(lastPage);
	}
	
	public boolean canFinish() {
		return getContainer().getCurrentPage() == lastPage;
	}
	
	@Override
	public boolean performFinish() {
		final String name = firstPage.getWorkspaceName();
		setWorkspaceRecord(WorkspaceChooser.createWorkspace(name, isRestart()));
		return true;
	}

	public void setWorkspaceRecord(WorkspaceRecord workspaceRecord) {
		this.workspaceRecord = workspaceRecord;
	}

	public WorkspaceRecord getWorkspaceRecord() {
		return workspaceRecord;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}

	public boolean isRestart() {
		return restart;
	}
}
