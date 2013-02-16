package com.subgraph.vega.internal.ui.http.requestlogviewer;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.conditions.ConditionSetChanged;
import com.subgraph.vega.api.model.conditions.IHttpConditionManager;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogNewRecordListener;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.RequestLogNewRecordEvent;

public class RequestViewContentProvider implements ILazyContentProvider, IRequestLogNewRecordListener {

	private final String conditionSetId;
	private final Color activeFilterColor;
	private final IEventHandler conditionSetListener;
	
	private IModel model;
	private IWorkspace currentWorkspace;
	private IEventHandler workspaceListener;
	private TableViewer tableViewer;
	
	private IHttpConditionSet filterCondition;
	private FilterTask currentResult;
	
	public RequestViewContentProvider(String instanceId, Color activeFilterColor) {
		if(instanceId != null) {
			conditionSetId = IHttpConditionManager.CONDITION_SET_FILTER + "." + instanceId;
		} else {
			conditionSetId = IHttpConditionManager.CONDITION_SET_FILTER;
		}
		conditionSetListener = createConditionSetListener();
		workspaceListener = createWorkspaceListener();
		this.activeFilterColor = activeFilterColor;
	}
	
	public IHttpConditionSet getConditionSet() {
		return filterCondition;
	}

	@Override
	public void dispose() {
		cleanupListeners();
		activeFilterColor.dispose();
		if(currentResult != null) {
			currentResult.cancel();
			currentResult = null;
		}
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(!(viewer instanceof TableViewer)) {
			tableViewer = null;
			return;
		}
		tableViewer = (TableViewer) viewer;
		
		if(newInput instanceof IModel) {
			setNewInput((IModel) newInput);
		} else {
			setNullInput();
		}
			
	}

	private void setNullInput() {
		cleanupListeners();
		tableViewer.setItemCount(0);
		model = null;
		currentWorkspace = null;
		filterCondition = null;
	}
	
	private void setNewInput(IModel model) {
		this.model = model;
		if(model == null) {
			setNullInput();
			return;
		}
		filterCondition = model.addConditionSetTracker(conditionSetId, conditionSetListener);
		filterCondition.setMatchOnEmptySet(true);
		
		currentWorkspace = model.addWorkspaceListener(workspaceListener);
		if(currentWorkspace != null) {
			currentWorkspace.getRequestLog().addNewRecordListener(this, filterCondition);
		}
		reloadRecords();
	}
	
	private void cleanupListeners() {
		if(currentWorkspace != null) {
			currentWorkspace.getRequestLog().removeNewRecordListener(this);
		}
		if(model != null) {
			if(filterCondition != null) {
				model.removeConditionSetTracker(conditionSetId, conditionSetListener);
				filterCondition = null;
			}
			model.removeWorkspaceListener(workspaceListener);
		}
	}

	@Override
	public void updateElement(int index) {
		if(currentResult == null) {
			return;
		}
		final IRequestLogRecord record = currentResult.getRecordForIndex(index);
		if(record != null) {
			tableViewer.replace(record, index);
		}
	}

	private IEventHandler createConditionSetListener() {
		return new IEventHandler() {

			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof ConditionSetChanged) {
					final IHttpConditionSet conditionSet = ((ConditionSetChanged) event).getConditionSet();
					setConditionFilter(conditionSet);
				}
			}
		};
	}
	
	private IEventHandler createWorkspaceListener() {
		return new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent)
					onWorkspaceOpen((WorkspaceOpenEvent) event);
				else if(event instanceof WorkspaceResetEvent)
					onWorkspaceReset((WorkspaceResetEvent) event);
				else if(event instanceof WorkspaceCloseEvent)
					onWorkspaceClose((WorkspaceCloseEvent) event);
				reloadRecords();
			}			
		};
	}
	private void onWorkspaceOpen(WorkspaceOpenEvent event) {
		currentWorkspace.getRequestLog().removeNewRecordListener(this);
		currentWorkspace = event.getWorkspace();
		currentWorkspace.getRequestLog().addNewRecordListener(this, filterCondition);
	}
	
	private void onWorkspaceReset(WorkspaceResetEvent event) {
		currentWorkspace.getRequestLog().removeNewRecordListener(this);
		currentWorkspace = event.getWorkspace();
		currentWorkspace.getRequestLog().addNewRecordListener(this, filterCondition);
		
	}
	
	private void onWorkspaceClose(WorkspaceCloseEvent event) {
		currentWorkspace.getRequestLog().removeNewRecordListener(this);
	}

	private void setConditionFilter(IHttpConditionSet conditionSet) {
		if(conditionSet != null) {
			conditionSet.setMatchOnEmptySet(true);
		}
		highlightTableForConditionFilter(conditionSet);
		final IRequestLog requestLog = currentWorkspace.getRequestLog();
		filterCondition = conditionSet;
		requestLog.removeNewRecordListener(this);
		requestLog.addNewRecordListener(this, conditionSet);
		reloadRecords();
	}

	private void highlightTableForConditionFilter(IHttpConditionSet conditionSet) {
		if(conditionSet != null && conditionSet.hasActiveConditions(false)) {
			tableViewer.getTable().setBackground(activeFilterColor);
		} else {
			tableViewer.getTable().setBackground(null);
		}
	}

	private void reloadRecords() {
		if(currentResult != null) {
			currentResult.cancel();
		}
		if(currentWorkspace == null) {
			return;
		}
		currentResult = new FilterTask(tableViewer, currentWorkspace.getRequestLog(), filterCondition);
		currentResult.applyFilter();
	}

	@Override
	public void onNewRecord(RequestLogNewRecordEvent event) {
		if(currentResult != null) {
			currentResult.addNewRecord(event.getNewRecord());
		}
	}
	
	public int getRowForRecord(IRequestLogRecord record) {
		if(currentResult != null) {
			return currentResult.getRowForRecord(record);
		} else {
			return -1;
		}
	}
}
