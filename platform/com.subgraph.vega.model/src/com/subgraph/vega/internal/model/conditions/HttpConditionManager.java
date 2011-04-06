package com.subgraph.vega.internal.model.conditions;

import java.util.ArrayList;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;

public class HttpConditionManager implements IHttpConditionManager {

	private final ObjectContainer database;
	private final List<IHttpConditionType> conditionTypes;
	
	public HttpConditionManager(ObjectContainer database) {
		this.database = database;
		this.conditionTypes = createConditionTypes();
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(database);
		registry.activating().addListener(new EventListener4<CancellableObjectEventArgs>() {
			@Override
			public void onEvent(Event4<CancellableObjectEventArgs> e, CancellableObjectEventArgs args) {
				final Object ob = args.object();
				if(ob instanceof HttpConditionSet) 
					((HttpConditionSet)ob).setConditionManager(HttpConditionManager.this);					
				else if(ob instanceof HttpConditionSetMap) 
					((HttpConditionSetMap)ob).setConditionManager(HttpConditionManager.this);
			}
		});
	}

	private List<IHttpConditionType> createConditionTypes() {
		final List<IHttpConditionType> types = new ArrayList<IHttpConditionType>();
		types.add(ConditionHostname.createType());
		types.add(ConditionRequestMethod.createType());
		types.add(ConditionHeader.createRequestType());
		types.add(ConditionHeader.createResponseType());
		types.add(ConditionResponseLength.createType());
		types.add(ConditionResponseStatusCode.createType());
		return types;
	}
	
	@Override
	public IHttpConditionSet getConditionSet(String name) {
		return getConditionSetMap().getConditionSet(name);
	}
	
	@Override
	public IHttpConditionSet getConditionSetCopy(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveConditionSet(String name, IHttpConditionSet conditionSet) {
		getConditionSetMap().saveConditionSet(name, conditionSet);
	}

	@Override
	public IHttpConditionSet createConditionSet() {
		return new HttpConditionSet(this);
	}

	private HttpConditionSetMap getConditionSetMap() {
		synchronized(this) {
			final List<HttpConditionSetMap> result = database.query(HttpConditionSetMap.class);
			if(result.size() == 1)
				return result.get(0);
			else if(result.size() > 1)
				throw new IllegalStateException("Duplicate HttpConditionSetMap records in database");
			HttpConditionSetMap map = new HttpConditionSetMap(this);
			database.store(map);
			return map;
		}
	}

	@Override
	public List<IHttpConditionType> getConditionTypes() {
		return new ArrayList<IHttpConditionType>(conditionTypes);
	}
}
