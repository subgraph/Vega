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
package com.subgraph.vega.internal.model;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.db4o.ObjectContainer;
import com.db4o.ext.DatabaseFileLockedException;
import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.NamedEventListenerManager;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IModelProperties;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.IWorkspaceEntry;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.tags.ITagModel;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.xml.IXmlRepository;
import com.subgraph.vega.internal.model.alerts.ScanAlertRepository;
import com.subgraph.vega.internal.model.conditions.HttpConditionManager;
import com.subgraph.vega.internal.model.requests.RequestLog;
import com.subgraph.vega.internal.model.tags.TagModel;
import com.subgraph.vega.internal.model.web.WebModel;

public class Workspace implements IWorkspace {
	private static final int BACKGROUND_COMMIT_INTERVAL = 10000;
	private final IWorkspaceEntry workspaceEntry;
	private final NamedEventListenerManager conditionChangeManager;
	private final EventListenerManager eventManager;
	private final DatabaseConfigurationFactory configurationFactory;
	private final IConsole console;
	private final IHTMLParser htmlParser;
	private final IXmlRepository xmlRepository;
	private final WorkspaceLockStatus lockStatus;

	private ITagModel tagModel;
	private IWebModel webModel;
	private IRequestLog requestLog;
	private IScanAlertRepository scanAlerts;
	private HttpConditionManager conditionManager;

	private ObjectContainer database;
	private boolean opened;

	private final Timer backgroundCommitTimer;
	private TimerTask backgroundCommitTask;

	private WorkspaceStatus workspaceStatus;

	Workspace(IWorkspaceEntry entry, NamedEventListenerManager conditionChangeManager, EventListenerManager eventManager, IConsole console, IHTMLParser htmlParser, IXmlRepository xmlRepository) {
		this.configurationFactory = new DatabaseConfigurationFactory();
		this.workspaceEntry = entry;
		this.conditionChangeManager = conditionChangeManager;
		this.eventManager = eventManager;
		this.console = console;
		this.htmlParser = htmlParser;
		this.xmlRepository = xmlRepository;
		this.lockStatus = new WorkspaceLockStatus(eventManager);
		this.backgroundCommitTimer = new Timer();
	}

	@Override
	public boolean open() {
		if(opened)
			throw new IllegalStateException("open() called on workspace which has already been opened.");

		database = openDatabase(getDatabasePath());
		if(database == null)
			return false;
		opened = true;
		eventManager.fireEvent(new WorkspaceOpenEvent(this));
		backgroundCommitTask = createBackgroundCommitTask(database);
		backgroundCommitTimer.scheduleAtFixedRate(backgroundCommitTask, 0, BACKGROUND_COMMIT_INTERVAL);
		return true;
	}

	private String getDatabasePath() {
		final File databaseFile = new File(workspaceEntry.getPath(), "model.db");
		return databaseFile.getAbsolutePath();
	}

	private ObjectContainer openDatabase(String databasePath) {
		try {
			final ObjectContainer db = configurationFactory.openContainer(databasePath);
			tagModel = new TagModel(db);
			webModel = new WebModel(db);
			requestLog = new RequestLog(db);
			scanAlerts = new ScanAlertRepository(db, xmlRepository);
			conditionManager = new HttpConditionManager(db, conditionChangeManager);
			return db;
		} catch (DatabaseFileLockedException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ITagModel getTagModel() {
		if (!opened) {
			throw new IllegalStateException("Must open workspace first");
		}
		return tagModel;
	}

	@Override
	public IWebModel getWebModel() {
		if(!opened)
			throw new IllegalStateException("Must open workspace first");
		return webModel;
	}

	@Override
	public IRequestLog getRequestLog() {
		if(!opened)
			throw new IllegalStateException("Must open workspace first");
		return requestLog;
	}

	@Override
	public IScanAlertRepository getScanAlertRepository() {
		if(!opened)
			throw new IllegalStateException("Must open workspace first");
		return scanAlerts;
	}

	@Override
	public IHttpConditionManager getHttpConditionMananger() {
		return conditionManager;
	}

	@Override
	public void close() {
		if(!opened)
			return;
		if(lockStatus.isLocked())
			throw new IllegalStateException("Cannot close locked workspace.");
		backgroundCommitTask.cancel();
		conditionManager.notifyClosed();
		database.close();
		opened = false;
		eventManager.fireEvent(new WorkspaceCloseEvent(this));
	}

	@Override
	public void setProperty(String name, Object value) {
		getProperties().setProperty(name, value);
	}

	@Override
	public void setStringProperty(String name, String value) {
		getProperties().setStringProperty(name, value);
	}

	@Override
	public void setIntegerProperty(String name, int value) {
		getProperties().setIntegerProperty(name, value);
	}

	@Override
	public Object getProperty(String name) {
		return getProperties().getProperty(name);
	}

	@Override
	public String getStringProperty(String name) {
		return getProperties().getStringProperty(name);
	}

	@Override
	public Integer getIntegerProperty(String name) {
		return getProperties().getIntegerProperty(name);
	}

	@Override
	public List<String> propertyKeys() {
		return getProperties().propertyKeys();
	}

	private IModelProperties getProperties() {
		if(workspaceStatus != null)
			return workspaceStatus.getProperties();
		synchronized(this) {
			List<WorkspaceStatus> result = database.query(WorkspaceStatus.class);
			if(result.size() == 0) {
				workspaceStatus = new WorkspaceStatus();
				database.store(workspaceStatus);
				return workspaceStatus.getProperties();
			} else if(result.size() == 1) {
				workspaceStatus =  result.get(0);
				return workspaceStatus.getProperties();
			} else {
				throw new IllegalStateException("Database corrupted, multiple WorkspaceStatus instances");
			}
		}
	}

	@Override
	public IHTMLParser getHTMLParser() {
		return htmlParser;
	}

	@Override
	public void consoleWrite(String output) {
		console.write(output);
	}

	@Override
	public void consoleError(String output) {
		console.error(output);
	}

	@Override
	public void lock() {
		lockStatus.lock();
	}

	@Override
	public void unlock() {
		lockStatus.unlock();
	}

	@Override
	public void reset() {
		if(lockStatus.isLocked())
			throw new IllegalStateException("Cannot reset locked workspace");

		backgroundCommitTask.cancel();
		synchronized(this) {
			conditionManager.notifyClosed();
			database.close();
			final File databaseFile = new File(workspaceEntry.getPath(), "model.db");
			if(!databaseFile.delete()) {

			}
			database = openDatabase(getDatabasePath());
			if(database != null) {
				workspaceStatus = null;
				eventManager.fireEvent(new WorkspaceResetEvent(this));
				backgroundCommitTask = createBackgroundCommitTask(database);
				backgroundCommitTimer.scheduleAtFixedRate(backgroundCommitTask, 0, BACKGROUND_COMMIT_INTERVAL);
			} else {
				eventManager.fireEvent(new WorkspaceCloseEvent(this));
			}
		}
	}

	private TimerTask createBackgroundCommitTask(final ObjectContainer db) {
		return new TimerTask() {
			@Override
			public void run() {
				if(!db.ext().isClosed()) {
					db.commit();
				}
			}
		};
	}

}
