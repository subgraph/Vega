package com.subgraph.vega.internal.model.scope;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.events.CancellableObjectEventArgs;
import com.db4o.events.Event4;
import com.db4o.events.EventListener4;
import com.db4o.events.EventRegistry;
import com.db4o.events.EventRegistryFactory;
import com.db4o.query.Predicate;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.scope.ActiveScopeChangedEvent;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.model.scope.ITargetScopeManager;

public class TargetScopeManager implements ITargetScopeManager {
	private final ObjectContainer database;
	private final TargetScopeId scopeId;
	private final EventListenerManager scopeChangeListeners;
	private ITargetScope activeScope;
	private ITargetScope defaultScope;
	
	public TargetScopeManager(ObjectContainer database) {
		this.database = database;
		this.scopeId = getTargetScopeId(database);
		scopeChangeListeners = new EventListenerManager();
		final EventRegistry registry = EventRegistryFactory.forObjectContainer(database);
		registry.activating().addListener(new EventListener4<CancellableObjectEventArgs>() {

			@Override
			public void onEvent(Event4<CancellableObjectEventArgs> e,
					CancellableObjectEventArgs args) {
				final Object ob = args.object();
				if(ob instanceof TargetScope) {
					((TargetScope)ob).setEventListenerManager(scopeChangeListeners);
				}
			}
			
		});
		defaultScope = findDefaultScope();
		activeScope = findActiveScope();
	}
	
	private TargetScopeId getTargetScopeId(ObjectContainer database) {
		List<TargetScopeId> result = database.query(TargetScopeId.class);
		if(result.size() == 0) {
			final TargetScopeId tsi = new TargetScopeId();
			database.store(tsi);
			return tsi;
		} else if(result.size() == 1) {
			return result.get(0);
		} else {
			throw new IllegalStateException("Database corrupted, found multiple TargetScopeId instances");
		}
	}

	@Override
	public ITargetScope createNewScope() {
		return createNewScope("New Scope");
	}
	
	@Override
	public ITargetScope createDuplicatedScope(ITargetScope scope) {
		final ITargetScope newScope = createNewScope(scope.getName());
		for(URI u: scope.getScopeURIs()) {
			newScope.addScopeURI(u);
		}
		for(URI u: scope.getExclusionURIs()) {
			newScope.addExclusionURI(u);
		}
		for(String s: scope.getExclusionPatterns()) {
			newScope.addExclusionPattern(s);
		}
		return newScope;
	}

	private ITargetScope createNewScope(String name) {
		final long id = scopeId.allocateId();
		final ITargetScope scope = new TargetScope(id, false, scopeChangeListeners);
		scope.setName(name);
		return scope;
	}
	
	private ITargetScope createDefaultScope() {
		final long id = scopeId.allocateId();
		final ITargetScope scope = new TargetScope(id, true, scopeChangeListeners);
		((TargetScope)scope).setIsActiveScope(true);
		scope.setName("Default Scope");
		database.store(scope);
		return scope;
	}
	
	@Override
	public void saveScope(ITargetScope scope) {
		database.store(scope);
	}

	@Override
	public boolean removeScope(ITargetScope scope) {
		if(scope == defaultScope) {
			return false;
		} else if(scope == activeScope) {
			setActiveScope(defaultScope);
		}
		
		database.delete(scope);
		return true;
	}

	@Override
	public Collection<ITargetScope> getAllScopes() {
		return database.query(ITargetScope.class);
	}

	@Override
	public ITargetScope getActiveScope() {
		return activeScope;
	}

	@Override
	public ITargetScope getDefaultScope() {
		return defaultScope;
	}
	
	@Override
	public void setActiveScope(ITargetScope scope) {
		if(scope == activeScope || !database.ext().isStored(scope)) {
			return;
		}
		synchronized (this) {
			((TargetScope)activeScope).setIsActiveScope(false);
			((TargetScope)scope).setIsActiveScope(true);
			activeScope = scope;
			
			scopeChangeListeners.fireEvent(new ActiveScopeChangedEvent(activeScope));
		}
	}

	@Override
	public void setScopeDetached(ITargetScope scope) {
		if(scope.isActiveScope()) {
			setActiveScope(defaultScope);
		}
		((TargetScope)scope).setDetached(true);
	}

	@Override
	public void setScopeAttached(ITargetScope scope) {
		((TargetScope)scope).setDetached(false);
	}

	@Override
	public ITargetScope addActiveScopeChangeListener(IEventHandler listener) {
		synchronized (this) {
			scopeChangeListeners.addListener(listener);
			return activeScope;
		}
	}
	
	@Override
	public void removeActiveScopeChangeListener(IEventHandler listener) {
		scopeChangeListeners.removeListener(listener);
	}

	private ITargetScope findDefaultScope() {
		final List<ITargetScope> results = database.query(new Predicate<ITargetScope>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(ITargetScope scope) {
				return scope.isDefaultScope();
			}
		});
		if(results.size() == 0) {
			return createDefaultScope();
		}
		return results.get(0);
	}

	private ITargetScope findActiveScope() {
		final List<ITargetScope> results = database.query(new Predicate<ITargetScope>() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(ITargetScope scope) {
				return scope.isActiveScope();
			}
		});

		if(results.size() == 0) {
			final ITargetScope active = findDefaultScope();
			((TargetScope)active).setIsActiveScope(true);
			return active;
		}
		return results.get(0);
	}
}
