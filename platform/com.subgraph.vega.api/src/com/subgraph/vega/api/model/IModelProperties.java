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
package com.subgraph.vega.api.model;

import java.util.List;

public interface IModelProperties {
	void setProperty(String name, Object value);
	void setStringProperty(String name, String value);
	void setIntegerProperty(String name, int value);
	Object getProperty(String name);
	String getStringProperty(String name);
	Integer getIntegerProperty(String name);
	List<String> propertyKeys();
}
