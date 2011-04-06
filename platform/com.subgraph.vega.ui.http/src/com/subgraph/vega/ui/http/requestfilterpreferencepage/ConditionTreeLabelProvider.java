package com.subgraph.vega.ui.http.requestfilterpreferencepage;

import org.eclipse.jface.viewers.LabelProvider;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.IHttpBooleanCondition;

public class ConditionTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof ConditionType) {
			return ((ConditionType)element).getName();
		}

		IHttpBooleanCondition condition = (IHttpBooleanCondition) element;
		final StringBuilder builder = new StringBuilder();
		builder.append(condition.getComparisonType().name());
		builder.append(":");
		builder.append(condition.getPattern());
		return builder.toString();
	}

}
