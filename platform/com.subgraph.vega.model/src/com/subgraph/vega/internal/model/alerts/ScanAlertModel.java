package com.subgraph.vega.internal.model.alerts;

import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertModel;
import com.subgraph.vega.api.model.alerts.NewScanAlertEvent;
import com.subgraph.vega.api.xml.IXmlRepository;

public class ScanAlertModel implements IScanAlertModel {
	private final EventListenerManager eventManager;
	private final ObjectContainer database;
	private final ScanAlertFactory alertFactory;
	
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
	public IScanAlert createAlert(String type) {
		return alertFactory.createAlert(type);
	}

	@Override
	public void addAlert(IScanAlert alert) {
		synchronized(database) {
			if(rejectDuplicateAlert(alert))
				return;
			database.store(alert);
		}
		eventManager.fireEvent(new NewScanAlertEvent(alert));		
	}

	@Override
	public List<IScanAlert> getAlerts() {
		return database.query(IScanAlert.class);
	}

	private boolean rejectDuplicateAlert(IScanAlert alert) {
		if(alert.getResource() == null)
			return false;
		for(IScanAlert a: getAlertListForResource(alert.getResource())) {
			if(a.equals(alert))
				return true;
		}
		return false;
	}
	
	private List<IScanAlert> getAlertListForResource(final String resource) {
		return database.query(new Predicate<IScanAlert>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(IScanAlert alert) {
				return resource.equals(alert.getResource());
			}
		});
	}
}
