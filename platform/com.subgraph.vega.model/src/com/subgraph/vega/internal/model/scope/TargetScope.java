package com.subgraph.vega.internal.model.scope;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashSet;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.events.EventListenerManager;
import com.subgraph.vega.api.model.scope.ActiveScopeChangedEvent;
import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.api.util.UriTools;

public class TargetScope implements ITargetScope, Activatable {

	private final long id;
	private final Set<String> scopeURIs;
	private final Set<String> exclusionPatterns;
	private final Set<String> exclusionURIs;
	private final boolean isDefault;
	private String name;
	private boolean isActiveScope;
	private boolean isReadOnly;
	private boolean isDetached;

	private transient EventListenerManager scopeChangedListeners;
	private transient List<Pattern> compiledPatterns;
	private transient Activator activator;
	
	TargetScope(long id, boolean isDefault, EventListenerManager scopeChangeListeners) {
		this.id = id;
		this.name = "";
		this.isDefault = isDefault;
		this.scopeURIs = new ActivatableHashSet<String>();
		this.exclusionPatterns = new ActivatableHashSet<String>();
		this.exclusionURIs = new ActivatableHashSet<String>();
		this.scopeChangedListeners = scopeChangeListeners;
	}
	
	@Override
	public long getScopeId() {
		activate(ActivationPurpose.READ);
		return id;
	}

	@Override
	public String getName() {
		activate(ActivationPurpose.READ);				
		return name;
	}

	private void checkReadOnly() {
		if(isReadOnly) {
			throw new IllegalStateException("Attempt to modify TargetScope instance which is marked Read Only");
		}
	}

	@Override
	public void setName(String name) {
		activate(ActivationPurpose.READ);
		checkReadOnly();
		this.name = name;
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public synchronized void addScopeURI(URI uri) {
		activate(ActivationPurpose.READ);
		checkReadOnly();
		synchronized (scopeURIs) {
			scopeURIs.add(uri.toString());	
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public synchronized void removeScopeURI(URI uri, boolean removeContained) {
		activate(ActivationPurpose.READ);
		checkReadOnly();
		synchronized (scopeURIs) {
			scopeURIs.remove(uri.toString());
			if(removeContained) {
				removeContainedURIs(uri);
			}
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}
	
	private void removeContainedURIs(URI base) {
		final List<String> current = new ArrayList<String>(scopeURIs);
		for(String uriStr: current) {
			if(UriTools.doesBaseUriContain(base, URI.create(uriStr))) {
				scopeURIs.remove(uriStr);
			}
		}
	}

	@Override
	public synchronized Collection<URI> getScopeURIs() {
		activate(ActivationPurpose.READ);
		final List<URI> ret = new ArrayList<URI>();
		synchronized (scopeURIs) {
			for(String uriStr: scopeURIs) {
				ret.add(URI.create(uriStr));
			}
			return Collections.unmodifiableCollection(ret);
		}
	}

	@Override
	public void addExclusionPattern(String pattern) {
		activate(ActivationPurpose.READ);
		checkReadOnly();
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
		checkReadOnly();
		synchronized (exclusionURIs) {
			exclusionURIs.add(uri.toString());
		}
		activate(ActivationPurpose.WRITE);
		notifyIfActiveScope();
	}

	@Override
	public void removeExclusionPattern(String pattern) {
		activate(ActivationPurpose.READ);
		checkReadOnly();
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
		checkReadOnly();
		synchronized (exclusionURIs) {
			exclusionURIs.remove(uri.toString());
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
		final List<URI> ret = new ArrayList<URI>();
		synchronized (exclusionURIs) {
			for(String uriStr: exclusionURIs) {
				ret.add(URI.create(uriStr));
			}
			return Collections.unmodifiableCollection(ret);
		}
	}

	@Override
	public boolean isExcluded(URI uri) {
		activate(ActivationPurpose.READ);
		final String uriString = uri.toString();
		for(Pattern p: getCompiledPatterns()) {
			if(p.matcher(uriString).find()) {
				return true;
			}
		}
		
		synchronized (exclusionURIs) {
			for(String uriStr: exclusionURIs) {
				if(UriTools.doesBaseUriContain(URI.create(uriStr), uri)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean isExcluded(HttpHost host, String uriPath) {
		activate(ActivationPurpose.READ);
		final String uriString = host.toString() + uriPath;
		for(Pattern p: getCompiledPatterns()) {
			if(p.matcher(uriString).find()) {
				return true;
			}
		}
		synchronized (exclusionURIs) {
			for(String uriStr: exclusionURIs) {
				if(matchesBase(URI.create(uriStr), host, uriPath)) {
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
			for(String uriStr: scopeURIs) {
				if(UriTools.doesBaseUriContain(URI.create(uriStr), uri)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override 
	public boolean isInsideScope(HttpHost host, String uriPath) {
		activate(ActivationPurpose.READ);
		synchronized (scopeURIs) {
			for(String uriStr: scopeURIs) {
				if(matchesBase(URI.create(uriStr), host, uriPath)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean matchesBase(URI baseURI, HttpHost host, String uriPath) {
		final HttpHost baseHost = URIUtils.extractHost(baseURI);
		final String basePath = getPathPart(baseURI);
		if(!hostsMatch(baseHost, host)) {
			return false;
		}
		return uriPath.startsWith(basePath);
	}

	
	private boolean hostsMatch(HttpHost h1, HttpHost h2) {
		return stringsMatch(h1.getHostName(), h2.getHostName()) && 
				stringsMatch(h1.getSchemeName(), h2.getSchemeName()) &&
				portsMatch(h1.getSchemeName(), h1.getPort(), h2.getPort());
	}

	private boolean stringsMatch(String s1, String s2) {
		if(s1 == null && s2 != null) {
			return false;
		}
		return s1.equalsIgnoreCase(s2);
	}
	
	private boolean portsMatch(String scheme, int p1, int p2) {
		if(p1 == p2) {
			return true;
		} else if(
				"http".equalsIgnoreCase(scheme) && (
						((p1 == -1) && (p2 == 80)) ||
						((p1 == 80) && (p2 == -1)))) {
			return true;
		} else if(
				"https".equalsIgnoreCase(scheme) && (
						((p1 == -1) && (p2 == 443)) ||
						((p1 == 443) && (p2 == -1)))) {
			return true;
		} else {
			return false;
		}
	}
	
	private String getPathPart(URI uri) {
		if(uri.getQuery() == null) {
			return uri.getPath();
		} else {
			return uri.getPath() + '?' + uri.getQuery();
		}
	}

	@Override
	public boolean filter(URI uri) {
		return isInsideScope(uri) && !isExcluded(uri);
	}
	
	@Override
	public boolean filter(HttpHost host, String uri) {
		return isInsideScope(host, uri) && !isExcluded(host, uri);
	}

	@Override
	public void clear() {
		activate(ActivationPurpose.READ);
		checkReadOnly();
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
	public boolean isEmpty() {
		return scopeURIs.isEmpty();
	}
	
	@Override
	public boolean isActiveScope() {
		activate(ActivationPurpose.READ);
		return isActiveScope;
	}

	
	@Override
	public void setReadOnly(boolean value) {
		activate(ActivationPurpose.READ);
		isReadOnly = value;
		activate(ActivationPurpose.WRITE);
	}

	public void setDetached(boolean value) {
		activate(ActivationPurpose.READ);
		if(value && isActiveScope) {
			throw new IllegalStateException("Cannot detach the active scope.");
		}
		if(value && isDefault) {
			throw new IllegalStateException("Cannot detach the default scope");
		}
		
		isDetached = value;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public boolean isReadOnly() {
		activate(ActivationPurpose.READ);
		return isReadOnly;
	}

	@Override
	public boolean isDetached() {
		return isDetached;
	}

	public void setIsActiveScope(boolean flag) {
		activate(ActivationPurpose.READ);
		if(flag && isDetached) {
			throw new IllegalStateException("Cannot use a detached scope as the active scope");
		}
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
