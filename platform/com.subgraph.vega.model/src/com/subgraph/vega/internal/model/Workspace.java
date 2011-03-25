package com.subgraph.vega.internal.model;

import java.io.File;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.ext.DatabaseFileLockedException;
import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.html.IHTMLParser;
import com.subgraph.vega.api.model.IModelProperties;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.IWorkspaceEntry;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanAlertModel;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.xml.IXmlRepository;
import com.subgraph.vega.internal.model.alerts.ScanAlertModel;
import com.subgraph.vega.internal.model.requests.RequestLog;
import com.subgraph.vega.internal.model.web.WebModel;

public class Workspace implements IWorkspace {

	private final IWorkspaceEntry workspaceEntry;
	private final EventListenerManager eventManager;
	private final DatabaseConfigurationFactory configurationFactory;
	private final IConsole console;
	private final IHTMLParser htmlParser;
	private final IXmlRepository xmlRepository;
	private final WorkspaceLockStatus lockStatus;
	
	private IWebModel webModel;
	private  IRequestLog requestLog;
	private IScanAlertModel scanAlerts;
	
	private ObjectContainer database;
	private boolean opened;
	
	private WorkspaceStatus workspaceStatus;
	
	Workspace(IWorkspaceEntry entry, EventListenerManager eventManager, IConsole console, IHTMLParser htmlParser, IXmlRepository xmlRepository) {
		this.configurationFactory = new DatabaseConfigurationFactory();
		this.workspaceEntry = entry;
		this.eventManager = eventManager;
		this.console = console;
		this.htmlParser = htmlParser;
		this.xmlRepository = xmlRepository;
		this.webModel = null;
		this.requestLog = null;
		this.scanAlerts = null;
		this.lockStatus = new WorkspaceLockStatus(eventManager);
	}
	
	public boolean open() {
		if(opened)
			throw new IllegalStateException("open() called on workspace which has already been opened.");
		
		database = openDatabase(getDatabasePath());
		if(database == null)
			return false;
		opened = true;
		eventManager.fireEvent(new WorkspaceOpenEvent(this));
		return true;		
	}
	
	private String getDatabasePath() {
		final File databaseFile = new File(workspaceEntry.getPath(), "model.db");
		return databaseFile.getAbsolutePath();	
	}
	
	private ObjectContainer openDatabase(String databasePath) {
		try {
			final ObjectContainer db = configurationFactory.openContainer(databasePath);
			webModel = new WebModel(db);
			requestLog = new RequestLog(db);
			scanAlerts = new ScanAlertModel(db, xmlRepository);
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
	public IScanAlertModel getScanAlertModel() {
		if(!opened)
			throw new IllegalStateException("Must open workspace first");
		return scanAlerts;
	}

	
	@Override
	public void close() {
		if(!opened)
			return;
		if(lockStatus.isLocked())
			throw new IllegalStateException("Cannot close locked workspace.");
		
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
		synchronized(database) {
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
		
		synchronized(database) {
			database.close();
			final File databaseFile = new File(workspaceEntry.getPath(), "model.db");
			if(!databaseFile.delete()) {
			
			}
			database = openDatabase(getDatabasePath());
			if(database != null) {
				eventManager.fireEvent(new WorkspaceResetEvent(this));
			} else {
				eventManager.fireEvent(new WorkspaceCloseEvent(this));
			}
		}		
	}
}
