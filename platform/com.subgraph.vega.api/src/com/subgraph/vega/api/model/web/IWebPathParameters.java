package com.subgraph.vega.api.model.web;

import java.util.Collection;
import java.util.List;
import org.apache.http.NameValuePair;

public interface IWebPathParameters {
	boolean hasParameters();
	Collection< List<NameValuePair> > getObservedParameterLists();
	Collection<String> getObservedParameterNames();
}
