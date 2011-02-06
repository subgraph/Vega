package com.subgraph.vega.api.model.web;

import java.util.Collection;
import java.util.List;
import org.apache.http.NameValuePair;

/**
 * 
 * ?id=100&page=welcome
 * ?id=202&action=foo
 * ?id=300&action=foo
 * 
 * getParameterLists() : [ [id=100,page=welcome], [id=202,action=foo], [id=300,action=foo] ]
 * getParameterNameLists() : [ [id, page], [id, action] ]
 * getParameterNames(): [ id, page, action ]
 * getValuesForParameter(id) : [100, 202, 300]
 * getValuesForParameter(action): [foo]
 *
 */
public interface IWebPathParameters {
	boolean hasParameters();
	Collection< List<NameValuePair> > getParameterLists();
	Collection< List<String> > getParameterNameLists();
	Collection<String> getParameterNames();
	Collection<String> getValuesForParameter(String name);
}
