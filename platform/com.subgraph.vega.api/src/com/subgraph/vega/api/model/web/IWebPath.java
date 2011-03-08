package com.subgraph.vega.api.model.web;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;

public interface IWebPath extends IWebEntity {
	enum PathType { PATH_UNKNOWN, PATH_DIRECTORY, PATH_FILE };
	IWebPath getParentPath();
	String getPathComponent();
	String getFullPath();
	URI getUri();
	IWebPath addChildPath(String pathComponent);
	IWebPath getChildPath(String pathComponent);
	IWebMountPoint getMountPoint();
	Collection<IWebPath> getChildPaths();
	
	void setPathType(PathType type);
	PathType getPathType();
	boolean isGetTarget();
	boolean isPostTarget();
	
	void addGetParameterList(List<NameValuePair> params);
	void addPostParameterList(List<NameValuePair> params);

	IWebPathParameters getGetParameters();
	IWebPathParameters getPostParameters();
	
	List<IWebResponse> getGetResponses();
	List<IWebResponse> getPostResponses();
	
	void addGetResponse(String query, String mimeType);
	void addPostResponse(List<NameValuePair> parameters, String mimeType);
}
