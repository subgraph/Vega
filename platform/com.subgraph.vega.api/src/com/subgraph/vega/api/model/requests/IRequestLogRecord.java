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
package com.subgraph.vega.api.model.requests;

import java.util.Collection;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.tags.ITag;

public interface IRequestLogRecord {
	long getRequestId();
	long getTimestamp();
	
	/**
	 * Get the end-to-end request execution time in milliseconds.
	 * @return Request execution time in milliseconds, or -1 when unknown.
	 */
	long getRequestMilliseconds();

	HttpHost getHttpHost();
	HttpRequest getRequest();
	HttpResponse getResponse();

	/**
	 * Get a collection of all tags applied to this record.
	 * @return Tags applied to this record.
	 */
	Collection<ITag> getAllTags();

	/**
	 * Add a tag to this record.
	 * @param tag Tag.
	 */
	void addTag(ITag tag);

	/**
	 * Remove a tag from this record.
	 * @param tag Tag.
	 */
	void removeTag(ITag tag);
}
