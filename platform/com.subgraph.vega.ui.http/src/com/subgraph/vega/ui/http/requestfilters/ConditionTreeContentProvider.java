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

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;

public class ConditionTreeContentProvider implements ITreeContentProvider {
	private IHttpConditionSet conditionSet;
	private Map<IHttpConditionType, List<IHttpCondition>> conditionTypeMap = new IdentityHashMap<IHttpConditionType, List<IHttpCondition>>(); 

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		conditionSet = (IHttpConditionSet) newInput;
		conditionTypeMap.clear();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		conditionTypeMap.clear();
		for(IHttpCondition c: conditionSet.getAllConditions()) {
			List<IHttpCondition> list = conditionTypeMap.get(c.getType());
			if(list == null) {
				list = new ArrayList<IHttpCondition>();
				conditionTypeMap.put(c.getType(), list);
			}
			list.add(c);
		}
		return conditionTypeMap.keySet().toArray(new IHttpConditionType[0]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof IHttpConditionType) {
			final List<IHttpCondition> conditions = conditionTypeMap.get(parentElement);
			if(conditions != null)
				return conditions.toArray(new IHttpCondition[0]);
		}
		return null; 
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof IHttpCondition) 
			return ((IHttpCondition) element).getType();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IHttpConditionType) {
			return true;
		}
		return false;
	}

}
