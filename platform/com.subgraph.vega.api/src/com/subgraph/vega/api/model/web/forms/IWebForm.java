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
package com.subgraph.vega.api.model.web.forms;

import java.net.URI;
import java.util.Collection;

public interface IWebForm {
	enum FormMethod { METHOD_GET, METHOD_POST };
	enum EncType { FORM_URLENCODED, FORM_DATA };
	
	EncType getEncodingType();
	FormMethod getMethod();
	URI getAction();
	Collection<IWebFormField> getFields();
}
