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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WorkspaceChooser {
	private final static String NAME_FILE = ".name";
	private final static String AUTOSTART_FILE = ".autostart";


	public WorkspaceChooser() {
	}

	public URL choose() {

		final List<WorkspaceRecord> workspaces = findAllWorkspaces();
		WorkspaceRecord workspaceRecord;

		/* only one workspace open it */
		if(workspaces.size() == 1) {
			workspaceRecord = workspaces.get(0);
		}
		/* no workspace create default */
		else if(workspaces.isEmpty()) {
			workspaceRecord = createWorkspace("Default", false);
		}
		/* more than one select one marked as auto-start */
		else {
			workspaceRecord = getAutoStartWorkspace(workspaces);
		}
		/* more than one and no auto-start */
		if(workspaceRecord == null) {
			/* show the dialog with the list of workspaces to select */
			workspaceRecord = SwitchWorkspaceHandler.openChoseWorkspaceDialog(false);
		}
		
		if(workspaceRecord != null) {
			return workspaceRecord.getURL();
		}

		return null;
	}

	public WorkspaceRecord getAutoStartWorkspace(List<WorkspaceRecord> workspaces) {
		WorkspaceRecord autoStartWorkspace = null;

		for(WorkspaceRecord rec: workspaces) {
			if(rec.isAutostart()) {

				/* return the first autos-tart workspace */
				if(autoStartWorkspace == null) {
					autoStartWorkspace = rec;
				}
				/* but unlink all of them */
				if(!unlinkAutostart(rec.getPath())) {
					// XXX
				}
			}

		}
		return autoStartWorkspace;
	}

	public List<WorkspaceRecord> findAllWorkspaces() {
		final File root = getBaseDirectory();
		List<WorkspaceRecord> workspaces = new ArrayList<WorkspaceRecord>();
		for(File dir : root.listFiles()) {
			if(isWorkspace(dir)) {
				workspaces.add(new WorkspaceRecord(getWorkspaceName(dir), dir, isAutostart(dir)));
			}
		}
		return workspaces;		
	}

	private boolean isWorkspace(File path) {
		final File nameFile = new File(path, NAME_FILE);
		return nameFile.exists();	
	}

	private boolean isAutostart(File path) {
		final File autoStart = new File(path, AUTOSTART_FILE);
		return autoStart.exists();
	}

	private boolean unlinkAutostart(File path) {
		final File autoStart = new File(path, AUTOSTART_FILE);
		return autoStart.delete();
	}

	private String getWorkspaceName(File path) {
		final File nameFile = new File(path, NAME_FILE);
		try {
			return readWorkspaceName(nameFile);
		} catch (IOException e) {
			throw new WorkspaceOpenException("Error reading workspace name file : " + e.getMessage());
		}
	}

	private String readWorkspaceName(File nameFile) throws IOException {
		final FileReader reader = new FileReader(nameFile);
		final BufferedReader input = new BufferedReader(reader);
		try {
			return input.readLine();
		} finally {
			try { input.close(); } catch (IOException ignored) { }
		}
	}

	static public File getBaseDirectory() {
		final File homeDirectory = new File(System.getProperty("user.home"));

		if(!homeDirectory.canWrite()) 
			throw new WorkspaceOpenException("Cannot write to home directory " + homeDirectory.getPath());

		final File baseDirectory = new File(homeDirectory, ".vega"); // FIXME Unix
		if(!baseDirectory.exists()) {
			if(!baseDirectory.mkdir()) {
				throw new WorkspaceOpenException("Could not create directory: " + baseDirectory.getPath());
			}
		}
		return baseDirectory;
	}

	static public WorkspaceRecord createWorkspace(String workspaceName, boolean autostart) {
		final File baseDirectory = getBaseDirectory();
		int i = 0;
		File workspaceDirectory;
		while(true) {
			workspaceDirectory = new File(baseDirectory, String.format("%02d", i));
			if(!workspaceDirectory.exists())
				break;
			i++;
		}
		if(!workspaceDirectory.mkdir()) {
			throw new WorkspaceOpenException("Could not create directory: " + workspaceDirectory.getPath());
		}

		try {
			writeNameFile(workspaceDirectory, workspaceName);
		} catch (IOException e) {
			throw new WorkspaceOpenException("Error writing name file to workspace directory " + e.getMessage());
		}

		if(autostart) {
			markAutostart(workspaceDirectory);
		}
		return new WorkspaceRecord(workspaceName, workspaceDirectory, autostart);
	}

	static public void markAutostart(File workspacePath) {
		final File autostartFile = new File(workspacePath, AUTOSTART_FILE);
		if(!autostartFile.exists() && !autostartFile.mkdir()) {
			throw new WorkspaceOpenException("Could not create autostart directory: " + autostartFile.getPath());
		}
	}

	static public void writeNameFile(File workspaceDirectory, String name) throws IOException {
		final File nameFile = new File(workspaceDirectory, NAME_FILE);
		final FileWriter writer = new FileWriter(nameFile);
		try {
			writer.write(name);
		} finally {
			try { writer.close(); } catch (IOException ignored) { }
		}
	}
}
