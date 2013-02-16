package com.subgraph.vega.internal.ui.http.requestlogviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.model.conditions.IHttpConditionSet;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class FilterTask {
	private final static int UPDATE_VIEWER_INTERVAL = 500;
	private final static int INITIAL_CHUNK_SIZE = 200;
	private final static int LOAD_CHUNK_SIZE = 1000;
	private final IRequestLog requestLog;
	private final IHttpConditionSet filter;
	private final TableViewer viewer;
	private final Display display;
	/* records with (id < nextRequestId) */
	private final List<IRequestLogRecord> records;
	/* records with (id >= nextRequestId) */
	private final List<IRequestLogRecord> addedRecords;
	private final Thread thread;
	private final Object lock = new Object();
	
	private volatile boolean isStarted;
	private volatile boolean isCancelled;
	private boolean isFinished;
	private volatile long nextRequestId;
	private int lastSize;
	
	FilterTask(TableViewer viewer, IRequestLog requestLog, IHttpConditionSet filter) {
		this.viewer = viewer;
		this.display = viewer.getControl().getDisplay();
		this.requestLog = requestLog;
		this.filter = filter;
		this.records = new ArrayList<IRequestLogRecord>();
		this.addedRecords = new ArrayList<IRequestLogRecord>();
		this.thread = new Thread(createRunnable());
	}
	
	IRequestLogRecord getRecordForIndex(int index) {
		synchronized(lock) {
			if(index < records.size()) {
				return records.get(index);
			}
		}
		return null;
	}
	
	void addNewRecord(IRequestLogRecord record) {
		synchronized (lock) {
			if(isFinished) {
				records.add(record);
			} else {
				if(record.getRequestId() >= nextRequestId) {
					addedRecords.add(record);
				}
			}
		}
	}
	
	void applyFilter() {
		synchronized (lock) {
			if(isStarted) {
				return;
			}
			nextRequestId = requestLog.getNextRequestId();
			isStarted = true;
			thread.start();
		}
	}

	void cancel() {
		isCancelled = true;
	}
	
	private void loadRecords() {
		final Iterator<IRequestLogRecord> it = requestLog.getRecordIteratorByConditionSet(filter);
		if(loadChunk(it, INITIAL_CHUNK_SIZE)) {
			return;
		}
		if(records.size() > 0) {
			selectViewerItem(records.get(0));
		}
		while(it.hasNext()) {
			if(loadChunk(it, LOAD_CHUNK_SIZE)) {
				return;
			}
		}

		performFinalMerge();
	}

	private boolean loadChunk(Iterator<IRequestLogRecord> it, int chunkSize) {
		final int size;
		synchronized(lock) {
			for(int i = 0; i < chunkSize && it.hasNext(); i++) {
				if(isCancelled) {
					return true;
				}
				final IRequestLogRecord record = it.next();
				if(record.getRequestId() < nextRequestId) {
					records.add(record);
				}
			}
			size = records.size();
		}
		applyRecordCountToViewer(size);
		return false;
	}
	
	private void performFinalMerge() {
		final int size;
		synchronized (lock) {
			records.addAll(addedRecords);
			addedRecords.clear();
			size = records.size();
			isFinished = true;
			lock.notifyAll();
		}
		applyRecordCountToViewer(size);
	}

	
	
	private void selectViewerItem(final IRequestLogRecord record) {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				if(!viewer.getControl().isDisposed() && !isCancelled) {
					viewer.setSelection(new StructuredSelection(record));
				}
			}
			
		});
	}
	private void applyRecordCountToViewer(final int recordCount) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if(!viewer.getControl().isDisposed() && !isCancelled) {
					viewer.setItemCount(recordCount);
					viewer.refresh(false);
				}
			}
		});
	}
	
	private Runnable createRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				loadRecords();
				runUpdateViewerLoop();
			}
		};
	}
	
	private void runUpdateViewerLoop() {
		while(!isCancelled) {
			try {
				Thread.sleep(UPDATE_VIEWER_INTERVAL);
			} catch (InterruptedException e) {
				return;
			}
			if(isCancelled) {
				return;
			}
			synchronized(lock) {
				if(records.size() != lastSize) {
					lastSize = records.size();
					applyRecordCountToViewer(lastSize);
				}
			}
		}
	}
	
	public int getRowForRecord(IRequestLogRecord record) {
		synchronized (lock) {
			while(!isFinished) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					return -1;
				}
			}
			return records.indexOf(record);
		}
	}
}
