package com.subgraph.vega.ui.httpeditor;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;


public class RequestRenderer {

	public String renderRequestText(HttpRequest request) {
		return renderHeaders(renderRequestStartLine(request), request.getAllHeaders());
	}

	public String renderRequestText(IRequestLogRecord record) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderRequestText(record.getRequest()));
		if(record.getRequest() instanceof HttpEntityEnclosingRequest) {
			final HttpEntityEnclosingRequest request = (HttpEntityEnclosingRequest) record.getRequest();
			final String body = renderEntityIfAscii(request.getEntity());
			if(body != null) {
				sb.append("\n");
				sb.append(body);
			}
		}
		return sb.toString();
	}
	
	public String renderResponseText(HttpResponse response) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderHeaders(renderResponseStartLine(response), response.getAllHeaders()));
		final String body = renderEntityIfAscii(response.getEntity());
		if(body != null) {
			sb.append("\n");
			sb.append(body);
		}
		return sb.toString();
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

	private String renderEntityIfAscii(HttpEntity entity) {
		final String body = entityAsString(entity);
		if(body == null || body.isEmpty())
			return null;
		
		final int total = (body.length() > 500) ? (500) : (body.length());
		int printable = 0;
		for(int i = 0; i < total; i++) {
			char c = body.charAt(i);
			if((c >= 0x20 && c <= 0x7F) || Character.isWhitespace(c))
				printable += 1;
		}
		if((printable * 100) / total > 90)
			return body;
		else
			return null;
	}
	
	private String entityAsString(HttpEntity entity) {
		if(entity == null)
			return null;
		try {
			return EntityUtils.toString(entity);
		} catch (ParseException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}