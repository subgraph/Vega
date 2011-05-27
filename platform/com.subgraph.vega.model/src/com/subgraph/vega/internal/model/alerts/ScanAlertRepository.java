package com.subgraph.vega.internal.model.alerts;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.db4o.ObjectContainer;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.events.ObjectInfoEventArgs;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.alerts.ActiveScanInstanceEvent;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.alerts.NewScanInstanceEvent;
import com.subgraph.vega.api.xml.IXmlRepository;

public class ScanAlertRepository implements IScanAlertRepository {
	private final static Logger logger = Logger.getLogger("alerts");
	private final ObjectContainer database;
	private final Object scanInstanceLock = new Object();
	private final ScanAlertFactory alertFactory;
	private final EventListenerManager scanInstanceEventManager;
	
	private IScanInstance activeScanInstance;
	
	public ScanAlertRepository(ObjectContainer db, IXmlRepository xmlRepository) {
		this.database = db;
		this.alertFactory = new ScanAlertFactory(xmlRepository);
		this.scanInstanceEventManager = new EventListenerManager();
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(database);
		registry.activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			@Override
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				final Object ob = args.object();
				if(ob instanceof ScanInstance) {
					final ScanInstance scan = (ScanInstance) ob;
					scan.setTransientState(database, alertFactory);
					final int status = scan.getScanStatus();
					if(status != IScanInstance.SCAN_COMPLETED && status != IScanInstance.SCAN_CANCELLED) {
						scan.updateScanStatus(IScanInstance.SCAN_CANCELLED);
					}
				}
			}
		});
		getProxyScanInstance();
	}

	@Override
	public IScanInstance addActiveScanInstanceListener(IEventHandler listener) {
		scanInstanceEventManager.addListener(listener);
		return activeScanInstance;
	}

	@Override
	public void removeActiveScanInstanceListener(IEventHandler listener) {
		scanInstanceEventManager.removeListener(listener);
	}

	@Override
	public void setActiveScanInstance(IScanInstance scanInstance) {
		activeScanInstance = scanInstance;
		scanInstanceEventManager.fireEvent(new ActiveScanInstanceEvent(scanInstance));
	}

	@Override
	public IScanInstance getActiveScanInstance() {
		return activeScanInstance;
	}

	@Override
	public List<IScanInstance> getAllScanInstances() {
		return database.query(IScanInstance.class);
	}

	@Override
	public synchronized IScanInstance createNewScanInstance() {
		synchronized(scanInstanceLock) {
			final long scanId = allocateNewScanId();
			final IScanInstance scan = createScanInstanceForScanId(scanId);
			scanInstanceEventManager.fireEvent(new NewScanInstanceEvent(scan));
			return scan;
		}
	}

	private IScanInstance createScanInstanceForScanId(long scanId) {
		final ScanInstance scan = new ScanInstance(scanId);
		scan.setTransientState(database, alertFactory);
		database.store(scan);
		return scan;
	}

	@Override
	public synchronized IScanInstance getScanInstanceByScanId(final long scanId) {
		final List<IScanInstance> results = database.query(new Predicate<IScanInstance>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(IScanInstance scan) {
				return scan.getScanId() == scanId;
			}			
		});
		if(results.size() == 0) {
			return null;
		} else if (results.size() > 1) {
			logger.warning("Multiple scan instances for scanId = "+ scanId);
		} 
		return results.get(0);
	}

	@Override
	public synchronized IScanInstance getProxyScanInstance() {
		final IScanInstance scan = getScanInstanceByScanId(PROXY_ALERT_ORIGIN_SCAN_ID);
		if(scan != null) {
			return scan;
		}
		return createScanInstanceForScanId(PROXY_ALERT_ORIGIN_SCAN_ID);
	}
	
	private long allocateNewScanId() {
		int count = 0;
		final Random r = new Random();
		while(true) {
			long scanId = r.nextInt(999999) + 1;
			if(getScanInstanceByScanId(scanId) == null) {
				return scanId;
			}
			count += 1;
			if(count > 10) {
				throw new IllegalStateException("Unable to generate unique random scan id");
			}
		}
	}
}
