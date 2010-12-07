package com.subgraph.vega.api.model.web;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;

public interface IWebPath extends IWebEntity {
	IWebPath getParentPath();
	String getPathComponent();
	String getFullPath();
	URI getUri();
	IWebPath addChildPath(String pathComponent);
	IWebMountPoint getMountPoint();
	Collection<IWebPath> getChildPaths();
	
	boolean isGetTarget();
	boolean isPostTarget();
	IWebPathParameters getGetParameters();
	IWebPathParameters getPostParameters();
	
	List<IWebResponse> getGetResponses();
	List<IWebResponse> getPostResponses();
	
	void addGetResponse(String query, String mimeType);
	void addPostResponse(List<NameValuePair> parameters, String mimeType);
}
