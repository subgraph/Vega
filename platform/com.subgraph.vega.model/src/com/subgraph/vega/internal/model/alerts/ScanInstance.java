package com.subgraph.vega.internal.model.alerts;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.db4o.ObjectContainer;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.query.Predicate;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;

public class ScanInstance implements IScanInstance, Activatable {
	private final static Logger logger = Logger.getLogger("alerts");
	private final long scanId;
	
	private transient EventListenerManager eventManager;
	private transient ObjectContainer database;
	private transient ScanAlertFactory alertFactory;
	private transient Lock lock;
	
	private transient Activator activator;
	
	ScanInstance(long scanId) {
		this.scanId = scanId;
	}

	void setTransientState(ObjectContainer database, ScanAlertFactory alertFactory, EventListenerManager eventManager) {
		this.database = database;
		this.alertFactory = alertFactory;
		this.eventManager = eventManager;
		this.lock = new ReentrantLock();
	}

	@Override
	public long getScanId() {
		activate(ActivationPurpose.READ);
		return scanId;
	}

	@Override
	public IScanAlert createAlert(String type) {
		return createAlert(type, null, -1);
	}

	@Override
	public IScanAlert createAlert(String type, String key) {
		return createAlert(type, key, -1);
	}

	@Override
	public IScanAlert createAlert(String type, String key, long requestId) {
		return alertFactory.createAlert(key, type, scanId, requestId);
	}

	@Override
	public void addAlert(IScanAlert alert) {
		if(rejectDuplicateAlert(alert)) {
			return;
		}
		database.store(alert);
		eventManager.fireEvent(new NewScanAlertEvent(alert));		
	}

	@Override
	public boolean hasAlertKey(String key) {
		return getAlertByKey(key) != null;
	}

	@Override
	public IScanAlert getAlertByKey(String key) {
		if(key == null) {
			return null;
		}
		
		synchronized (this) {
			final List<ScanAlert> results = getAlertListForKey(key);
			if(results.size() == 0) {
				return null;
			}
			
			if(results.size() > 1) {
				logger.warning("Multiple alert model entries for key: "+ key);
			}
			
			return results.get(0);
		}
	}

	private List<ScanAlert> getAlertListForKey(final String key) {
		return database.query(new Predicate<ScanAlert>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(ScanAlert alert) {
				return key.equals(alert.getKey());
			}			
		});
	}

	@Override
	public List<IScanAlert> getAllAlerts() {
		return database.query(new Predicate<IScanAlert>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IScanAlert alert) {
				return alert.getScanId() == scanId;
			}			
		});
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void unlock() {
		lock.unlock();
	}
	
	private boolean rejectDuplicateAlert(IScanAlert alert) {
		if(alert.getResource() == null) {
			return false;
		}
		
		for(ScanAlert sa: getAlertListForResource(alert.getResource())) {
			if(sa.equals(alert)) {
				return true;
			}
		}
		
		if(alert.getKey() == null) {
			return false;
		} else {
			return hasAlertKey(alert.getKey());
		}
	}

	private List<ScanAlert> getAlertListForResource(final String resource) {
		return database.query(new Predicate<ScanAlert>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(ScanAlert alert) {
				return resource.equals(alert.getResource());
			}
		});
	}


	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;			
	}
}
