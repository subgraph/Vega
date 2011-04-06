package com.subgraph.vega.ui.http.requestfilterpreferencepage;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.IHttpRangeCondition;
import com.subgraph.vega.api.model.conditions.IHttpRegexCondition;

public class ConditionTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof IHttpConditionType) {
			return ((IHttpConditionType)element).getName();
		} else if(element instanceof IHttpRegexCondition) {
			final IHttpRegexCondition condition = (IHttpRegexCondition) element;
			return condition.getType().getName() + ":"+ condition.getPattern();
		} else if (element instanceof IHttpRangeCondition) {
			final IHttpRangeCondition condition = (IHttpRangeCondition) element;
			return condition.getType().getName() + ":" + condition.getRangeLow() + "-"+ condition.getRangeHigh();
		} else {
			return null;
		}
	}

}
