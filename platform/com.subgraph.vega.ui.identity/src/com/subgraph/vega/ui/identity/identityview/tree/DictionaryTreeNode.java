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
package com.subgraph.vega.ui.identity.identityview.tree;

import com.subgraph.vega.api.model.identity.IIdentity;

public class DictionaryTreeNode implements IIdentityTreeNode {
	private static final String IMAGE_PATH = "icons/dictionary.png";
	private final IIdentity identity;

	public DictionaryTreeNode(IIdentity identity) {
		this.identity = identity;
	}

	@Override
	public String getText() {
		return "Dictionary";
	}

	@Override
	public String getImagePath() {
		return IMAGE_PATH;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

}
