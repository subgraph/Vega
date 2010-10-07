package com.subgraph.vega.internal.model.web;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;

public class WebGetTarget extends AbstractWebEntity implements IWebGetTarget {

	private final IWebPath path;
	private final String query;
	private final ImmutableList<NameValuePair> parameters;
	private final URI uri;
	private String mimeType = "";
	
	
	WebGetTarget(WebModel model, IWebPath webPath, String query, String mimeType) {
		super(model);
		this.path = checkNotNull(webPath);
		this.query = Strings.nullToEmpty(query);
		parameters = parseParameters(query);
		uri = createURI(path.toURI(), query);
		if(mimeType != null)
			this.mimeType = mimeType;
	}
	
	private static URI createURI(URI pathURI, String query) {
		try {
			return new URI(pathURI.getScheme(), pathURI.getUserInfo(), pathURI.getHost(), pathURI.getPort(), pathURI.getPath(), Strings.emptyToNull(query), null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Could not create URI for get target query: "+ query, e);
		}
	}

	@Override
	public IWebEntity getParent() {
		return path;
	}

	public void setMimeType(String mimeType) {
		if(mimeType == null || mimeType.equals(mimeType))
			return;
		this.mimeType = mimeType;
		model.notifyEntityChanged(this);
	}
	
	@Override
	public URI toURI() {
		return uri;
	}

	public String getQuery() {
		return query;
	}
	
	@Override
	public IWebHost getHostEntity() {
		return path.getHostEntity();
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public IWebPath getPath() {
		return path;
	}

	@Override
	public List<NameValuePair> getParameters() {
		return parameters;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("uri", uri).toString(); 
	}
	
	@Override
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if(other instanceof WebGetTarget) {
			WebGetTarget that = (WebGetTarget) other;
			return path.equals(that.path) && parameters.equals(that.parameters);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(path, parameters);
	}
	
	private static ImmutableList<NameValuePair> parseParameters(String params) {
		if(params == null || params.isEmpty())
			return ImmutableList.of();
		final List<NameValuePair> parameterList = new ArrayList<NameValuePair>();
		try {
			URLEncodedUtils.parse(parameterList, new Scanner(params), "UTF-8");
		} catch (RuntimeException e) {
			System.err.println("Failed to parse URI parameters for WebGetTarget: "+ params);
		}
		return ImmutableList.copyOf(parameterList);
	}
}
