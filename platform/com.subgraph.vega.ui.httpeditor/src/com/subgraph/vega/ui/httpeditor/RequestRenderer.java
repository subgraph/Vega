package com.subgraph.vega.ui.httpeditor;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;


public class RequestRenderer {

	public String renderRequestText(HttpRequest request) {
		return renderHeaders(renderRequestStartLine(request), request.getAllHeaders());
	}

	public String renderRequestText(IRequestLogRecord record) {
		return renderRequestText(record.getRequest());
	}
	
	public String renderResponseText(HttpResponse response) {
		return renderHeaders(renderResponseStartLine(response), response.getAllHeaders());
	}

	public String renderResponseText(IRequestLogRecord record) {
		return renderResponseText(record.getResponse());
	}
	
	private String renderRequestStartLine(HttpRequest request) {
		return request.getRequestLine().toString() + '\n';
	}

	private String renderResponseStartLine(HttpResponse response) {
		return response.getStatusLine().toString() + '\n';
	}
	
	private String renderHeaders(String startLine, Header[] headers) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(startLine);
		for(Header h: headers)
			buffer.append(h.getName() +": "+ h.getValue() +"\n");
		return buffer.toString();
	}

}