package com.subgraph.vega.internal.model.scope;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashSet;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.scope.ActiveScopeChangedEvent;
import com.subgraph.vega.api.model.scope.ITargetScope;

public class TargetScope implements ITargetScope, Activatable {

	private final Set<URI> scopeURIs;
	private final Set<String> exclusionPatterns;
	private final Set<URI> exclusionURIs;
	private final boolean isDefault;
	private String name;
	private boolean isActiveScope;

	private transient EventListenerManager scopeChangedListeners;
	private transient List<Pattern> compiledPatterns;
	private transient Activator activator;
	
	TargetScope(boolean isDefault, EventListenerManager scopeChangeListeners) {
		this.name = "";
		this.isDefault = isDefault;
		this.scopeURIs = new ActivatableHashSet<URI>();
		this.exclusionPatterns = new ActivatableHashSet<String>();
		this.exclusionURIs = new ActivatableHashSet<URI>();
		this.scopeChangedListeners = scopeChangeListeners;
	}
	
	@Override
	public String getName() {
		activate(ActivationPurpose.READ);				
		return name;
	}

	@Override
	public void setName(String name) {
		activate(ActivationPurpose.READ);				
		this.name = name;
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public synchronized void addScopeURI(URI uri) {
		activate(ActivationPurpose.READ);
		synchronized (scopeURIs) {
			scopeURIs.add(uri);	
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public synchronized void removeScopeURI(URI uri, boolean removeContained) {
		activate(ActivationPurpose.READ);				
		synchronized (scopeURIs) {
			scopeURIs.remove(uri);
			if(removeContained) {
				removeContainedURIs(uri);
			}
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}
	
	private void removeContainedURIs(URI base) {
		final List<URI> current = new ArrayList<URI>(scopeURIs);
		for(URI u: current) {
			if(uriContains(base, u)) {
				scopeURIs.remove(u);
			}
		}
	}

	private boolean uriContains(URI base, URI uri) {
		final String baseString = base.toString();
		return uri.toString().startsWith(baseString);
	}

	@Override
	public synchronized Collection<URI> getScopeURIs() {
		activate(ActivationPurpose.READ);
		synchronized (scopeURIs) {
			return Collections.unmodifiableList(new ArrayList<URI>(scopeURIs));
		}
	}

	@Override
	public void addExclusionPattern(String pattern) {
		activate(ActivationPurpose.READ);
		synchronized (exclusionPatterns) {
			exclusionPatterns.add(pattern);
			compiledPatterns = compilePatterns();
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public void addExclusionURI(URI uri) {
		activate(ActivationPurpose.READ);
		synchronized (exclusionURIs) {
			exclusionURIs.add(uri);
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public void removeExclusionPattern(String pattern) {
		activate(ActivationPurpose.READ);
		synchronized(exclusionPatterns) {
			exclusionPatterns.remove(pattern);
			compiledPatterns = compilePatterns();
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public void removeExclusionURI(URI uri) {
		activate(ActivationPurpose.READ);
		synchronized (exclusionURIs) {
			exclusionURIs.remove(uri);
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public Collection<String> getExclusionPatterns() {
		activate(ActivationPurpose.READ);
		synchronized(exclusionPatterns) {
			return Collections.unmodifiableCollection(new ArrayList<String>(exclusionPatterns));
		}
	}

	@Override
	public Collection<URI> getExclusionURIs() {
		activate(ActivationPurpose.READ);
		synchronized (exclusionURIs) {
			return Collections.unmodifiableCollection(new ArrayList<URI>(exclusionURIs));
		}
	}

	@Override
	public boolean isExcluded(URI uri) {
		final String uriString = uri.toString();
		for(Pattern p: getCompiledPatterns()) {
			if(p.matcher(uriString).find()) {
				return true;
			}
		}
		
		synchronized (exclusionURIs) {
			for(URI u: exclusionURIs) {
				if(uriContains(u, uri)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isInsideScope(URI uri) {
		activate(ActivationPurpose.READ);
		synchronized (scopeURIs) {
			for(URI u: scopeURIs) {
				if(uriContains(u, uri)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean filter(URI uri) {
		return isInsideScope(uri) && !isExcluded(uri);
	}

	@Override
	public void clear() {
		activate(ActivationPurpose.READ);
		synchronized (scopeURIs) {
			synchronized (exclusionPatterns) {
				scopeURIs.clear();
				exclusionPatterns.clear();
				exclusionURIs.clear();
			}
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public boolean isActiveScope() {
		activate(ActivationPurpose.READ);
		return isActiveScope;
	}

	
	public void setIsActiveScope(boolean flag) {
		activate(ActivationPurpose.READ);
		isActiveScope = flag;
		activate(ActivationPurpose.WRITE);
	}
	
	@Override
	public boolean isDefaultScope() {
		activate(ActivationPurpose.READ);
		return isDefault;
	}

	private List<Pattern> getCompiledPatterns() {
		synchronized (exclusionPatterns) {
			if(compiledPatterns == null) {
				compiledPatterns = compilePatterns();
			}
		}
		return compiledPatterns;
	}
	
	private List<Pattern> compilePatterns() {
		activate(ActivationPurpose.READ);
		final List<Pattern> patterns = new ArrayList<Pattern>();
		for(String regex: exclusionPatterns) {
			patterns.add(Pattern.compile(regex));
		}
		return patterns;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	private void notifyIfActiveScope() {
		if(isActiveScope) {
			scopeChangedListeners.fireEvent(new ActiveScopeChangedEvent(this));
		}
	}

	void setEventListenerManager(EventListenerManager mgr) {
		this.scopeChangedListeners = mgr;
	}
	
	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if (activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if (this.activator == activator) {
			return;
		}
		if (activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		this.activator = activator;			
	}
}
