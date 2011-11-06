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
package com.subgraph.vega.internal.model.variables;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.variables.IVariable;

public class Variable implements IVariable, Activatable {
	private transient Activator activator;
	private String name;
	private String description;
	
	@Override
	public void setName(String name) {
		activate(ActivationPurpose.READ);
		this.name = name;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	@Override
	public void setDescription(String description) {
		activate(ActivationPurpose.READ);
		this.description = description;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getDescription() {
		activate(ActivationPurpose.READ);
		return description;
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
