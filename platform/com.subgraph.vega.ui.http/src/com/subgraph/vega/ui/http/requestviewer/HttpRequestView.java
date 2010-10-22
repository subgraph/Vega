package com.subgraph.vega.ui.http.requestviewer;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.requestlog.IRequestLogRecord;

public class HttpRequestView extends ViewPart {
	private TableViewer tableViewer;
	private RequestResponseViewer requestResponseViewer;
	private HttpViewFilter filter;

	public HttpRequestView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		final SashForm form = new SashForm(parent, SWT.VERTICAL);
		final Composite comp = new Composite(form, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		comp.setLayout(tcl);
		
		tableViewer = new TableViewer(comp);
		createColumns(tableViewer, tcl);
		
		tableViewer.setContentProvider(new HttpViewContentProvider());
		tableViewer.setLabelProvider(new HttpViewLabelProvider());
		IModel model = Activator.getDefault().getModel();
		if(model != null)
			tableViewer.setInput(model.getCurrentWorkspace().getRequestLog());
		requestResponseViewer = new RequestResponseViewer(form);
		form.setWeights(new int[] {40, 60});
		parent.pack();
		
		filter = new HttpViewFilter();
		tableViewer.addFilter(filter);
		tableViewer.addSelectionChangedListener(createSelectionChangedListener());
		
		getSite().getPage().addSelectionListener(new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {
				if(!(selection instanceof IStructuredSelection))
					return;
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if(o == null) {
					filter.setFilterEntity(null);
					tableViewer.refresh();
				} else if(o instanceof IWebEntity) {
					filterByEntity((IWebEntity) o);
				}				
			}
		});
	}

	private void createColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = {"Host", "Method", "Request", "Status", "Length", };
		final ColumnLayoutData[] layoutData = {
				new ColumnPixelData(120, true, true),
				new ColumnPixelData(60, true, true),
				new ColumnWeightData(100, 100, true),
				new ColumnPixelData(50, true, true),
				new ColumnPixelData(80, true, true)
		};
		
		for(int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);			
			c.setText(titles[i]);
			c.setMoveable(true);
		}	
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	private void filterByEntity(IWebEntity entity) {
		filter.setFilterEntity(entity);
		tableViewer.refresh();
		if(tableViewer.getTable().getItemCount() == 1) {
			Object ob = tableViewer.getElementAt(0);
			tableViewer.setSelection(new StructuredSelection(ob), true);
		}
	}
	
	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if(selection.getFirstElement() instanceof IRequestLogRecord)
					requestResponseViewer.setCurrentRecord((IRequestLogRecord) selection.getFirstElement());
				else 
					requestResponseViewer.setCurrentRecord(null);				
			}
		};
	}
	
	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

}