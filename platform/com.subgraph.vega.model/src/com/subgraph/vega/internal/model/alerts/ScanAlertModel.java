package com.subgraph.vega.internal.model.alerts;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertModel;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.xml.IXmlRepository;

public class ScanAlertModel implements IScanAlertModel {
	private final Logger logger = Logger.getLogger("alerts");
	private final EventListenerManager eventManager;
	private final ObjectContainer database;
	private final ScanAlertFactory alertFactory;
	private final Lock lock = new ReentrantLock();

	public ScanAlertModel(ObjectContainer database, IXmlRepository xmlRepository) {
		this.database = database;
		this.eventManager = new EventListenerManager();
		this.alertFactory = new ScanAlertFactory(xmlRepository);
	}

	@Override
	public void addAlertListenerAndPopulate(IEventHandler listener) {
		for(IScanAlert alert: getAlerts()) {
			listener.handleEvent(new NewScanAlertEvent(alert));
		}
		eventManager.addListener(listener);
	}

	@Override
	public void removeAlertListener(IEventHandler listener) {
		eventManager.removeListener(listener);
	}

	@Override
	public IScanAlert createAlert(String type, String key) {
		return alertFactory.createAlert(key, type, -1);
	}

	@Override
	public IScanAlert createAlert(String type, String key, long requestId) {
		return alertFactory.createAlert(key, type, requestId);
	}

	@Override
	public IScanAlert createAlert(String type) {
		return alertFactory.createAlert(null, type, -1);
	}

	@Override
	public IScanAlert getAlertByKey(final String key) {
		if(key == null)
			return null;
		synchronized(database) {
			final List<IScanAlert> results = getAlertListForKey(key);
			if(results.size() == 0)
				return null;
			if(results.size() > 1)
				logger.warning("Multiple alert model entries for key "+ key);
			return results.get(0);
		}
	}

	private List<IScanAlert> getAlertListForKey(final String key) {
		return database.query(new Predicate<IScanAlert>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IScanAlert alert) {
				return key.equals(alert.getKey());
			}
		});
	}

	@Override
	public void addAlert(IScanAlert alert) {

		if(rejectDuplicateAlert(alert))
			return;
		synchronized(database) {
			database.store(alert);
			database.commit();
		}
		eventManager.fireEvent(new NewScanAlertEvent(alert));
	}

	@Override
	public List<IScanAlert> getAlerts() {
		synchronized(database) {
			return database.query(IScanAlert.class);
		}
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
		if(alert.getResource() == null)
			return false;
		for(IScanAlert a: getAlertListForResource(alert.getResource())) {
			if(a.equals(alert))
				return true;
		}
		if(alert.getKey() == null)
			return false;
		else
			return hasAlertKey(alert.getKey());
	}

	@Override
	public boolean hasAlertKey(String key) {
		return getAlertByKey(key) != null;
	}

	private List<IScanAlert> getAlertListForResource(final String resource) {
		synchronized(database) {
		return database.query(new Predicate<IScanAlert>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(IScanAlert alert) {
				return resource.equals(alert.getResource());
			}
		});
		}
	}
}
