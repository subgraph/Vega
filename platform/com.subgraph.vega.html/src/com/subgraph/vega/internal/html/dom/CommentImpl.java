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
