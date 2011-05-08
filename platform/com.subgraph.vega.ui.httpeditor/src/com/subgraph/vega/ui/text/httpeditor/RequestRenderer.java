package com.subgraph.vega.ui.text.httpeditor;

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
		StringBuilder sb = new StringBuilder();
		sb.append(renderRequestStartLine(request));
		sb.append(renderHeaders(request.getAllHeaders()));
		if (request instanceof HttpEntityEnclosingRequest) {
			final HttpEntityEnclosingRequest requestEntity = (HttpEntityEnclosingRequest) request;
			final String body = renderEntityIfAscii(requestEntity.getEntity());
			if(body != null) {
				sb.append("\n");
				sb.append(body);
			}
		}
		return sb.toString();
	}

	public String renderRequestText(IRequestLogRecord record) {
		return renderRequestText(record.getRequest());
	}

	public String renderResponseText(HttpResponse response) {
		StringBuilder sb = new StringBuilder();
		sb.append(renderResponseStartLine(response));
		sb.append(renderHeaders(response.getAllHeaders()));
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
	
	public String renderEntity(HttpEntity entity) {
		return renderEntityIfAscii(entity);
	}
	
	private String renderRequestStartLine(HttpRequest request) {
		return request.getRequestLine().toString() + '\n';
	}

	private String renderResponseStartLine(HttpResponse response) {
		return response.getStatusLine().toString() + '\n';
	}
	
	private String renderHeaders(Header[] headers) {
		StringBuilder buffer = new StringBuilder();
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