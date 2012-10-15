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
package com.subgraph.vega.api.model.alerts;

import java.util.Collection;

import com.subgraph.vega.api.model.IModelProperties;

public interface IScanAlert extends IModelProperties {
	enum Severity { HIGH, MEDIUM, LOW, INFO, UNKNOWN };
	String getName();
	Severity getSeverity();
	String getTitle();
	String getResource();
	void setTemplateName(String name);
	String getTemplateName();
	String getKey();
	boolean hasAssociatedRequest();
	IScanInstance getScanInstance();
	long getScanId();
	long getRequestId();
	void addStringMatchHighlight(String matchStr);
	void addRegexHighlight(String regex);
	void addRegexCaseInsensitiveHighlight(String regex);
	Collection<IScanAlertHighlight> getHighlights();
}
