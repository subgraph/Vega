package com.subgraph.vega.impl.scanner.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;

import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebPathParameters;

public class PathStateParameterManager {
	
	// For each unique set of parameters, we have an indexed list of PathState nodes, one for each parameter
	private final Map<Set<NameValuePair>, List<PathState>> parametersToPathStates = new HashMap<Set<NameValuePair>, List<PathState>>();
	
	PathStateParameterManager(PathStateManager stateManager, IWebPath path) {
		IWebPathParameters parameters = path.getGetParameters();
		for(List<NameValuePair> plist: parameters.getParameterLists()) {
			addParameterList(stateManager, path, plist);
		}
	}
	
	public synchronized List<PathState> addParameterList(PathStateManager stateManager, IWebPath path, List<NameValuePair> plist) {
		final Set<NameValuePair> pset = new HashSet<NameValuePair>(plist);
		if(parametersToPathStates.containsKey(pset))
			return parametersToPathStates.get(pset);
		
		final List<PathState> pathStates = new ArrayList<PathState>(plist.size());
		
		parametersToPathStates.put(pset, pathStates);
		
		final PathState parentState = stateManager.getStateForPath(path);
		for(int i = 0; i < plist.size(); i++)  {
			PathState st = new PathState(stateManager, parentState, path, plist, i);
			pathStates.add(st);
			parentState.addChildState(st);
		}
		return pathStates;
	}
	
	public synchronized boolean hasParameterList(List<NameValuePair> plist) {
		final Set<NameValuePair> pset = new HashSet<NameValuePair>(plist);
		return parametersToPathStates.containsKey(pset);
	}
	
	public synchronized List<PathState> getStatesForParameterList(List<NameValuePair> plist) {
		final Set<NameValuePair> pset = new HashSet<NameValuePair>(plist);
		final List<PathState> result = parametersToPathStates.get(pset);
		if(result == null)
			return Collections.emptyList();
		else
			return result;
	}
}
