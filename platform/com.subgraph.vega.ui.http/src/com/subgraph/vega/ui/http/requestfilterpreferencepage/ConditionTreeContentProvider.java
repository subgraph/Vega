package com.subgraph.vega.ui.http.requestfilterpreferencepage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.IHttpBooleanCondition;
import com.subgraph.vega.api.http.conditions.IHttpConditionSet;

public class ConditionTreeContentProvider implements ITreeContentProvider {
	private IHttpConditionSet conditionSet;
	private Map<ConditionType, ArrayList<IHttpBooleanCondition>> conditionTypeMap = new EnumMap<ConditionType, ArrayList<IHttpBooleanCondition>>(ConditionType.class); 

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
		for (IHttpBooleanCondition c: conditionSet.getConditions()) {
			ArrayList<IHttpBooleanCondition> list = conditionTypeMap.get(c.getType());
			if (list == null) {
				list = new ArrayList<IHttpBooleanCondition>();
				conditionTypeMap.put(c.getType(), list);
			}
			list.add(c);
		}

		Set<ConditionType> keys = conditionTypeMap.keySet();
		return keys.toArray(new ConditionType[keys.size()]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ConditionType) {
			ArrayList<IHttpBooleanCondition> list = conditionTypeMap.get((ConditionType)parentElement);
			return list.toArray(new IHttpBooleanCondition[list.size()]);
		}
		return null; 
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ConditionType) {
			return true;
		}
		return false;
	}

}
