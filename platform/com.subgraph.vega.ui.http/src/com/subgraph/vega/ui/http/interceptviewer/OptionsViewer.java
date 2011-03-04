package com.subgraph.vega.ui.http.interceptviewer;

import java.util.Iterator;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.resource.ImageDescriptor;

import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointMatchType;
import com.subgraph.vega.api.http.proxy.HttpInterceptorBreakpointType;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.api.http.proxy.IHttpInterceptorBreakpoint;
import com.subgraph.vega.ui.http.Activator;

public class OptionsViewer {
	private Composite parentComposite;
	private TableViewer tableViewerBreakpoints;
	private static final Image CHECKED = ImageDescriptor.createFromFile(null, "icons/checked.gif").createImage();
	private static final Image UNCHECKED = ImageDescriptor.createFromFile(null, "icons/unchecked.gif").createImage();
	private ComboViewer comboViewerBreakpointTypes;
	private ComboViewer comboViewerBreakpointMatchTypes;
	private Text patternBreakpointText;
	private IHttpInterceptor interceptor;

	public Composite createViewer(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new FillLayout());
		createBreakpointsEditor(parentComposite);
		interceptor = Activator.getDefault().getProxyService().getInterceptor();
		tableViewerBreakpoints.setInput(interceptor);

		return parentComposite;
	}
	
	public Composite getViewer() {
		return parentComposite;
	}

	private Composite createBreakpointsEditor(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Breakpoints");
		rootControl.setLayout(new GridLayout(2, false));

		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		final Composite compTable = createTableBreakpoints(rootControl, gd, 7);
		compTable.setLayoutData(gd);
		final Composite compTableButtons = createTableBreakpointsButtons(rootControl);
		compTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		final Composite compCreate = createCreatorBreakpoints(rootControl);
		compCreate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		final Composite compCreateButtons = createCreatorBreakpointsButtons(rootControl);
		compCreateButtons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		return rootControl;
	}
	
	private Composite createTableBreakpoints(Composite parent, GridData gd, int heightInRows) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		final TableColumnLayout tcl = new TableColumnLayout();
		rootControl.setLayout(tcl);

		tableViewerBreakpoints = new TableViewer(rootControl, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createTableBreakpointsColumns(tableViewerBreakpoints, tcl);
		tableViewerBreakpoints.setContentProvider(new BreakpointsTableContentProvider());
		final Table table = tableViewerBreakpoints.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gd.heightHint = table.getItemHeight() * heightInRows;

		return rootControl;
	}

	private void createTableBreakpointsColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "", "Type", "Matches", "Pattern", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(20, false, true),
			new ColumnPixelData(150, true, true),
			new ColumnPixelData(150, true, true),
			new ColumnPixelData(350, true, true),
		};
		final EditingSupport editorList[] = {
				new BreakpointEnabledEditingSupport(viewer),
				new BreakpointTypeEditingSupport(viewer),
				new BreakpointMatchTypeEditingSupport(viewer),
				new BreakpointPatternEditingSupport(viewer),
		};
		final ColumnLabelProvider providerList[] = {
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return null;
				}

				@Override
				public Image getImage(Object element) {
					if (((IHttpInterceptorBreakpoint) element).getIsEnabled()) {
						return CHECKED;
					} else {
						return UNCHECKED;
					}
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpInterceptorBreakpoint) element).getType().getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpInterceptorBreakpoint) element).getMatchType().getName();
				}
			},		
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpInterceptorBreakpoint) element).getPattern();
				}
			},
		};

		for (int i = 0; i < titles.length; i++) {
			final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn c = column.getColumn();
			layout.setColumnData(c, layoutData[i]);			
			c.setText(titles[i]);
			c.setMoveable(true);
			column.setEditingSupport(editorList[i]);
			column.setLabelProvider(providerList[i]);
		}	
	}

	private Composite createTableBreakpointsButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		Button button = new Button(rootControl, SWT.PUSH);
		button.setText("remove");
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		button.addSelectionListener(createSelectionListenerButtonRemove());

		return rootControl;
	}
	
	private SelectionListener createSelectionListenerButtonRemove() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewerBreakpoints.getSelection();
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					interceptor.removeBreakpoint((IHttpInterceptorBreakpoint) i.next());
				}
				tableViewerBreakpoints.refresh();
			}
		};
	}

	private Composite createCreatorBreakpoints(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(3, false));

		comboViewerBreakpointTypes = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerBreakpointTypes.setContentProvider(new ArrayContentProvider());
		comboViewerBreakpointTypes.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((HttpInterceptorBreakpointType) element).getName();
			}
		});
		final HttpInterceptorBreakpointType[] typesList = HttpInterceptorBreakpointType.values();
		comboViewerBreakpointTypes.setInput(typesList);
		comboViewerBreakpointTypes.setSelection(new StructuredSelection(typesList[0]));

		comboViewerBreakpointMatchTypes = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerBreakpointMatchTypes.setContentProvider(new ArrayContentProvider());
		comboViewerBreakpointMatchTypes.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((HttpInterceptorBreakpointMatchType) element).getName();
			}
		});
		HttpInterceptorBreakpointMatchType[] matchTypesList = HttpInterceptorBreakpointMatchType.values();
		comboViewerBreakpointMatchTypes.setInput(matchTypesList);
		comboViewerBreakpointMatchTypes.setSelection(new StructuredSelection(matchTypesList[0]));

		patternBreakpointText = new Text(rootControl, SWT.BORDER);
		patternBreakpointText.setMessage("regular expression");
		patternBreakpointText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		return rootControl;
	}

	private Composite createCreatorBreakpointsButtons(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(1, true));

		Button button = new Button(rootControl, SWT.PUSH);
		button.setText("create");
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		button.addSelectionListener(createSelectionListenerButtonCreateBreakpoint());

		return rootControl;
	}

	private SelectionListener createSelectionListenerButtonCreateBreakpoint() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				HttpInterceptorBreakpointType breakpointType = (HttpInterceptorBreakpointType) ((IStructuredSelection) comboViewerBreakpointTypes.getSelection()).getFirstElement();
				HttpInterceptorBreakpointMatchType matchType = (HttpInterceptorBreakpointMatchType) ((IStructuredSelection) comboViewerBreakpointMatchTypes.getSelection()).getFirstElement();
				String pattern = patternBreakpointText.getText();
				if (breakpointType != null && matchType != null && pattern != null) {
					interceptor.createBreakpoint(breakpointType, matchType, pattern, true);
					tableViewerBreakpoints.refresh();
				}
			}
		};
	}

}
