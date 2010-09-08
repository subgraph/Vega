package com.subgraph.vega.ui.httpeditor;

import org.apache.http.Header;


import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class RequestRenderer {

	public String renderRequestText(IRequestLogRecord record) {
		return renderHeaders(renderRequestStartLine(record), record.getRequest().getAllHeaders());
	}
	
	public String renderResponseText(IRequestLogRecord record) {
		return renderHeaders(renderResponseStartLine(record), record.getResponse().getAllHeaders());
	}
	
	private String renderRequestStartLine(IRequestLogRecord record) {
		return record.getRequest().getRequestLine().toString() + '\n';
	}
	
	private String renderResponseStartLine(IRequestLogRecord record) {
		return record.getResponse().getStatusLine().toString() + '\n';
	}
	
	private String renderHeaders(String startLine, Header[] headers) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(startLine);
		for(Header h: headers)
			buffer.append(h.getName() +": "+ h.getValue() +"\n");
		return buffer.toString();
				
	}	
}