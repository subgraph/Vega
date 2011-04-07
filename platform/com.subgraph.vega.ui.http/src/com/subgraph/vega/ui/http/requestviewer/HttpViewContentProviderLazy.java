package com.subgraph.vega.ui.http.requestviewer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.conditions.ConditionSetChanged;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.IRequestLogUpdateListener;
import com.subgraph.vega.api.model.requests.RequestLogUpdateEvent;

public class HttpViewContentProviderLazy implements ILazyContentProvider {
	private static final int UPDATE_INTERVAL = 500;

	private IWorkspace workspace;
	private TableViewer tableViewer;
	private List<IRequestLogRecord> records;
	private final IRequestLogUpdateListener callback;
	private final IEventHandler conditionSetListener;

	private TimerTask updateTask;
	private final Timer updateTimer = new Timer();
	private int lastUpdateCount;
	private int currentCount;
	private IHttpConditionSet filterCondition;

	public HttpViewContentProviderLazy() {
		callback = createUpdateListener();
		conditionSetListener = createConditionSetListener();
	}

	@Override
	public void dispose() {
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

		if(newInput instanceof IWorkspace) 
			setNewInput((IWorkspace) newInput);
		else
			setNullInput();
	}

	private void setNullInput() {
		currentCount = lastUpdateCount = 0;
		tableViewer.setItemCount(0);
		if(workspace != null) {
			workspace.getRequestLog().removeUpdateListener(callback);
			workspace.getHttpConditionMananger().removeConditionSetListener(conditionSetListener);
		}

		workspace = null;
		updateTask = null;
		filterCondition = null;
	}
	
	private void setNewInput(IWorkspace newWorkspace) {
		if(newWorkspace == null) {
			setNullInput();
			return;
		}
		
		if(workspace != null)
			workspace.getRequestLog().removeUpdateListener(callback);
			
		workspace = newWorkspace;
		filterCondition = workspace.getHttpConditionMananger().getConditionSet("filter");
		workspace.getHttpConditionMananger().addConditionSetListenerByName("filter", conditionSetListener);
		workspace.getRequestLog().addUpdateListener(callback, filterCondition);
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
					System.out.println("Condition set changed");
					final IHttpConditionSet conditionSet = ((ConditionSetChanged) event).getConditionSet();
					setConditionFilter(conditionSet);
				}				
			}
		};
	}

	@Override
	public void updateElement(int index) {
		if(index >= records.size()) {
			System.out.println("Reloading query because index ("+ index + ") is greater than current record set size of ("+ records.size() + ")");
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
	}

	private List<IRequestLogRecord> queryRecords() {
		final IRequestLog requestLog = workspace.getRequestLog();
		if(filterCondition == null)
			return requestLog.getAllRecords();
		else
			return requestLog.getRecordsByConditionSet(filterCondition);
	}

	public void setConditionFilter(IHttpConditionSet conditionSet) {
		final IRequestLog requestLog = workspace.getRequestLog();
		filterCondition = conditionSet;
		requestLog.removeUpdateListener(callback);
		requestLog.addUpdateListener(callback, conditionSet);
		reloadRecords();
		tableViewer.refresh();
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
