package com.subgraph.vega.internal.http.requests;

import java.util.Date;

import org.apache.http.cookie.ClientCookie;

import com.subgraph.vega.api.http.requests.IHttpResponseCookie;

public class HttpResponseCookie implements IHttpResponseCookie {
	private final String header;
	private final ClientCookie cookie;
	
	HttpResponseCookie(String header, ClientCookie cookie) {
		this.header = header;
		this.cookie = cookie;
	}

	@Override
	public String getAttribute(String name) {
		return cookie.getAttribute(name);
	}

	@Override
	public boolean containsAttribute(String name) {
		return cookie.containsAttribute(name);
	}

	@Override
	public String getName() {
		return cookie.getName();
	}

	@Override
	public String getValue() {
		return cookie.getValue();
	}

	@Override
	public String getComment() {
		return cookie.getComment();
	}

	@Override
	public String getCommentURL() {
		return cookie.getCommentURL();
	}

	@Override
	public Date getExpiryDate() {
		return cookie.getExpiryDate();
	}

	@Override
	public boolean isPersistent() {
		return cookie.isPersistent();
	}

	@Override
	public String getDomain() {
		return cookie.getDomain();
	}

	@Override
	public String getPath() {
		return cookie.getPath();
	}

	@Override
	public int[] getPorts() {
		return cookie.getPorts();
	}

	@Override
	public boolean isSecure() {
		return cookie.isSecure();
	}

	@Override
	public int getVersion() {
		return cookie.getVersion();
	}

	@Override
	public boolean isExpired(Date date) {
		return cookie.isExpired(date);
	}

	@Override
	public String getHeader() {
		return header;
	}
	
	@Override
	public String toString() {
		return cookie.toString();
	}
}
