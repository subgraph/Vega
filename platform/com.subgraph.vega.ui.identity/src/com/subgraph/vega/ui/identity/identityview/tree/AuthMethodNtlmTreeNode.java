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
import com.subgraph.vega.api.model.identity.IAuthMethodNtlm;

public class AuthMethodNtlmTreeNode implements IIdentityTreeNode {
	private final IAuthMethodNtlm authMethod;
	private final List<IIdentityTreeNode> childrenList = new ArrayList<IIdentityTreeNode>();

	public AuthMethodNtlmTreeNode(IAuthMethod authMethod) {
		this.authMethod = (IAuthMethodNtlm) authMethod;
		childrenList.add(new StringTreeNode("Username: " + this.authMethod.getUsername()));
		childrenList.add(new StringTreeNode("Password: " + this.authMethod.getPassword()));
		childrenList.add(new StringTreeNode("Workstation: " + this.authMethod.getWorkstation()));
		childrenList.add(new StringTreeNode("Domain: " + this.authMethod.getDomain()));
	}

	@Override
	public String getText() {
		return "NTLMv1/NTLMv2/NTLM2 Authentication";
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
