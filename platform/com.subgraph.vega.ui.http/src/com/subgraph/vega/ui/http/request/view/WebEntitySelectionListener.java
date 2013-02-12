package com.subgraph.vega.ui.http.request.view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.IHttpCondition;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.conditions.IHttpConditionType;
import com.subgraph.vega.api.model.conditions.match.IHttpConditionMatchAction;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;

public class WebEntitySelectionListener implements ISelectionListener {
	
	private final IModel model;
	private final String instanceId;
	
	public WebEntitySelectionListener(IModel model, String instanceId) {
		this.model = model;
		this.instanceId = instanceId;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if(!(selection instanceof IStructuredSelection)) {
			return;
		}
		final IStructuredSelection ss = (IStructuredSelection) selection;
		for(Object ob: ss.toArray()) {
			if(ob instanceof IWebEntity) {
				handleWebEntitySelected((IWebEntity) ob);
			}
		}
	}

	private String getConditionSetId() {
		if(instanceId == null) {
			return IHttpConditionManager.CONDITION_SET_FILTER;
		} else {
			return IHttpConditionManager.CONDITION_SET_FILTER + "." + instanceId;
		}
	}

	private void handleWebEntitySelected(IWebEntity entity) {
		final IHttpConditionSet conditionSet = getConditionSet();
		if(conditionSet == null) {
			return;
		}
		conditionSet.clearTemporaryConditions(false);
		
		if(entity instanceof IWebHost) {
			handleWebHostSelected(conditionSet, (IWebHost) entity);
		} else if(entity instanceof IWebPath) {
			handleWebPathSelected(conditionSet, (IWebPath) entity);
		} else if(entity instanceof IWebResponse) {
			handleWebResponseSelected(conditionSet, (IWebResponse) entity);
		}
		conditionSet.notifyChanged();
	}
	
	private void handleWebHostSelected(IHttpConditionSet conditionSet, IWebHost host) {
		addHostnameCondition(conditionSet, host.getHostname());
	}
	
	private void handleWebPathSelected(IHttpConditionSet conditionSet, IWebPath path) {
		addHostnameCondition(conditionSet, path.getMountPoint().getWebHost().getHostname());
		addPathCondition(conditionSet, path.getFullPath());
	}
	
	private void handleWebResponseSelected(IHttpConditionSet conditionSet, IWebResponse response) {
		handleWebPathSelected(conditionSet, response.getPathEntity());
	}

	private void addHostnameCondition(IHttpConditionSet conditionSet, String hostname) {
		final IHttpConditionType type = conditionSet.getConditionManager().getConditionTypeByName("hostname");
		final IHttpConditionMatchAction matchAction = type.getMatchActionByName("contains");
		matchAction.setArgumentFromString(hostname);
		final IHttpCondition condition = type.createConditionInstance(matchAction);
		conditionSet.appendTemporaryCondition(condition, false);
	}
	
	private void addPathCondition(IHttpConditionSet conditionSet, String path) {
		final IHttpConditionType type = conditionSet.getConditionManager().getConditionTypeByName("request path");
		final IHttpConditionMatchAction matchAction = type.getMatchActionByName("starts with");
		matchAction.setArgumentFromString(path);
		final IHttpCondition condition = type.createConditionInstance(matchAction);
		conditionSet.appendTemporaryCondition(condition, false);
	}


	private IHttpConditionSet getConditionSet() {
		final IHttpConditionManager manager = getConditionManager();
		if(manager == null) {
			return null;
		}
		final String id = getConditionSetId();
		return manager.getConditionSet(id);
	}

	private IHttpConditionManager getConditionManager() {
		final IWorkspace workspace = model.getCurrentWorkspace();
		if(workspace == null) {
			return null;
		}
		return workspace.getHttpConditionMananger();
	}
}
