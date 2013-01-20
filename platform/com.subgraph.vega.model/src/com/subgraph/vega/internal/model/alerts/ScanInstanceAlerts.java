package com.subgraph.vega.internal.model.alerts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;

public class ScanInstanceAlerts {
	private final static Logger logger = Logger.getLogger("alerts");

	private final ObjectContainer database;
	private final IScanInstance scanInstance;
	private final EventListenerManager eventManager;
	private final ScanAlertFactory alertFactory;

	
	ScanInstanceAlerts(ObjectContainer database, IScanInstance scanInstance, EventListenerManager eventManager, ScanAlertFactory alertFactory) {
		this.database = database;
		this.scanInstance = scanInstance;
		this.eventManager = eventManager;
		this.alertFactory = alertFactory;
	}

	public IScanAlert createAlert(String type, String key, long requestId) {
		return alertFactory.createAlert(key, type, scanInstance, requestId);
	}

	public void addAlert(IScanAlert alert) {
		synchronized(this) {
			if(rejectDuplicateAlert(alert)) {
				return;
			}
			database.store(alert);
		}
		eventManager.fireEvent(new NewScanAlertEvent(alert));
	}
	
	
	public void removeAlerts(Collection<IScanAlert> alerts) {
		synchronized(this) {
			for(IScanAlert alert: alerts) {
				database.delete(alert);
			}
		}
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
				return resource.equals(alert.getResource()) && alert.getScanId() == scanInstance.getScanId();
			}
		});
	}
	
	public boolean hasAlertKey(String key) {
		return (getAlertByKey(key) != null);
	}

	
	public IScanAlert getAlertByKey(String key) {
		if(key == null) {
			return null;
		}
		
		final List<ScanAlert> results = getAlertListForKey(key);
		if(results.size() == 0) {
			return null;
		}

		if(results.size() > 1) {
			logger.warning("Multiple alert model entries for key: "+ key);
		}
		return results.get(0);
	}

	private List<ScanAlert> getAlertListForKey(final String key) {
			return database.query(new Predicate<ScanAlert>() {
				private static final long serialVersionUID = 1L;
				@Override
				public boolean match(ScanAlert alert) {
					return key.equals(alert.getKey()) && alert.getScanId() == scanInstance.getScanId();
				}			
			});
	}

	public List<IScanAlert> getAllAlerts() {
		return database.query(new Predicate<IScanAlert>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IScanAlert alert) {
				return alert.getScanId() == scanInstance.getScanId();
			}			
		});
	}

	public void removeAllAlerts() {
		final List<IScanAlert> all = new ArrayList<IScanAlert>(getAllAlerts());
		removeAlerts(all);
	}

	public void addScanEventListenerAndPopulate(IEventHandler listener) {
		List<IScanAlert> allAlerts = null;
		synchronized(this) {
			allAlerts = getAllAlerts();
			eventManager.addListener(listener);
		}
		
		for(IScanAlert alert: allAlerts) {
			listener.handleEvent(new NewScanAlertEvent(alert));
		}
	}
}
