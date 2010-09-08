package com.subgraph.vega.api.model.web;

import java.util.Collection;
import java.util.Set;

public interface IWebPath extends IWebEntity {
	IWebHost getHost();
	String getPath();
	IWebPath getParentPath();
	Collection<IWebPath> getChildPaths();
	Collection<IWebGetTarget> getTargets();
	IWebPath getChildPath(String path);
	IWebPath getOrCreateChildPath(String path);
	String getFullPath();
	Set<IWebPath> getUnvisitedPaths();
	IWebGetTarget addGetTarget(String query, String mimeType);
}
