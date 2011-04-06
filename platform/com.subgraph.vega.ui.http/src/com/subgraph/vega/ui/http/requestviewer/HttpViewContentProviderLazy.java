package com.subgraph.vega.ui.http.requestviewer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.model.requests.IRequestLogUpdateListener;
import com.subgraph.vega.api.model.requests.RequestLogUpdateEvent;

public class HttpViewContentProviderLazy implements ILazyContentProvider {
	private static final int UPDATE_INTERVAL = 500;

	private IRequestLog requestLog;
	private TableViewer tableViewer;
	private List<IRequestLogRecord> records;
	private IRequestLogUpdateListener callback;

	private TimerTask updateTask;
	private final Timer updateTimer = new Timer();
	private int lastUpdateCount;
	private int currentCount;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		tableViewer = (TableViewer) viewer;
		if(updateTask != null)
			updateTask.cancel();

		if(newInput == null) {
			currentCount = lastUpdateCount = 0;
			tableViewer.setItemCount(0);
			if(requestLog != null)
				requestLog.removeUpdateListener(callback);
			updateTask = null;
			return;
		}
		requestLog = (IRequestLog) newInput;
		callback = createUpdateListener();

		records = requestLog.getAllRecords();
		currentCount = lastUpdateCount = records.size();
		tableViewer.setItemCount(currentCount);
		requestLog.addUpdateListener(callback);
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


	@Override
	public void updateElement(int index) {
		if(index >= records.size()) {
			records = requestLog.getAllRecords();
		}

		IRequestLogRecord record = records.get(index);
		if(record != null) {
			tableViewer.replace(record, index);
		}
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
