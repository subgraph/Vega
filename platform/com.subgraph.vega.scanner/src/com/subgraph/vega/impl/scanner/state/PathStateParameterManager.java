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
package com.subgraph.vega.impl.scanner.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;

import com.subgraph.vega.api.crawler.ICrawlerResponseProcessor;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPathParameters;
import com.subgraph.vega.impl.scanner.handlers.FileProcessor;

public class PathStateParameterManager {
	private final static ICrawlerResponseProcessor fileFetchProcessor = new FileProcessor();
	private final PathState pathState;
	// For each unique set of parameters, we have an indexed list of PathState nodes, one for each parameter
	private final Map<Set<String>, List<PathState>> parametersToPathStates = new HashMap<Set<String>, List<PathState>>();
	private final Map<Set<String>, List<PathState>> parametersToPostPathStates = new HashMap<Set<String>, List<PathState>>();

	PathStateParameterManager(PathState ps) {
		this.pathState = ps;
		final IWebPath path = ps.getPath();
		IWebPathParameters parameters = path.getGetParameters();
		// XXX hmmm?
		for(List<NameValuePair> plist: parameters.getParameterLists()) {
			addParameterList(plist);
		}
	}

	public synchronized List<PathState> addParameterList(List<NameValuePair> plist) {
		final Set<String> names = getNameSetForParameterList(plist);

		if(parametersToPathStates.containsKey(names))
			return parametersToPathStates.get(names);

		final List<PathState> pathStates = new ArrayList<PathState>(plist.size());

		parametersToPathStates.put(names, pathStates);

		for(int i = 0; i < plist.size(); i++)  {
			if(!isExcludedParameter(plist.get(i))) {
				addFuzzablePathState(pathStates, plist, i);
			}
		}
		return pathStates;
	}
	
	private boolean isExcludedParameter(NameValuePair parameter) {
		return pathState.getPathStateManager().isExcludedParameter(parameter.getName());
	}
		
	private void addFuzzablePathState(List<PathState> pathStates, List<NameValuePair> parameters, int index) {
		final PathState ps = PathState.createParameterPathState(fileFetchProcessor, pathState, parameters, index);
		pathStates.add(ps);
	}
	
	private Set<String> getNameSetForParameterList(List<NameValuePair> plist) {
		final Set<String> names = new HashSet<String>();
		for(NameValuePair nvp: plist) {
			names.add(nvp.getName());
		}
		return names;
	}

	public synchronized boolean hasParameterList(List<NameValuePair> plist) {
		return parametersToPathStates.containsKey( getNameSetForParameterList(plist) );
	}

	public synchronized List<PathState> getStatesForParameterList(List<NameValuePair> plist) {
		final List<PathState> result = parametersToPathStates.get( getNameSetForParameterList(plist) );
		if(result == null)
			return Collections.emptyList();
		else
			return result;
	}

	public synchronized boolean hasPostParameterList(List<NameValuePair> plist) {
		return parametersToPostPathStates.containsKey( getNameSetForParameterList(plist) );
	}

	public synchronized List<PathState> getStatesForPostParameterList(List<NameValuePair> plist) {
		final List<PathState> result = parametersToPostPathStates.get( getNameSetForParameterList(plist) );
		if(result == null)
			return Collections.emptyList();
		else
			return result;
	}

	public synchronized List<PathState> addPostParameterList(List<NameValuePair> plist) {
		final Set<String> names = getNameSetForParameterList(plist);
		if(parametersToPostPathStates.containsKey(names))
			return parametersToPostPathStates.get(names);

		final List<PathState> pathStates = new ArrayList<PathState>();
		parametersToPostPathStates.put(names, pathStates);

		for(int i = 0; i < plist.size(); i++) {
			if(!isExcludedParameter(plist.get(i))) {
				addFuzzablePostPathState(pathStates, plist, i);
			}
		}
		return pathStates;
	}
	
	private void addFuzzablePostPathState(List<PathState> pathStates, List<NameValuePair> parameters, int index) {
		final PathState ps = PathState.createPostParameterPathState(fileFetchProcessor, pathState, parameters, index);
		pathStates.add(ps);
	}
}
