package com.subgraph.vega.internal.model.conditions;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.conditions.IHttpCondition;

public abstract class AbstractCondition implements IHttpCondition, Activatable {
	
	private boolean isInverted;
	private boolean isEnabled;
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void setEnabled(boolean state) {
		isEnabled = state;
	}

	@Override
	public boolean isInverted() {
		return isInverted;
	}

	@Override
	public void setInverted(boolean flag) {
		isInverted = flag;
	}

	protected boolean maybeInvert(boolean value) {
		return isInverted ^ value;
	}
	
	public MatchOption getMatchOption() {
		return (isInverted) ? (MatchOption.DOESNT_MATCH) : (MatchOption.DOES_MATCH);
	}

	private transient Activator activator;

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
