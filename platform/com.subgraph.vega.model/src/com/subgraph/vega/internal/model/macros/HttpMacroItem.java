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

import java.util.Collection;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.collections.ActivatableHashMap;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.macros.IHttpMacroItem;
import com.subgraph.vega.api.model.macros.IHttpMacroItemParam;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpMacroItem implements IHttpMacroItem, Activatable {
	private transient Activator activator;
	private IRequestLogRecord requestLogRecord;
	private boolean useCookies;
	private boolean keepCookies;
	private ActivatableHashMap<String, IHttpMacroItemParam> paramDict;

	public HttpMacroItem(IRequestLogRecord requestLogRecord) {
		this.requestLogRecord = requestLogRecord;
	}

	@Override
	public IRequestLogRecord getRequestLogRecord() {
		activate(ActivationPurpose.READ);
		return requestLogRecord;
	}

	@Override
	public void setUseCookies(boolean useCookies) {
		activate(ActivationPurpose.READ);
		this.useCookies = useCookies;
		activate(ActivationPurpose.WRITE);

	}

	@Override
	public boolean getUseCookies() {
		activate(ActivationPurpose.READ);
		return useCookies;
	}

	@Override
	public void setKeepCookies(boolean keepCookies) {
		activate(ActivationPurpose.READ);
		this.keepCookies = keepCookies;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public boolean getKeepCookies() {
		activate(ActivationPurpose.READ);
		return keepCookies;
	}

	@Override
	public Collection<IHttpMacroItemParam> getParams() {
		activate(ActivationPurpose.READ);
		return paramDict.values();
	}

	@Override
	public IHttpMacroItemParam getParam(String name) {
		activate(ActivationPurpose.READ);
		return paramDict.get(name);
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
