package com.subgraph.vega.http.conditions;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.IHttpBooleanCondition;
import com.subgraph.vega.api.http.conditions.IHttpConditionSet;
import com.subgraph.vega.api.http.conditions.TransactionDirection;
import com.subgraph.vega.internal.http.conditions.ConditionTypeDomainName;
import com.subgraph.vega.internal.http.conditions.ConditionTypeRequestHeader;
import com.subgraph.vega.internal.http.conditions.ConditionTypeRequestMethod;
import com.subgraph.vega.internal.http.conditions.ConditionTypeResponseHeader;
import com.subgraph.vega.internal.http.conditions.ConditionTypeResponseStatus;

public class HttpConditionSet implements IHttpConditionSet {
	private final ArrayList<IHttpBooleanCondition> conditionList = new ArrayList<IHttpBooleanCondition>();

	@Override
	public IHttpBooleanCondition createCondition(ConditionType conditionType, Enum<?> comparisonType, String pattern, boolean isEnabled) {
		IHttpBooleanCondition condition = null;
		switch (conditionType) {
			case DOMAIN_NAME:
				condition = new ConditionTypeDomainName(comparisonType, pattern, isEnabled);
				break;
			case REQUEST_METHOD:
				condition = new ConditionTypeRequestMethod(comparisonType, pattern, isEnabled);
				break;
			case REQUEST_HEADER:
				condition = new ConditionTypeRequestHeader(comparisonType, pattern, isEnabled); 
				break;
			case RESPONSE_HEADER:
				condition = new ConditionTypeResponseHeader(comparisonType, pattern, isEnabled);
				break;
			case RESPONSE_STATUS:
				condition = new ConditionTypeResponseStatus(comparisonType, pattern, isEnabled);
				break;
			default:
				throw new IllegalArgumentException("Unknown condition type " + conditionType);
		}

		conditionList.add(condition);
		return condition;
	}

	@Override
	public void removeCondition(IHttpBooleanCondition condition) {
		conditionList.remove(condition);
	}

	@Override
	public int getBreakpontIdxOf(IHttpBooleanCondition condition) {
		return conditionList.indexOf(condition);
	}

	@Override
	public int getConditionCnt() {
		return conditionList.size();
	}

	@Override
	public IHttpBooleanCondition[] getConditions() {
		return conditionList.toArray(new IHttpBooleanCondition[conditionList.size()]);
	}

	private String escapeString(String s) {
		final StringBuilder builder = new StringBuilder();
	    final StringCharacterIterator iter = new StringCharacterIterator(s);
	    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
	    	if (c == '@' || c == '!' || c == '\\') {
	    		builder.append('\\');
	    	}
	    	builder.append(c);
	    }
		return builder.toString();
	}

	private String unescapeString(String s) {
		final StringBuilder builder = new StringBuilder();
	    final StringCharacterIterator iter = new StringCharacterIterator(s);
	    for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
	    	if (c == '\\') {
	    		c = iter.next();
	    		if (c == CharacterIterator.DONE) {
	    			throw new IllegalArgumentException("Invalid quoted character");
	    		}
	    	}
	    	builder.append(c);
	    }
	    return builder.toString();
	}

	@Override
	public String serialize() {
		final StringBuilder builder = new StringBuilder();
		for (int idx = 0; idx < conditionList.size(); idx++) {
			if (idx > 0) {
				builder.append("!!");
			}
			IHttpBooleanCondition condition = conditionList.get(idx);
			builder.append(escapeString(condition.getType().name()));
			builder.append("@@");
			builder.append(escapeString(condition.getComparisonType().name()));
			builder.append("@@");
			builder.append(escapeString(condition.getPattern()));
		}
		return builder.toString();
	}

	@Override
	public void unserialize(String str) {
		conditionList.clear();
		if (str != "") {
			final String[] conditions = str.split("!!");
			for (int idx = 0; idx < conditions.length; idx++) {
				final String[] fields = conditions[idx].split("@@");
				if (fields.length != 3) {
					conditionList.clear();
					throw new IllegalArgumentException("Bad preferences string");
				}
				ConditionType conditionType = ConditionType.valueOf(unescapeString(fields[0]));
				Enum<?> comparisonType = Enum.valueOf((Class) conditionType.getComparisonTypeClass(), unescapeString(fields[1]));
				String pattern = fields[2];
				createCondition(conditionType, comparisonType, pattern, true);
			}
		}
	}

	@Override
	public boolean test(HttpRequest request, HttpResponse response) {
		for (IHttpBooleanCondition condition: conditionList) {
			if (condition.getIsEnabled() == true) {
				if ((condition.getType().getMask() & TransactionDirection.DIRECTION_REQUEST.getMask()) != 0) {
					return condition.test(request);
				} else {
					return condition.test(response);
				}
			}
		}
		return false;
	}

}
