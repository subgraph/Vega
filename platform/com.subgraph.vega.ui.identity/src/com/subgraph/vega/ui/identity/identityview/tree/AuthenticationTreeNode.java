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
import com.subgraph.vega.api.model.identity.IIdentity;

public class AuthenticationTreeNode implements IIdentityTreeNode {
	private static final String IMAGE_PATH = "icons/authentication.png";
	private final IIdentity identity;
	private IAuthMethod authMethod;
	private List<IIdentityTreeNode> childrenList;

	public AuthenticationTreeNode(IIdentity identity) {
		this.identity = identity;
	}

	@Override
	public String getText() {
		return "Authentication";
	}

	@Override
	public String getImagePath() {
		return IMAGE_PATH;
	}

	@Override
	public boolean hasChildren() {
		IAuthMethod authMethodUpdt = identity.getAuthMethod();
		if (authMethodUpdt != authMethod) {
			if (childrenList == null) {
				childrenList = new ArrayList<IIdentityTreeNode>();
			} else {
				childrenList.clear();
			}

			authMethod = authMethodUpdt;
			switch (authMethod.getType()) {
			case AUTH_METHOD_RFC2617:
				childrenList.add(new AuthMethodRfc2617TreeNode(authMethod));
				break;
			case AUTH_METHOD_NTLM:
				childrenList.add(new AuthMethodNtlmTreeNode(authMethod));
				break;
			case AUTH_METHOD_HTTP_MACRO:
				childrenList.add(new AuthMethodHttpMacroTreeNode(authMethod));
				break;
			}
		}
		return (childrenList != null && childrenList.size() != 0);
	}

	@Override
	public Object[] getChildren() {
		return childrenList.toArray();
	}

}
