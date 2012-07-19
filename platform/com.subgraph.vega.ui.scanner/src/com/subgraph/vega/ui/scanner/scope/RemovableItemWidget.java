package com.subgraph.vega.ui.scanner.scope;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.model.scope.ITargetScope;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.images.ImageCache;

public abstract class RemovableItemWidget extends Composite {
	private final static String REMOVE_ICON = "icons/remove.png";
	private final static ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	
	protected final Text addText;
	protected final Button addButton;
	private final TableViewer tableViewer;
	
	protected ITargetScope currentScope;

	public RemovableItemWidget(Composite parent, String title, StructuredViewer scopeViewer) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		final Group group = new Group(this, SWT.NONE);
		group.setText(title);
		group.setLayout(new GridLayout(2, false));
		tableViewer = createTableViewer(group);
		addText = createText(group, createModifyListener());
		addButton = createAddButton(group, createSelectionListener());
		currentScope = getScopeFromSelection(scopeViewer.getSelection());
		scopeViewer.addSelectionChangedListener(createSelectionChangedListener());
		reloadInput();
	}
	
	
	
	protected void reloadInput() {
		tableViewer.setInput(getTableInput());
		tableViewer.getTable().getParent().layout();
	}
	
	private ISelectionChangedListener createSelectionChangedListener() {
		return new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				currentScope = getScopeFromSelection(event.getSelection());
				reloadInput();
			}
		};
	}
	
	private ITargetScope getScopeFromSelection(ISelection selection) {
		if(!(selection instanceof StructuredSelection)) {
			return null;
		}
		final Object ob = ((StructuredSelection)selection).getFirstElement();
		if(!(ob instanceof ITargetScope)) {
			return null;
		}
		return (ITargetScope) ob;
	}
	
	private ModifyListener createModifyListener() {
		return new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				addButton.setEnabled(verifyText(addText.getText()));
			}
		};
	}

	private SelectionListener createTextSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(handleInput(addText.getText())) {
					addText.setText("");
					reloadInput();
				}
				
			}
		};
	}
	
	private SelectionAdapter createSelectionListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(handleInput(addText.getText())) {
					addText.setText("");
					reloadInput();
				}
			}
		};
	}

	abstract Object[] getTableInput();
	abstract boolean verifyText(String input);
	abstract boolean handleInput(String input);
	abstract boolean handleRemoveElement(Object element);
	
	private Text createText(Composite parent, ModifyListener modifyListener) {
		final Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(modifyListener);
		text.addSelectionListener(createTextSelectionListener());
		text.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent e) {
				if(e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
				}
			}
		});
		return text;
	}

	private Button createAddButton(Composite parent, SelectionListener selectionListener) {
		final Button button = new Button(parent, SWT.NONE);
		button.setText("Add");
		button.setEnabled(false);
		button.addSelectionListener(selectionListener);
		return button;
	}
	
	private TableViewer createTableViewer(Composite parent) {
		final Composite tableComposite = new Composite(parent, SWT.NONE);
		final TableViewer tableViewer = new TableViewer(tableComposite, SWT.V_SCROLL | SWT.BORDER);
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(false);
		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				handleTableMouseDown(event, tableViewer);
			}
		});
		final TableColumnLayout layout = new TableColumnLayout();
		createDataColumn(tableViewer, layout);
		createRemoveColumn(tableViewer, layout);
		tableComposite.setLayout(layout);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.heightHint = 80;
		tableComposite.setLayoutData(gd);
		tableViewer.setContentProvider(new ArrayContentProvider());
		return tableViewer;
	}
	
	private void handleTableMouseDown(Event e, TableViewer tableViewer) {
		final Table table = tableViewer.getTable();
		final Rectangle clientArea = table.getClientArea();
		Point point = new Point(e.x, e.y);
		for(int i = table.getTopIndex(); i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Rectangle rect = item.getBounds(1);
			if(!rect.intersects(clientArea)) {
				return;
			}
			if(rect.contains(point)) {
				if(handleRemoveElement(tableViewer.getElementAt(i))) {
					reloadInput();
				}
			}
		}
	}
	
	private void createDataColumn(TableViewer viewer, TableColumnLayout layout) {
		final TableViewerColumn tvc = createTableColumn(viewer, layout, SWT.LEFT, new ColumnWeightData(100));
		tvc.setLabelProvider(new ColumnLabelProvider());
	}
	
	private void createRemoveColumn(TableViewer viewer, TableColumnLayout layout) {
		final TableViewerColumn tvc = createTableColumn(viewer, layout, SWT.CENTER, new ColumnPixelData(20, false, true));
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}
			@Override
			public Image getImage(Object element) {
				return imageCache.get(REMOVE_ICON);
			}
		});
	}
	
	private TableViewerColumn createTableColumn(TableViewer viewer, TableColumnLayout layout, int align, ColumnLayoutData layoutData) {
		final TableViewerColumn tvc = new TableViewerColumn(viewer, align);
		final TableColumn tc = tvc.getColumn();
		tc.setMoveable(false);
		tc.setResizable(false);
		layout.setColumnData(tc, layoutData);
		return tvc;
	}
}
