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
package com.subgraph.vega.ui.scanner.alerts;

import java.util.Collection;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;

public interface IAlertTreeNode {
	String getKey();
	void remove();
	void addAlert(IScanAlert alert);
	void removeAlert(IScanAlert alert);
	boolean hasChildren();
	Collection<IScanAlert> getAlerts();
	int getAlertCount();
	Object[] getChildren();
	String getLabel();
	String getImage();
	IScanInstance getScanInstance();
}
