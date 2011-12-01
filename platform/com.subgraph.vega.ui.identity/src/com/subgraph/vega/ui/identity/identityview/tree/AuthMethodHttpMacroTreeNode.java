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

import com.subgraph.vega.api.model.identity.IAuthMethod;
import com.subgraph.vega.api.model.identity.IAuthMethodHttpMacro;

public class AuthMethodHttpMacroTreeNode implements IIdentityTreeNode {
	private final IAuthMethodHttpMacro authMethod;
	private final List<IIdentityTreeNode> childrenList = new ArrayList<IIdentityTreeNode>();

	public AuthMethodHttpMacroTreeNode(IAuthMethod authMethod) {
		this.authMethod = (IAuthMethodHttpMacro) authMethod;
		childrenList.add(new StringTreeNode("Macro: " + this.authMethod.getMacro().getName()));
	}

	@Override
	public String getText() {
		return "HTTP Macro Authentication";
	}

	@Override
	public String getImagePath() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object[] getChildren() {
		return childrenList.toArray();
	}

}
