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
	private final Map<Set<NameValuePair>, List<PathState>> parametersToPathStates = new HashMap<Set<NameValuePair>, List<PathState>>();
	
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
		final Set<NameValuePair> pset = new HashSet<NameValuePair>(plist);
		if(parametersToPathStates.containsKey(pset))
			return parametersToPathStates.get(pset);
		
		final List<PathState> pathStates = new ArrayList<PathState>(plist.size());
		
		parametersToPathStates.put(pset, pathStates);
		
		for(int i = 0; i < plist.size(); i++)  {
			PathState st = PathState.createParameterPathState(fileFetchProcessor,  pathState, plist, i);
			pathStates.add(st);
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
