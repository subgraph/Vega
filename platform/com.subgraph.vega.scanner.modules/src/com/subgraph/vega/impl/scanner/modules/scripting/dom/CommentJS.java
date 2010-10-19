package com.subgraph.vega.impl.scanner.modules.scripting.dom;

import org.w3c.dom.Comment;

public class CommentJS extends NodeJS {
	private static final long serialVersionUID = 1L;
	
	public CommentJS() {
	}
	
	public CommentJS(Comment comment) {
		super(comment);
	}
	
	@Override
	public void jsConstructor(Object ob) {
	}
	
	@Override
	public String getClassName() {
		return "Comment";
	}
}
