package com.subgraph.vega.ui.httpviewer;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public class HttpMessageTextRenderer {

	public String getRequestAsText(HttpRequest request) {
		final StringBuilder sb = new StringBuilder();
		renderRequestLine(sb, request);
		renderAllHeaders(sb, request);
		return sb.toString();
	}
		
	public String getResponseAsText(HttpResponse response) {
		final StringBuilder sb = new StringBuilder();
		renderStatusLine(sb, response);
		renderAllHeaders(sb, response);		
		return sb.toString();
	}
	
	private void renderRequestLine(StringBuilder sb, HttpRequest request) {
		sb.append(request.getRequestLine().toString());
		sb.append('\n');
	}
	private void renderStatusLine(StringBuilder sb, HttpResponse response) {
		sb.append(response.getStatusLine().toString());
		sb.append('\n');
	}
	
	private void renderAllHeaders(StringBuilder sb, HttpMessage message) {
		for(Header h: message.getAllHeaders()) 
			renderHeader(sb, h);
	}

	private void renderHeader(StringBuilder sb, Header header) {
		sb.append(header.getName());
		sb.append(": ");
		sb.append(header.getValue());
		sb.append("\n");
	}
}
