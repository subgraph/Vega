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
package com.subgraph.vega.api.http.requests;

import java.net.URI;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.model.tags.ITag;

public interface IHttpResponse {
	enum ResponseStatus { RESPONSE_OK };
	ResponseStatus getResponseStatus();
	URI getRequestUri();
	int getResponseCode();
	boolean isFetchFail();
	HttpRequest getOriginalRequest();
	void setRawResponse(HttpResponse response); // temporary, probably. used in interceptor.
	HttpResponse getRawResponse();
	HttpHost getHost();
	String getBodyAsString();
	IHTMLParseResult getParsedHTML();
	boolean isMostlyAscii();
	IPageFingerprint getPageFingerprint();
	boolean lockResponseEntity();
	long getRequestMilliseconds();
	
	/**
	 * Set the database ID of a IRequestLogRecord created for this object. Must only be invoked when a IRequestLogRecord
	 * is inserted into the database.
	 * @param requestId Database ID of IRequestLogRecord.
	 */
	void setRequestId(long requestId);

	/**
	 * Get the database ID of the IRequestLogRecord corresponding with this object.
	 * @return Database ID of IRequestLogRecord, or -1 if none has been set.
	 */
	long getRequestId();

	/**
	 * Get a list of all tags applied to this record.
	 * @return Tags applied to this record.
	 */
	List<ITag> getTags();

	/**
	 * Apply a tag to this record.
	 * @param tag Tag.
	 */
	void addTag(ITag tag);

	/**
	 * Remove a tag from this record.
	 * @param tag Tag.
	 */
	void removeTag(ITag tag);
}
