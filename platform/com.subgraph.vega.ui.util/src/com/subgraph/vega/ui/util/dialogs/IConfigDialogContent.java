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
package com.subgraph.vega.ui.util.dialogs;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IConfigDialogContent {
	Composite createContents(Composite parent);
	String getTitle();
	String getMessage();
	Control getFocusControl();
	void onClose();
	void onOk();
}
