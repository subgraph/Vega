/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.internal.model.macros;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.http.requests.IHttpMacroContext;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.http.requests.custom.HttpEntityEnclosingMutableRequest;
import com.subgraph.vega.http.requests.custom.HttpMutableRequest;

public class HttpMacroItem implements IHttpMacroItem, Activatable {
	// Headers to remove when copying headers while generating a request
	private final static String[] HEADERS_RM = {
		// Hop-by-hop headers specified in RFC2616 section 13.5.1.
		HTTP.CONN_DIRECTIVE, // "Connection"
		HTTP.CONN_KEEP_ALIVE, // "Keep-Alive"
		"Proxy-Authenticate",
		"Proxy-Authorization",
		"TE",
		"Trailers",
		HTTP.TRANSFER_ENCODING, // "Transfer-Encoding"
		"Upgrade",

		// Not part of the RFC but should not be forwarded; see http://homepage.ntlworld.com/jonathan.deboynepollard/FGA/web-proxy-connection-header.html
		"Proxy-Connection",

		// Others
		HTTP.CONTENT_LEN,
		HTTP.CONTENT_TYPE,
		"Cookie",
	};

	private transient Activator activator;
	private IRequestLogRecord requestLogRecord;
	private boolean useCookies;
	private boolean keepCookies;
	private ActivatableHashMap<String, IHttpMacroItemParam> paramDict;

	public HttpMacroItem(IRequestLogRecord requestLogRecord) {
		this.requestLogRecord = requestLogRecord;
		useCookies = true;
		keepCookies = true;
		paramDict = new ActivatableHashMap<String, IHttpMacroItemParam>();
		createParams();
	}

	@Override
	public IRequestLogRecord getRequestLogRecord() {
		activate(ActivationPurpose.READ);
		return requestLogRecord;
	}

	@Override
	public void setUseCookies(boolean useCookies) {
		activate(ActivationPurpose.READ);
		this.useCookies = useCookies;
		activate(ActivationPurpose.WRITE);

	}

	@Override
	public boolean getUseCookies() {
		activate(ActivationPurpose.READ);
		return useCookies;
	}

	@Override
	public void setKeepCookies(boolean keepCookies) {
		activate(ActivationPurpose.READ);
		this.keepCookies = keepCookies;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public boolean getKeepCookies() {
		activate(ActivationPurpose.READ);
		return keepCookies;
	}

	@Override
	public Collection<IHttpMacroItemParam> getParams() {
		activate(ActivationPurpose.READ);
		return paramDict.values();
	}

	@Override
	public IHttpMacroItemParam getParam(String name) {
		activate(ActivationPurpose.READ);
		return paramDict.get(name);
	}

	@Override
	public HttpUriRequest createRequest(IHttpMacroContext context) throws URISyntaxException, UnsupportedEncodingException {
		activate(ActivationPurpose.READ);
		HttpRequest request = requestLogRecord.getRequest();
		URI uri = createRequestUri(request, requestLogRecord.getHttpHost(), context);
		String body = createRequestBody(context);
		HttpUriRequest uriRequest;
		if (body.length() != 0) {
			HttpEntityEnclosingMutableRequest tmp = new HttpEntityEnclosingMutableRequest(request.getRequestLine().getMethod(), uri);
			StringEntity entity = new StringEntity(body, "UTF-8");
			entity.setContentType("application/x-www-form-urlencoded");
			tmp.setEntity(entity);
			uriRequest = tmp;
		} else {
			uriRequest = new HttpMutableRequest(request.getRequestLine().getMethod(), uri);
		}
		uriRequest.setParams(request.getParams());
		uriRequest.setHeaders(copyHeaders(request.getAllHeaders()));
		return uriRequest;
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if (activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if (this.activator == activator) {
			return;
		}
		if (activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		this.activator = activator;			
	}

	// XXX clean this
	private void createParams() {
		HttpRequest request = requestLogRecord.getRequest();

		URI uri = null;
		try {
			uri = new URI(request.getRequestLine().getUri());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<NameValuePair> requestParamList = URLEncodedUtils.parse(uri, "UTF-8");
		for (NameValuePair pair: requestParamList) {
			paramDict.put(pair.getName(), new HttpMacroItemParam(pair.getName(), pair.getValue(), IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_URI));
		}		

		if (request instanceof HttpEntityEnclosingRequest) {
			List<NameValuePair> entityParamList = null;
			try {
				entityParamList = URLEncodedUtils.parse(((HttpEntityEnclosingRequest) request).getEntity());
			} catch (IOException e) {
				// there won't be any exceptions for a record from the database
			}
			for (NameValuePair pair: entityParamList) {
				paramDict.put(pair.getName(), new HttpMacroItemParam(pair.getName(), pair.getValue(), IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_BODY));
			}
		}
	}

	private URI createRequestUri(HttpRequest request, HttpHost host, IHttpMacroContext context) throws URISyntaxException {
		StringBuilder buf = new StringBuilder();
		int cnt = 0;
		for (IHttpMacroItemParam param: paramDict.values()) {
			if (param.getSetIn() == IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_URI) {
				if (cnt != 0) {
					buf.append('&');
				}
				buf.append(param.getName());
				buf.append('=');
				buf.append(param.getValue());
				cnt++;
			}
		}
		URI requestUri = new URI(request.getRequestLine().getUri());
		String query;
		if (buf.length() != 0) {
			query = buf.toString();
		} else {
			query = null;
		}
		return new URI(host.getSchemeName(), null, host.getHostName(), host.getPort(), requestUri.getRawPath(), query, null);
	}

	private String createRequestBody(IHttpMacroContext context) {
		ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
		for (IHttpMacroItemParam param: paramDict.values()) {
			if (param.getSetIn() == IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_BODY) {
				paramList.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}
		return URLEncodedUtils.format(paramList, "UTF-8");
	}

	private Header[] copyHeaders(Header[] headers) {
		ArrayList<Header> headerList = new ArrayList<Header>();
		for (Header header: headers) {
			int idx;
			for (idx = 0; idx < HEADERS_RM.length; idx++) {
				if (HEADERS_RM[idx].equals(header.getName())) {
					break;
				}
			}
			if (idx == HEADERS_RM.length) {
				headerList.add(header);
			}
		}
		return headerList.toArray(new Header[0]);
	}

}
