package com.subgraph.vega.impl.scanner.requests;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.model.web.IWebPath;

public abstract class AbstractRequestBuilder implements IRequestBuilder {
	
	private final IWebPath path;
	private final URI baseURI;
	
	protected AbstractRequestBuilder(IWebPath path) {
		this.path = path;
		this.baseURI = path.getUri();
	}
	
	protected HttpUriRequest createPathRequest() {
		return createRequest(getUriForPath());
	}
	
	protected HttpUriRequest createRequest(URI uri) {
		return new HttpGet(uri);
	}
	
	protected HttpUriRequest createRequestFromPath(String path) {
		final URI u = createUri(path);
		return createRequest(u);
	}

	protected HttpUriRequest createRequestFromQuery(String query) {
		final URI u = createUri(getBasePath(), query);
		return createRequest(u);
	}

	protected String getBasePath() {
		return getUriForPath().getPath();
	}

	protected URI createUri(String path) {
		return createUri(path, null);
	}

	protected URI createUri(String path, String query) {
		final String q = (query == null) ? (baseURI.getQuery()) : (query);
		try {
			return new URI(baseURI.getScheme(), baseURI.getAuthority(), path, q, null);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Failed to create new URI with path = "+ path + " and query = "+ q);
		}
	}

	private URI getUriForPath() {
		switch(path.getPathType()) {
		case PATH_DIRECTORY:
			return maybeAddTrailingSlash(baseURI);
		case PATH_PATHINFO:
			return maybeRemoveTrailingSlash(baseURI);
		default:
			return baseURI;
		}
	}

	private URI maybeAddTrailingSlash(URI uri) {
		if(uri.getPath().endsWith("/"))
			return uri;
		final String path = uri.getPath() + "/";
		return createUri(path);
	}
	
	private URI maybeRemoveTrailingSlash(URI uri) {
		if(!uri.getPath().endsWith("/"))
			return uri;
		String p = uri.getPath();
		while(p.length() > 0 && p.endsWith("/"))
			p = p.substring(0, p.length() - 1);
		
		return createUri(p);
	}
}
