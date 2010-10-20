package com.subgraph.vega.ui.scanner.info;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.scanner.dashboard.DashboardPane;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

public class ScanInfoView extends ViewPart {
	private final Logger logger = Logger.getLogger("info-view");

	public static String ID = "com.subgraph.vega.views.scaninfo";
	
	private Browser browser;
	private DashboardPane dashboard;
	private Composite contentPanel;
	private final AlertRenderer renderer;
	private StackLayout stackLayout = new StackLayout();
	
	public ScanInfoView() {
		final TemplateLoader loader = createTemplateLoader();
		if(loader == null)
			renderer = null;
		else
			renderer = new AlertRenderer(loader);
	}
	
	private TemplateLoader createTemplateLoader() {
		final IPathFinder pathFinder = Activator.getDefault().getPathFinder();
		if(pathFinder == null)
			throw new IllegalStateException("Cannot find templates to render because path finder service is not available");
		final File templateDirectory = new File(pathFinder.getDataDirectory(), "templates");
		try {
			return new FileTemplateLoader(templateDirectory);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to open template directory: "+ e.getMessage());
			return null;
		}
		
	}

	@Override
	public void createPartControl(Composite parent) {
		contentPanel = new Composite(parent, SWT.NONE);
		contentPanel.setLayout(stackLayout);
		dashboard = new DashboardPane(contentPanel);
		browser = new Browser(contentPanel, SWT.NONE);
		
		getSite().getPage().addSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if(o instanceof IScanAlert) {		
					IScanAlert alert = (IScanAlert) o;
					displayAlert(alert);
				}				
			}
			
		});
		stackLayout.topControl = dashboard;
		contentPanel.layout();
	}

	private void displayAlert(IScanAlert alert) {
		String html = renderer.render(alert);
		if(html != null && !browser.isDisposed())
			browser.setText(html, true);
		stackLayout.topControl = browser;
		contentPanel.layout();
	}
	
	public void showDashboard() {
		stackLayout.topControl = dashboard;
		contentPanel.layout();
	}
	
	@Override
	public void setFocus() {
		browser.setFocus();
	}

}
