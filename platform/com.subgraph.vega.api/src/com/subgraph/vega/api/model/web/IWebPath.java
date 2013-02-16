/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.api.model.web;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.apache.http.NameValuePair;

public interface IWebPath extends IWebEntity {
	enum PathType { PATH_UNKNOWN, PATH_DIRECTORY, PATH_FILE, PATH_PATHINFO };
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
	
	void setMimeType(String mimeType);
	String getMimeType();
	
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
