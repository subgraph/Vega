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
package com.subgraph.vega.ui.util.modules;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.api.scanner.modules.IScannerModule;

public class ModuleRegistryLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if (element instanceof IScannerModule)
			return ((IScannerModule) element).getModuleName();
		else if (element instanceof String)
			return (String) element;
		else
			return null;
	}
}
