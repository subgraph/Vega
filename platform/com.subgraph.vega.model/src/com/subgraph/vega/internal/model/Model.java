package com.subgraph.vega.internal.model;

import java.util.List;
import java.util.logging.Logger;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.IWorkspaceEntry;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.xml.IXmlRepository;

public class Model implements IModel {
	private final Logger logger = Logger.getLogger("model");
	private final EventListenerManager workspaceEventManager = new EventListenerManager();
	private IWorkspace currentWorkspace;
	
	private IConsole console;
	private IHTMLParser htmlParser;
	private IXmlRepository xmlRepository;
	private IPathFinder pathFinder;
	
	
	private WorkspaceEntries workspaceEntries;
	
	public void activate() {
		workspaceEntries = new WorkspaceEntries(pathFinder);
	}
	
	protected void setConsole(IConsole console) {
		this.console = console;	
	}

	protected void unsetConsole(IConsole console) {
		this.console = null;
	}
	
	protected void setHTMLParser(IHTMLParser parser) {
		this.htmlParser = parser;
	}
	
	protected void unsetHTMLParser(IHTMLParser parser) {
		this.htmlParser = null;
	}
	
	protected void setXmlRepository(IXmlRepository xmlRepository) {
		this.xmlRepository = xmlRepository;
	}
	
	protected void unsetXmlRepository(IXmlRepository xmlRepository) {
		this.xmlRepository = null;
	}
	
	protected void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}
	
	protected void unsetPathFinder(IPathFinder pathFinder) {
		this.pathFinder = null;
	}

	@Override
	public IWorkspace addWorkspaceListener(IEventHandler handler) {
		workspaceEventManager.addListener(handler);
		return currentWorkspace;
	}

	@Override
	public void removeWorkspaceListener(IEventHandler handler) {
		workspaceEventManager.removeListener(handler);
	}

	@Override
	public List<IWorkspaceEntry> getWorkspaceEntries() {
		return workspaceEntries.getWorkspaceEntries();
	}

	@Override
	public boolean openDefaultWorkspace() {
		final IWorkspaceEntry entry = workspaceEntries.getDefaultWorkspaceEntry();
		if(entry == null)
			return false;
		else
			return openWorkspaceEntry(entry);
	}

	@Override
	public boolean openWorkspaceByIndex(int index) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	private boolean openWorkspaceEntry(IWorkspaceEntry entry) {
		IWorkspace workspace = new Workspace(entry, workspaceEventManager, console, htmlParser, xmlRepository);
		if(!workspace.open()) {
			logger.warning("Failed to open workspace at path "+ entry.getPath());
			return false;
		}
		currentWorkspace = workspace;
		return true;
	}

	@Override
	public void resetCurrentWorkspace() {
		if(currentWorkspace != null)
			currentWorkspace.reset();		
	}

	@Override
	public IWorkspace getCurrentWorkspace() {
		return currentWorkspace;
	}
}

