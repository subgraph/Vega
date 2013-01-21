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
package com.subgraph.vega.internal.ui.http.requestlogviewer;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

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
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.IRequestLogUpdateListener;
import com.subgraph.vega.api.model.requests.RequestLogUpdateEvent;

public class HttpViewContentProviderLazy implements ILazyContentProvider {
	private static final int UPDATE_INTERVAL = 500;

	private IModel model;
	private IWorkspace currentWorkspace;
	private IEventHandler workspaceListener;
	private TableViewer tableViewer;
	private List<IRequestLogRecord> records;
	private final String conditionSetId;
	private final IRequestLogUpdateListener callback;
	private final IEventHandler conditionSetListener;

	private TimerTask updateTask;
	private final Timer updateTimer = new Timer();
	private int lastUpdateCount;
	private int currentCount;
	private IHttpConditionSet filterCondition;

	/**
	 * @param instanceId A unique ID to differentiate between condition filter sets.
	 */
	public HttpViewContentProviderLazy(String instanceId) {
		if (instanceId != null) {
			conditionSetId = IHttpConditionManager.CONDITION_SET_FILTER + "." + instanceId;
		} else {
			conditionSetId = IHttpConditionManager.CONDITION_SET_FILTER;
		}
		workspaceListener = createWorkspaceListener();
		callback = createUpdateListener();
		conditionSetListener = createConditionSetListener();
	}

	public IHttpConditionSet getConditionSet() {
		return filterCondition;
	}
	
	@Override
	public void dispose() {
		cleanupListeners();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if(updateTask != null)
			updateTask.cancel();

		if(!(viewer instanceof TableViewer)) {
			tableViewer = null;
			return;
		}
		tableViewer = (TableViewer) viewer;

		if(newInput instanceof IModel) 
			setNewInput((IModel) newInput);
		else
			setNullInput();
	}
	
	private void setNullInput() {
		cleanupListeners();
		currentCount = lastUpdateCount = 0;
		tableViewer.setItemCount(0);
		model = null; 
		currentWorkspace = null;
		updateTask = null;
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
		if(currentWorkspace != null) 
			currentWorkspace.getRequestLog().addUpdateListener(callback, filterCondition);
		
		reloadRecords();
		updateTask = createTimerTask(tableViewer.getControl().getDisplay());
		updateTimer.scheduleAtFixedRate(updateTask, 0, UPDATE_INTERVAL);
	}

	private IRequestLogUpdateListener createUpdateListener() {
		return new IRequestLogUpdateListener() {
			@Override
			public void update(RequestLogUpdateEvent event) {
				currentCount = event.getRecordCount();
			}
		};
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
	
	private void cleanupListeners() {
		if (currentWorkspace != null) {
			currentWorkspace.getRequestLog().removeUpdateListener(callback);
		}

		if (model != null) {
			if (filterCondition != null) {
				model.removeConditionSetTracker(conditionSetId, conditionSetListener);
				filterCondition = null;
			}
			model.removeWorkspaceListener(workspaceListener);
		}
	}

	private void onWorkspaceOpen(WorkspaceOpenEvent event) {
		currentWorkspace.getRequestLog().removeUpdateListener(callback);
		currentWorkspace = event.getWorkspace();
		currentWorkspace.getRequestLog().addUpdateListener(callback, filterCondition);
	}
	
	private void onWorkspaceReset(WorkspaceResetEvent event) {
		currentWorkspace.getRequestLog().removeUpdateListener(callback);
		currentWorkspace = event.getWorkspace();
		currentWorkspace.getRequestLog().addUpdateListener(callback, filterCondition);
	}
	
	private void onWorkspaceClose(WorkspaceCloseEvent event) {
		currentWorkspace.getRequestLog().removeUpdateListener(callback);
	}

	@Override
	public void updateElement(int index) {
		if(index >= records.size()) {
			records = queryRecords();
		}
		
		final IRequestLogRecord record = (index < records.size()) ? (records.get(index)) : (null);
		
		if(record != null) {
			tableViewer.replace(record, index);
		}
	}

	private void reloadRecords() {
		records = queryRecords();
		currentCount = records.size();
		lastUpdateCount = -1;
		resetTable();
	}

	private List<IRequestLogRecord> queryRecords() {
		if(currentWorkspace == null)
			return Collections.emptyList();
		final IRequestLog requestLog = currentWorkspace.getRequestLog();
		if(filterCondition == null)
			return requestLog.getAllRecords();
		else
			return requestLog.getRecordsByConditionSet(filterCondition);
	}

	public void setConditionFilter(IHttpConditionSet conditionSet) {
		if(conditionSet != null)
			conditionSet.setMatchOnEmptySet(true);
		final IRequestLog requestLog = currentWorkspace.getRequestLog();
		filterCondition = conditionSet;
		requestLog.removeUpdateListener(callback);
		requestLog.addUpdateListener(callback, conditionSet);
		reloadRecords();
	}
	
	private void resetTable() {
		final Display display = tableViewer.getControl().getDisplay();
		if(display == null)
			return;
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if(tableViewer.getTable().isDisposed())
					return;
				tableViewer.getTable().setSelection(0);
				tableViewer.getTable().showSelection();
				tableViewer.setItemCount(currentCount);
				tableViewer.refresh(true);
			}
		});
	}

	private TimerTask createTimerTask(final Display display) {
		return new TimerTask() {
			@Override
			public void run() {
				if(lastUpdateCount != currentCount) {
					display.syncExec(new Runnable() {
						@Override
						public void run() {
							lastUpdateCount = currentCount;
							if(!tableViewer.getControl().isDisposed())
								tableViewer.setItemCount(currentCount);
						}
					});
				}
			}
		};
	}
}
