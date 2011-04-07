package com.subgraph.vega.internal.model.conditions;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.db4o.query.Query;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.IHttpConditionType.HttpConditionStyle;

public class ConditionHostname extends AbstractRegexCondition {
	static private transient IHttpConditionType conditionType;
	
	static IHttpConditionType getConditionType() {
		synchronized(ConditionHostname.class) {
			if(conditionType == null)
				conditionType = createType();
			return conditionType;
		}
	}

	private static IHttpConditionType createType() {
		return new ConditionType("hostname", HttpConditionStyle.CONDITION_REGEX) {
			@Override
			public IHttpCondition createConditionInstance() {
				return new ConditionHostname();
			}
		};
	}

	@Override
	public boolean matches(HttpRequest request) {
		final URI uri = getRequestUri(request);
		if(uri == null)
			return false;
		return maybeInvert(matchesPattern(uri.getHost()));
	}
	
	private URI getRequestUri(HttpRequest request) {
		if(request == null)
			return null;
		try {
			return new URI(request.getRequestLine().getUri());
		} catch (URISyntaxException e) {
			return null;
		}		
	}

	@Override
	public boolean matches(HttpResponse response) {
		return false;
	}

	@Override
	public boolean matches(HttpRequest request, HttpResponse response) {
		return matches(request);
	}

	@Override
	public IHttpConditionType getType() {
		return getConditionType();
	}

	@Override
	public void filterRequestLogQuery(Query query) {
		constrainQuery(query.descend("hostname"));		
	}
}
