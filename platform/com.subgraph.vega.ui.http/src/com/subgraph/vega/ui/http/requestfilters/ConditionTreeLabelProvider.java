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
package com.subgraph.vega.ui.http.requestfilters;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;

public class ConditionTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof IHttpConditionType) {
			return ((IHttpConditionType)element).getName();
		} else if(element instanceof IHttpCondition) {			
			final IHttpCondition c = (IHttpCondition) element;
			return c.getMatchAction().getLabel() +  " " + c.getMatchAction().getArgumentAsString();
		} else {
			return null;
		}
	}

}
