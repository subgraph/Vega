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
package com.subgraph.vega.impl.scanner.modules.scripting.dom;

import org.w3c.dom.Comment;

public class CommentJS extends NodeJS {
	private static final long serialVersionUID = 1L;
	
	public CommentJS() {
	}
	
	public CommentJS(Comment comment, DocumentJS document) {
		super(comment, document);
	}
	
	@Override
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "Comment";
	}
}
