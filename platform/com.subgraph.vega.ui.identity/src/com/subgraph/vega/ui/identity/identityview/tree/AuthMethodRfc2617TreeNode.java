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
import com.subgraph.vega.api.model.identity.IAuthMethodRfc2617;

public class AuthMethodRfc2617TreeNode implements IIdentityTreeNode {
	private IAuthMethodRfc2617 authMethod;
	private final List<IIdentityTreeNode> childrenList = new ArrayList<IIdentityTreeNode>();

	public AuthMethodRfc2617TreeNode(IAuthMethod authMethod) {
		this.authMethod = (IAuthMethodRfc2617) authMethod;
		childrenList.add(new StringTreeNode("Username: " + this.authMethod.getUsername()));
		childrenList.add(new StringTreeNode("Password: " + this.authMethod.getPassword()));
	}

	@Override
	public String getText() {
		StringBuffer buf = new StringBuffer();
		if (authMethod.getAuthScheme() == IAuthMethodRfc2617.AuthScheme.AUTH_SCHEME_BASIC) {
			buf.append("Basic");
		} else {
			buf.append("Digest");
		}
		buf.append(" Authentication");
		return buf.toString();
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
