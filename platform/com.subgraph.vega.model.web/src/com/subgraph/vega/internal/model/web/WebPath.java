package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.subgraph.vega.api.model.web.old.IWebEntity;
import com.subgraph.vega.api.model.web.old.IWebGetTarget;
import com.subgraph.vega.api.model.web.old.IWebHost;
import com.subgraph.vega.api.model.web.old.IWebPath;

public class WebPath extends AbstractWebEntity implements IWebPath {

	private final Map<String, IWebPath> childPaths = new LinkedHashMap<String, IWebPath>();
	private final Map<String, IWebGetTarget> getTargets = new LinkedHashMap<String, IWebGetTarget>();
	private final IWebHost host;
	private final String path;
	private final String fullPath;
	private final URI uri;
	private final IWebPath parentPath;
	
	private volatile transient ImmutableList<IWebPath> childPathList = ImmutableList.of();
	private volatile transient ImmutableList<IWebGetTarget> getTargetList = ImmutableList.of();
	
	WebPath(WebModel model, IWebHost host, String path, IWebPath parentPath) {
		super(model);
		this.host = checkNotNull(host);
		this.path = checkNotNull(path);
		this.parentPath = parentPath;
		this.fullPath = createFullPath();
		this.uri = createURI(host.toURI(), fullPath);
	}
	
	private static URI createURI(URI hostURI, String fullPath) {
		try {
			return new URI(hostURI.getScheme(),hostURI.getUserInfo(), hostURI.getHost(), hostURI.getPort(), fullPath, null, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Failed to create URI for path: "+ fullPath, e);
		}
	}
	
	@Override
	public IWebEntity getParent() {
		if(parentPath == null)
			return host;
		else
			return parentPath;
	}

	@Override
	public URI toURI() {
		return uri;
	}

	@Override
	public IWebHost getHostEntity() {
		return host;
	}

	@Override
	public IWebHost getHost() {
		return host;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public IWebPath getParentPath() {
		return parentPath;
	}

	@Override
	public Collection<IWebPath> getChildPaths() {
		return childPathList;
	}

	@Override
	public Collection<IWebGetTarget> getTargets() {
		return getTargetList;
	}

	@Override
	public IWebPath getChildPath(String path) {
		synchronized (childPaths) {
			return childPaths.get(path);
		}
	}

	@Override
	public IWebPath getOrCreateChildPath(String path) {
		synchronized(childPaths) {
			if(childPaths.containsKey(path))
				return childPaths.get(path);
			WebPath newPath = new WebPath(model, host, path, this);
			childPaths.put(path, newPath);
			childPathList = ImmutableList.copyOf(childPaths.values());
			model.notifyEntityAdded(newPath);
			return newPath;
		}
	}
	
	@Override
	public String getFullPath() {
		return fullPath;
	}
	
	@Override
	public Set<IWebPath> getUnvisitedPaths() {
		Set<IWebPath> unvisitedPaths = new HashSet<IWebPath>();
		if(!isVisited())
			unvisitedPaths.add(this);
		for(IWebPath p: getChildPaths())
			unvisitedPaths.addAll(p.getUnvisitedPaths());
		return unvisitedPaths;
	}

	@Override
	public IWebGetTarget addGetTarget(String query, String mimeType) {
		synchronized (getTargets) {
			if(getTargets.containsKey(query)) {
				IWebGetTarget get = getTargets.get(query);
				get.setMimeType(mimeType);
				return get;
			}
			
			WebGetTarget newGet = new WebGetTarget(model, this, Strings.nullToEmpty(query), mimeType);
			getTargets.put(query, newGet);
			getTargetList = ImmutableList.copyOf(getTargets.values());
			model.notifyEntityAdded(newGet);
			return newGet;
		}
	}
	
	private String createFullPath() {
		List<String> pathList = new ArrayList<String>();
		for(IWebPath wp = this; wp != null; wp = wp.getParentPath())
			pathList.add(wp.getPath());
		Collections.reverse(pathList);
		return Joiner.on('/').join(pathList);
	}
	
	public String toString() {
		return Objects.toStringHelper(this).add("uri", uri).toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof WebPath) {
			WebPath that = (WebPath) other;
			return fullPath.equals(that.fullPath) && host.equals(((WebPath) other).host);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(fullPath, host);
	}
}
