package com.subgraph.vega.internal.model.web;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashSet;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.web.IWebPathParameters;

public class WebPathParameters implements IWebPathParameters, Activatable {
	
	private transient Activator activator;

	private final Set<List<NameValuePair>> observedParameterLists;
	private final Set<String> observedParameterNames;

	WebPathParameters() {
		this.observedParameterLists = new ActivatableHashSet<List<NameValuePair>>();
		this.observedParameterNames = new ActivatableHashSet<String>();
	}
	
	@Override
	public boolean hasParameters() {
		activate(ActivationPurpose.READ);
		synchronized(observedParameterLists) {
			return !observedParameterLists.isEmpty();
		}
	}

	@Override
	public Collection<List<NameValuePair>> getObservedParameterLists() {
		activate(ActivationPurpose.READ);
		synchronized(observedParameterLists) {
			return new HashSet<List<NameValuePair>>(observedParameterLists);
		}
	}
	@Override
	public Collection<String> getObservedParameterNames() {
		activate(ActivationPurpose.READ);
		synchronized(observedParameterLists) {
			return new HashSet<String>(observedParameterNames);
		}
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if(activator != null) {
			activator.activate(activationPurpose);
		}		
	}

	@Override
	public void bind(Activator activator) {
		if(this.activator == activator) {
			return;
		}
		
		if(activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		
		this.activator = activator;		
	}
}
