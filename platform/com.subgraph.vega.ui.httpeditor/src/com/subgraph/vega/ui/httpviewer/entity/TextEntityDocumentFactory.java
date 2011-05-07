package com.subgraph.vega.ui.httpviewer.entity;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

import com.subgraph.vega.ui.httpviewer.html.HtmlPartitionScanner;
import com.subgraph.vega.ui.httpviewer.syntax.js.JavascriptPartitionScanner;
import com.subgraph.vega.ui.httpviewer.syntax.js.formatter.JavascriptFormatter;

public class TextEntityDocumentFactory {
	private final static int MAX_LONG_LINE = 200;
	
	IDocument createDocument(String text, String contentType) {
		final String lower = contentType.toLowerCase();
		if(isJavascript(lower)) {
			return createJavascriptDocument(text);
		} else if (isHtml(lower)) {
			return createHtmlDocument(text);
		} else {
			return createBasicDocument(text);
		}
	}
	
	private IDocument createJavascriptDocument(String text) {
		final Document document = new Document();
		final IDocumentPartitioner partitioner = new FastPartitioner(new JavascriptPartitionScanner(), JavascriptPartitionScanner.JS_PARTITION_TYPES);
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		JavascriptFormatter formatter = new JavascriptFormatter();
		document.set(formatter.format(text));
		return document;
	}

	private IDocument createHtmlDocument(String text) {
		final IDocument document = new Document();
		final IDocumentPartitioner partitioner = new FastPartitioner(new HtmlPartitionScanner(), HtmlPartitionScanner.HTML_TYPES);
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		document.set(longLineFormatter(text, '>'));
		return document;
	}
	
	private String longLineFormatter(String input, char splitAt) {
		final StringBuilder sb = new StringBuilder();
		int count = 0;
		char c;
		for(int i = 0; i < input.length(); i++) {
			count += 1;
			c = input.charAt(i);
			sb.append(c);
			if(count > MAX_LONG_LINE && c == splitAt) {
				sb.append("\n");
				count = 0;
			}
		}
		return sb.toString();
	}
	
	private IDocument createBasicDocument(String text) {
		final Document document = new Document();
		document.set(longLineFormatter(text, ' '));
		return document;
	}
	
	private boolean isJavascript(String contentType) {
		return contentType.contains("javascript") || contentType.contains("json");
	}
	
	private boolean isHtml(String contentType) {
		return contentType.contains("html");
	}
}
