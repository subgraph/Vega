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
package com.subgraph.vega.api.model.conditions.match;

public interface IHttpConditionMatchAction {
	enum MatchActionArgumentType { ARGUMENT_REGEX, ARGUMENT_STRING, ARGUMENT_INTEGER, ARGUMENT_RANGE };
	
	String getLabel();
	MatchActionArgumentType getArgumentType();
	String getArgumentAsString();
	boolean setArgumentFromString(String value);
	boolean isValidArgumentString(String value);
}

