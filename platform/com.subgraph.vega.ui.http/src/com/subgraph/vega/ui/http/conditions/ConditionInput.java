package com.subgraph.vega.ui.http.conditions;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Composite;

import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionIntegerMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionRangeMatchAction;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionStringMatchAction;

public class ConditionInput {
	private final IHttpConditionManager conditionManager;
	
	private ConditionTypeComboViewer conditionTypeViewer;
	private MatchActionComboViewer matchActionViewer;
	private MatchActionArgumentPanel matchActionArguments;
	
	public ConditionInput(IHttpConditionManager conditionManager) {
		this.conditionManager = conditionManager;
	}
	
	public ComboViewer createConditionTypeCombo(Composite parent) {
		conditionTypeViewer = new ConditionTypeComboViewer(parent, conditionManager.getConditionTypes());
		if(matchActionViewer != null)
			conditionTypeViewer.setMatchTypeViewer(matchActionViewer);
		return conditionTypeViewer;
	}
	
	public ComboViewer createConditionMatchCombo(Composite parent) {
		matchActionViewer = new MatchActionComboViewer(parent);
		if(conditionTypeViewer != null)
			conditionTypeViewer.setMatchTypeViewer(matchActionViewer);
		if(matchActionArguments != null)
			matchActionViewer.setMatchActionArgumentPanel(matchActionArguments);
		return matchActionViewer;
	}
	
	public Composite createInputPanel(Composite parent) {
		matchActionArguments = new MatchActionArgumentPanel(parent);
		if(matchActionViewer != null)
			matchActionViewer.setMatchActionArgumentPanel(matchActionArguments);
		return matchActionArguments;
	}
	
	public void reset() {
		conditionTypeViewer.reset();
		matchActionViewer.reset();
	}

	public IHttpCondition createConditionFromData() {
		final IHttpConditionMatchAction matchAction = matchActionViewer.getSelectedMatchAction();
		final IHttpConditionType conditionType = conditionTypeViewer.getSelectedConditionType();
		if(matchAction == null || conditionType == null)
			return null;
		
		switch(matchAction.getArgumentType()) {
		case ARGUMENT_STRING:
			return createStringCondition(conditionType, matchAction, matchActionArguments.getStringText());
		case ARGUMENT_REGEX:
			return createStringCondition(conditionType, matchAction, matchActionArguments.getRegexText());
		case ARGUMENT_INTEGER:
			return createIntegerCondition(conditionType, matchAction, matchActionArguments.getIntegerText());
		case ARGUMENT_RANGE:
			return createRangeCondition(conditionType, matchAction, matchActionArguments.getRangeLowText(), matchActionArguments.getRangeHighText());
		}
		return null;
	}
	
	
	private IHttpCondition createStringCondition(IHttpConditionType type, IHttpConditionMatchAction matchAction, String value) {
		if(!value.isEmpty() && (matchAction instanceof IHttpConditionStringMatchAction)) {
			((IHttpConditionStringMatchAction) matchAction).setString(value);
			return type.createConditionInstance(matchAction);
		}
		return null;
	}
	
	private IHttpCondition createIntegerCondition(IHttpConditionType type, IHttpConditionMatchAction matchAction, String integerString) {
		if(integerString.isEmpty() || !(matchAction instanceof IHttpConditionIntegerMatchAction))
			return null;
		try {
			final int value = Integer.parseInt(integerString);
			((IHttpConditionIntegerMatchAction) matchAction).setInteger(value);
			return type.createConditionInstance(matchAction);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private IHttpCondition createRangeCondition(IHttpConditionType type, IHttpConditionMatchAction matchAction, String rangeLowText, String rangeHighText) {
		if(rangeLowText.isEmpty() || rangeHighText.isEmpty() || !(matchAction instanceof IHttpConditionRangeMatchAction))
			return null;
		try {
			final int low = Integer.parseInt(rangeLowText);
			final int high = Integer.parseInt(rangeHighText);
			if(low < 0 || high < 0 || low > high)
				return null;
			((IHttpConditionRangeMatchAction) matchAction).setRange(low, high);
			return type.createConditionInstance(matchAction);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
