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
package com.subgraph.vega.internal.model.macros;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;

public class HttpMacroItemParam implements IHttpMacroItemParam, Activatable {
	private transient Activator activator;
	private String name;
	private ValueSource source;
	private ValueSetIn setIn;
	private String value;
	
	public HttpMacroItemParam(String name, ValueSetIn setIn) {
		this.name = name;
		this.setIn = setIn;
	}

	public HttpMacroItemParam(String name, String value, ValueSetIn setIn) {
		this.name = name;
		this.source = IHttpMacroItemParam.ValueSource.SOURCE_LITERAL;
		this.value = value;
		this.setIn = setIn;
	}
	
	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	@Override
	public void setValueSource(ValueSource source) {
		activate(ActivationPurpose.READ);
		this.source = source;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public ValueSource getValueSource() {
		activate(ActivationPurpose.READ);
		return source;
	}

	@Override
	public void setSetIn(ValueSetIn setIn) {
		activate(ActivationPurpose.READ);
		this.setIn = setIn;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public ValueSetIn getSetIn() {
		activate(ActivationPurpose.READ);
		return setIn;
	}

	@Override
	public void setValue(String value) {
		activate(ActivationPurpose.READ);
		this.value = value;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getValue() {
		activate(ActivationPurpose.READ);
		return value;
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
