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

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.identity.IIdentity;

public class IdentityTreeNode implements IIdentityTreeNode {
	private static final String IMAGE_PATH = "icons/identity.png";
	private final IIdentity identity;
	private final List<IIdentityTreeNode> childrenList = new ArrayList<IIdentityTreeNode>();

	public IdentityTreeNode(IIdentity identity) {
		this.identity = identity;
		childrenList.add(new DictionaryTreeNode(identity));
		childrenList.add(new AuthenticationTreeNode(identity));
		childrenList.add(new ScanExclusionsTreeNode(identity));
	}

	@Override
	public String getText() {
		return identity.getName();
	}

	@Override
	public String getImagePath() {
		return IMAGE_PATH;
	}

	@Override
	public boolean hasChildren() {
		return (childrenList.size() > 0);
	}

	@Override
	public Object[] getChildren() {
		return childrenList.toArray();
	}

}
