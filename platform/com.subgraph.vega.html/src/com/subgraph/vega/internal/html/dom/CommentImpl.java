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
package com.subgraph.vega.internal.html.dom;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CommentImpl extends CharacterDataImpl implements Comment {
	
	CommentImpl(org.jsoup.nodes.Comment jsoupComment, Document ownerDocument) {
		super(jsoupComment, jsoupComment.getData(), ownerDocument);
	}
	
	public String getNodeName() {
		return "#comment";
	}
	
	@Override
	public short getNodeType() {
		return Node.COMMENT_NODE;
	}
}
