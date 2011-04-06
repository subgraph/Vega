package com.subgraph.vega.ui.http.interceptviewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.subgraph.vega.api.http.conditions.ConditionType;
import com.subgraph.vega.api.http.conditions.IHttpBooleanCondition;
import com.subgraph.vega.api.http.conditions.IHttpConditionSet;
import com.subgraph.vega.api.http.conditions.MatchType;
import com.subgraph.vega.api.http.conditions.TransactionDirection;
import com.subgraph.vega.api.http.proxy.HttpInterceptorLevel;
import com.subgraph.vega.api.http.proxy.IHttpInterceptor;
import com.subgraph.vega.ui.http.Activator;

public class OptionsViewer {
	private static final Image IMAGE_CHECKED = Activator.getImageDescriptor("icons/checked.png").createImage();
	private static final Image IMAGE_UNCHECKED = Activator.getImageDescriptor("icons/unchecked.png").createImage();
	private final TransactionDirection direction;
	private Composite parentComposite;
	private ComboViewer comboViewerInterceptorLevel;
	private TableViewer tableViewerBreakpoints;
	private ComboViewer comboViewerBreakpointTypes;
	private ComboViewer comboViewerBreakpointMatchTypes;
	private Text patternBreakpointText;
	private IHttpInterceptor interceptor;

	public OptionsViewer(TransactionDirection direction) {
		this.direction = direction;
	}

	public Composite createViewer(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, true));
		createInterceptorOptions(parentComposite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		createBreakpointsEditor(parentComposite).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		interceptor = Activator.getDefault().getProxyService().getInterceptor();
		comboViewerInterceptorLevel.setSelection(new StructuredSelection(interceptor.getInterceptLevel(direction)));
		tableViewerBreakpoints.setInput(interceptor);

		return parentComposite;
	}
	
	public TransactionDirection getDirection() {
		return direction;
	}

	public Composite getViewer() {
		return parentComposite;
	}

	private Composite createInterceptorOptions(Composite parent) {
		final Group rootControl = new Group(parent, SWT.NONE);
		rootControl.setText("Interceptor Options");
		rootControl.setLayout(new GridLayout(2, false));

		final Label label = new Label(rootControl, SWT.NONE);
		label.setText("Intercept for:");

		comboViewerInterceptorLevel = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerInterceptorLevel.setContentProvider(new ArrayContentProvider());
		comboViewerInterceptorLevel.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((HttpInterceptorLevel) element).getName();
			}
		});
		comboViewerInterceptorLevel.setInput(HttpInterceptorLevel.values());
		comboViewerInterceptorLevel.addSelectionChangedListener(createSelectionChangedListenerComboViewerInterceptorLevel());
		
		return rootControl;
	}

	private ISelectionChangedListener createSelectionChangedListenerComboViewerInterceptorLevel() {
		return new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent e) {
				HttpInterceptorLevel level = (HttpInterceptorLevel) ((IStructuredSelection) comboViewerInterceptorLevel.getSelection()).getFirstElement();
				if (level != null) {
					interceptor.setInterceptLevel(direction, level);
				}
			}
		};
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
		tableViewerBreakpoints.setContentProvider(new BreakpointsTableContentProvider(direction));
		final Table table = tableViewerBreakpoints.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		gd.heightHint = table.getItemHeight() * heightInRows;

		return rootControl;
	}

	private void createTableBreakpointsColumns(TableViewer viewer, TableColumnLayout layout) {
		final String[] titles = { "", "Type", "Matches", "Pattern", };
		final ColumnLayoutData[] layoutData = {
			new ColumnPixelData(16, false, true),
			new ColumnPixelData(150, true, true),
			new ColumnPixelData(150, true, true),
			new ColumnWeightData(100, 100, true),
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
					if (((IHttpBooleanCondition) element).getIsEnabled()) {
						return IMAGE_CHECKED;
					} else {
						return IMAGE_UNCHECKED;
					}
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpBooleanCondition) element).getType().getName();
				}
			},
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((MatchType)((IHttpBooleanCondition)element).getComparisonType()).getName();
				}
			},		
			new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IHttpBooleanCondition) element).getPattern();
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
				IHttpConditionSet conditionSet = interceptor.getBreakpointSet(direction);
				for (Iterator<?> i = selection.iterator(); i.hasNext();) {
					conditionSet.removeCondition((IHttpBooleanCondition) i.next());
				}
				tableViewerBreakpoints.refresh();
			}
		};
	}

	private List<ConditionType> getBreakpointTypesList() {
		final List<ConditionType> typesList = new ArrayList<ConditionType>();
		for (ConditionType t: ConditionType.values()) {
			if ((t.getMask() & direction.getMask()) != 0) {
				typesList.add(t);
			}
		}
		return typesList;
	}

	private Composite createCreatorBreakpoints(Composite parent) {
		final Composite rootControl = new Composite(parent, SWT.NONE);
		rootControl.setLayout(new GridLayout(3, false));

		comboViewerBreakpointTypes = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerBreakpointTypes.setContentProvider(new ArrayContentProvider());
		comboViewerBreakpointTypes.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((ConditionType) element).getName();
			}
		});
		final List<ConditionType> typesList = getBreakpointTypesList();
		comboViewerBreakpointTypes.setInput(typesList);
		if (typesList.size() != 0) {
			comboViewerBreakpointTypes.setSelection(new StructuredSelection(typesList.get(0)));
		}

		comboViewerBreakpointMatchTypes = new ComboViewer(rootControl, SWT.READ_ONLY);
		comboViewerBreakpointMatchTypes.setContentProvider(new ArrayContentProvider());
		comboViewerBreakpointMatchTypes.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((MatchType) element).getName();
			}
		});
		MatchType[] matchTypesList = MatchType.values();
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
				ConditionType breakpointType = (ConditionType) ((IStructuredSelection) comboViewerBreakpointTypes.getSelection()).getFirstElement();
				MatchType matchType = (MatchType) ((IStructuredSelection) comboViewerBreakpointMatchTypes.getSelection()).getFirstElement();
				String pattern = patternBreakpointText.getText();
				if (breakpointType != null && matchType != null && pattern != null) {
					IHttpConditionSet conditionSet = interceptor.getBreakpointSet(direction);
					conditionSet.createCondition(breakpointType, matchType, pattern, true);
					tableViewerBreakpoints.refresh();
					comboViewerBreakpointTypes.setSelection(new StructuredSelection(comboViewerBreakpointTypes.getElementAt(0)));
					comboViewerBreakpointMatchTypes.setSelection(new StructuredSelection(comboViewerBreakpointMatchTypes.getElementAt(0)));
					patternBreakpointText.setText("");
				}
			}
		};
	}

}
