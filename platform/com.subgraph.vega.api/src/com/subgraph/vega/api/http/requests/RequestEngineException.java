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

public class RequestEngineException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public RequestEngineException(String message) {
		super(message);
	}
	
	public RequestEngineException(String message, Throwable ex) {
		super(message, ex);
	}
}
