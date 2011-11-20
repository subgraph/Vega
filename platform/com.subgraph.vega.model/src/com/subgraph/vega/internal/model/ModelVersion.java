package com.subgraph.vega.internal.model;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.IModelVersion;

public class ModelVersion implements IModelVersion, Activatable {
	private transient Activator activator;
	private final int verMajor;
	private final int verMinor;

	public ModelVersion(int verMajor, int verMinor) {
		this.verMajor = verMajor;
		this.verMinor = verMinor;
	}

	@Override
	public int getVerMajor() {
		activate(ActivationPurpose.READ);
		return verMajor;
	}

	@Override
	public int getVerMinor() {
		activate(ActivationPurpose.READ);
		return verMinor;
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
