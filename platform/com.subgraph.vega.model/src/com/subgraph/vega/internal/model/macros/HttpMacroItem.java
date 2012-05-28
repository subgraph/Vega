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
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableArrayList;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.http.requests.IHttpHeaderBuilder;
import com.subgraph.vega.api.http.requests.IHttpMacroContext;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam.ValueSetIn;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.http.requests.builder.HttpRequestBuilder;

public class HttpMacroItem implements IHttpMacroItem, Activatable {
	// Headers to remove when copying headers while generating a request.
	private final static String[] HEADERS_RM = {
		HTTP.CONN_DIRECTIVE, // "Connection"
		HTTP.CONN_KEEP_ALIVE, // "Keep-Alive"
		"Proxy-Authenticate",
		"Proxy-Authorization",
		"TE",
		"Trailers",
		HTTP.TRANSFER_ENCODING, // "Transfer-Encoding"
		"Upgrade",
		"Proxy-Connection",
		HTTP.CONTENT_LEN,
		HTTP.CONTENT_TYPE,
		"Cookie",
	};

	private transient Activator activator;
	private IRequestLogRecord requestLogRecord;
	private HttpParams requestParams;
	private String requestMethod;
	private ProtocolVersion requestProtocolVersion;
	private String requestScheme;
	private String requestHost;
	private int requestPort;
	private String requestPath; /** Raw path of request */
	private final ArrayList<Header> requestHeaderList = new ArrayList<Header>();
	private boolean useCookies;
	private boolean keepCookies;
	private ActivatableArrayList<IHttpMacroItemParam> paramList;

	public HttpMacroItem(IRequestLogRecord requestLogRecord) throws URISyntaxException, IOException {
		this.requestLogRecord = requestLogRecord;
		useCookies = true;
		keepCookies = true;
		final HttpRequest request = requestLogRecord.getRequest(); 
		processRequest(request, requestLogRecord.getHttpHost());
		paramList = new ActivatableArrayList<IHttpMacroItemParam>();
		processParams(request);
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
	public IHttpMacroItemParam createParam(String name, String value, ValueSetIn setIn) {
		HttpMacroItemParam param = new HttpMacroItemParam(name, value, setIn);
		paramList.add(param);
		return param;
	}

	@Override
	public void removeParam(IHttpMacroItemParam param) {
		paramList.remove(param);
	}

	@Override
	public int paramsSize() {
		return paramList.size();
	}

	@Override
	public int indexOfParam(IHttpMacroItemParam param) {
		return paramList.indexOf(param);
	}

	@Override
	public void swapParams(int idx1, int idx2) {
		if (idx1 < paramList.size() && idx2 < paramList.size() && idx1 != idx2) {
			final IHttpMacroItemParam tmp = paramList.set(idx1, paramList.get(idx2));
			paramList.set(idx2, tmp);
		}
	}

	@Override
	public Collection<IHttpMacroItemParam> getParams() {
		return new ArrayList<IHttpMacroItemParam>(paramList);
	}

	@Override
	public IHttpMacroItemParam[] getParam(String name) {
		final ArrayList<IHttpMacroItemParam> tmp = new ArrayList<IHttpMacroItemParam>();
		for (IHttpMacroItemParam param: paramList) {
			if (param.getName().equals(name)) {
				tmp.add(param);
			}
		}
		return tmp.toArray(new IHttpMacroItemParam[0]);
	}

	@Override
	public HttpUriRequest createRequest(IHttpMacroContext context) throws UnsupportedEncodingException, URISyntaxException {
		final HttpRequestBuilder builder = new HttpRequestBuilder();
		setRequestBuilder(builder, context);
		return builder.buildRequest(false);
	}

	@Override
	public void setRequestBuilder(IHttpRequestBuilder requestBuilder, IHttpMacroContext context) throws UnsupportedEncodingException, URISyntaxException {
		activate(ActivationPurpose.READ);
		setRequestParams(requestBuilder);
		setRequestLine(requestBuilder, context);
		setRequestHeaders(requestBuilder);
		setRequestBody(requestBuilder, context);
	}

	@Override
	public void updateFromRequestBuilder(IHttpRequestBuilder requestBuilder) throws URISyntaxException {
		activate(ActivationPurpose.READ);
		requestParams = requestBuilder.getParams().copy();
		requestMethod = requestBuilder.getMethod();
		requestScheme = requestBuilder.getScheme();
		requestHost = requestBuilder.getHost();
		requestPort = requestBuilder.getHostPort();
		final URI uri = new URI(requestBuilder.getPath());
		requestPath = uri.getRawPath();
		requestProtocolVersion = requestBuilder.getProtocolVersion();
		requestHeaderList.clear();
		for (IHttpHeaderBuilder header: requestBuilder.getHeaders()) {
			requestHeaderList.add(header.buildHeader());
		}
		activate(ActivationPurpose.WRITE);

		// REVISIT: we need to process the request body and URI for parameters 
	}

	private void setRequestParams(IHttpRequestBuilder requestBuilder) {
		requestBuilder.setParams(requestParams.copy());
	}

	private void setRequestLine(IHttpRequestBuilder requestBuilder, IHttpMacroContext context) throws URISyntaxException {
		requestBuilder.setMethod(requestMethod);
		setRequestUri(requestBuilder, context);
		requestBuilder.setProtocolVersion(requestProtocolVersion);
	}
	
	private void setRequestUri(IHttpRequestBuilder requestBuilder, IHttpMacroContext context) throws URISyntaxException {
		requestBuilder.setScheme(requestScheme);
		requestBuilder.setHost(requestHost);
		requestBuilder.setHostPort(requestPort);
		final String query = createUriQuery(context);
		String path = requestPath;
		if (query.length() != 0) {
			path += "?" + query;
		}
		requestBuilder.setPath(path);
	}
	
	private void setRequestHeaders(IHttpRequestBuilder requestBuilder) {
		requestBuilder.setHeaders(requestHeaderList.toArray(new Header[0]));
	}
	
	private void setRequestBody(IHttpRequestBuilder requestBuilder, IHttpMacroContext context) throws UnsupportedEncodingException {
		ArrayList<NameValuePair> bodyParamList = new ArrayList<NameValuePair>();
		for (IHttpMacroItemParam param: paramList) {
			if (param.getSetIn() == IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_BODY) {
				bodyParamList.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}

		if (bodyParamList.size() != 0) {
			final StringEntity entity = new StringEntity(URLEncodedUtils.format(bodyParamList, "UTF-8"), "UTF-8");
			entity.setContentType("application/x-www-form-urlencoded");
			requestBuilder.setEntity(entity);
			requestBuilder.setMethod("POST");
		}
	}

	private String createUriQuery(IHttpMacroContext context) {
		StringBuilder buf = new StringBuilder();
		for (IHttpMacroItemParam param: paramList) {
			if (param.getSetIn() == IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_URI) {
				if (buf.length() != 0) {
					buf.append('&');
				}
				buf.append(param.getName());
				buf.append('=');
				buf.append(param.getValue());
			}
		}
		return buf.toString();
	}
		
	private void processRequest(HttpRequest request, HttpHost host) throws URISyntaxException {
		requestParams = request.getParams().copy();
		final RequestLine requestLine = request.getRequestLine();		
		requestMethod = requestLine.getMethod();
		requestScheme = host.getSchemeName();
		requestHost = host.getHostName();
		requestPort = host.getPort();
		final URI uri = new URI(request.getRequestLine().getUri());
		requestPath = uri.getRawPath();
		requestProtocolVersion = requestLine.getProtocolVersion();
		requestHeaderList.clear();
		Collections.addAll(requestHeaderList, copyHeaders(request.getAllHeaders()));
	}
	
	private void processParams(HttpRequest request) throws URISyntaxException, IOException {
		processParamsUri(request);
		processParamsBody(request);
	}
	
	private void processParamsUri(HttpRequest request) throws URISyntaxException {
		URI uri = new URI(request.getRequestLine().getUri());
		List<NameValuePair> requestParamList = URLEncodedUtils.parse(uri, "UTF-8");
		for (NameValuePair pair: requestParamList) {
			paramList.add(new HttpMacroItemParam(pair.getName(), pair.getValue(), IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_URI));
		}
	}

	private void processParamsBody(HttpRequest request) throws IOException {
		if (request instanceof HttpEntityEnclosingRequest) {
			List<NameValuePair> entityParamList = null;
			entityParamList = URLEncodedUtils.parse(((HttpEntityEnclosingRequest) request).getEntity());
			for (NameValuePair pair: entityParamList) {
				paramList.add(new HttpMacroItemParam(pair.getName(), pair.getValue(), IHttpMacroItemParam.ValueSetIn.VALUE_SET_IN_BODY));
			}
		}
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

}
