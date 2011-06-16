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
package com.subgraph.vega.internal.model.requests;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import com.db4o.ObjectContainer;

public class LazyEntityLoader {
	
	private final long entityId;
	private final ObjectContainer database;
	
	LazyEntityLoader(long entityId, ObjectContainer database) {
		this.entityId = entityId;
		this.database = database;
	}
	
	HttpEntity getEntity() {
		if(entityId == 0) {
			return null;
		}
		
		final RequestLogEntity e = database.ext().getByID(entityId);
		if(e == null) {
			return null;
		}
		database.ext().activate(e);
		final HttpEntity copy = createEntityCopy(e);
		database.ext().deactivate(e, 1);
		database.ext().purge(e);
		return copy;
	}
	
	private HttpEntity createEntityCopy(RequestLogEntity entity) {
		final ByteArrayEntity copy = new ByteArrayEntity(entity.getContentArray());
		copy.setContentType(entity.getContentType());
		copy.setContentEncoding(copy.getContentEncoding());
		return copy;
	}
}
