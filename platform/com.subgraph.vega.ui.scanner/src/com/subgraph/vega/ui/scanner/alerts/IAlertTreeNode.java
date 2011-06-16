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

import com.subgraph.vega.api.model.alerts.IScanAlert;

public interface IAlertTreeNode {
	void addAlert(IScanAlert alert);
	boolean hasChildren();
	int getAlertCount();
	Object[] getChildren();
	String getLabel();
	String getImage();
}
