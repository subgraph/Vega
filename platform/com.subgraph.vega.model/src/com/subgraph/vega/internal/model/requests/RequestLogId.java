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

public class RequestLogId {
	private long currentId = 0;
	
	synchronized long allocateId() {
		final long ret = currentId;
		currentId++;
		return ret;
	}
	
	synchronized long getCurrentId() {
		return currentId;
	}
}
