package com.subgraph.vega.internal.model.requests;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.requests.IRequestOrigin;

public class RequestOrigin implements IRequestOrigin, Activatable {
	private transient Activator activator;
	private Origin origin;
	
	public RequestOrigin(Origin origin) {
		this.origin = origin;
	}

	@Override
	public Origin getOrigin() {
		activate(ActivationPurpose.READ);
		return origin;
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
