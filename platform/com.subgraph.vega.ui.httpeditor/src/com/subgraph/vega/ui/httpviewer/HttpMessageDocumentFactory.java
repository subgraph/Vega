package com.subgraph.vega.ui.httpviewer;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.httpviewer.partitioning.HeaderPartitionRule;
import com.subgraph.vega.ui.httpviewer.partitioning.RequestLinePartitionRule;
import com.subgraph.vega.ui.httpviewer.partitioning.ResponseLinePartitionRule;

public class HttpMessageDocumentFactory {

	public final static String PARTITION_REQUEST_LINE = "http-request-line";
	public final static String PARTITION_RESPONSE_LINE = "http-response-line";
	public final static String PARTITION_MESSAGE_HEADER = "http-header";

	public final static String[] PARTITION_TYPES = { 
		PARTITION_REQUEST_LINE, 
		PARTITION_RESPONSE_LINE, 
		PARTITION_MESSAGE_HEADER 
	};
	
	private final HttpMessageTextRenderer messageRenderer = new HttpMessageTextRenderer();
	
	public IDocument createDocumentForRequest(HttpRequest request, boolean uriDecode) {
		final String text = messageRenderer.getRequestAsText(request);
		return createDocumentForText((uriDecode) ? (decodeContent(text)) : (text));
	}
	
	public IDocument createDocumentForRequest(IHttpRequestBuilder builder, boolean uriDecode) {	
		final String text = messageRenderer.getRequestAsText(builder);
		return createDocumentForText((uriDecode) ? (decodeContent(text)) : (text));
	}

	public IDocument createDocumentForResponse(HttpResponse response, boolean uriDecode) {
		final String text = messageRenderer.getResponseAsText(response);
		return createDocumentForText((uriDecode) ? (decodeContent(text)) : (text));
	}
	
	public IDocument createDocumentForResponse(IHttpResponseBuilder builder, boolean uriDecode) {
		final String text = messageRenderer.getResponseAsText(builder);
		return createDocumentForText((uriDecode) ? (decodeContent(text)) : (text));
	}

	public IDocument createDocumentForText(String text) {
		final IDocument document = createDocument();
		document.set(text);
		return document;
	}

	private IDocument createDocument() {
		final Document document = new Document();
		final IDocumentPartitioner partitioner = createPartitioner();
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		return document;
	}
	
	private IDocumentPartitioner createPartitioner() {
		final RuleBasedPartitionScanner scanner = new RuleBasedPartitionScanner();
		scanner.setPredicateRules(createRules());
		return new FastPartitioner(scanner, PARTITION_TYPES);
	}
	
	private IPredicateRule[] createRules() {
		final List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		rules.add(new RequestLinePartitionRule(new Token(PARTITION_REQUEST_LINE)));
		rules.add(new ResponseLinePartitionRule(new Token(PARTITION_RESPONSE_LINE)));
		rules.add(new HeaderPartitionRule(new Token(PARTITION_MESSAGE_HEADER)));
		return rules.toArray(new IPredicateRule[0]);
	}
	
	private String decodeContent(String content) {
		if(content.length() < 3)
			return content;
		
		final StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if(c == '%') {
				i += processPercentEscape(sb, content.substring(i));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private int processPercentEscape(StringBuilder sb, String s) {
		if(s.length() > 1 && s.charAt(1) == '%') {
			sb.append('%');
			return 1;
		} else if(s.length() > 2 && isHex(s.charAt(1))  && isHex(s.charAt(2))) {
			sb.append(decode(s.charAt(1), s.charAt(2)));
			return 2;
		} else {
			sb.append('%');
			return 0;
		}
	}

	private static final String HEX_DIGITS = "01234567890abcdef";
	
	private boolean isHex(char c) {
		return (HEX_DIGITS.indexOf(Character.toLowerCase(c)) != -1);
	}
	
	private char decode(char hi, char low) {
		StringBuilder sb = new StringBuilder();
		sb.append(hi);
		sb.append(low);
		return (char) Integer.parseInt(sb.toString(), 16);
	}
}
