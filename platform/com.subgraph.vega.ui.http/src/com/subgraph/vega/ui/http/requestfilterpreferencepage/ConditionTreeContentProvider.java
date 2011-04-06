package com.subgraph.vega.ui.http.requestfilterpreferencepage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;



public class ConditionTreeContentProvider implements ITreeContentProvider {
	private IHttpConditionSet conditionSet;
	private Map<IHttpConditionType, List<IHttpCondition>> conditionTypeMap = new HashMap<IHttpConditionType, List<IHttpCondition>>(); 

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
