package com.subgraph.vega.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.paths.IPathFinder;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

public class AlertExporter {
	IScanAlertRepository alertRepository;
	ReportRenderer renderer;	
	private final Logger logger = Logger.getLogger("alert-exporter");


	public AlertExporter(IWorkspace workspace) {
		alertRepository = workspace.getScanAlertRepository();
		renderer = new ReportRenderer(createTemplateLoader());
	}
	
	public AlertExporter() {
		renderer = new ReportRenderer(createTemplateLoader());
	}
	
	public void exportAlertsbyList(List<IScanAlert> alerts) {
		writeFile("/tmp/test2.html", renderer.renderList(alerts));
	}
	
	public void exportAllAlerts() {
		
		List <IScanInstance> scanInstances = alertRepository.getAllScanInstances();
		
		for (IScanInstance s : scanInstances) {
			List<IScanAlert> scanInstanceAlerts = s.getAllAlerts();
			writeFile("/tmp/test.html",renderer.renderList(scanInstanceAlerts));
		}
	}
	
	public void exportbyScanInstance(IScanInstance scanInstance) {
		writeFile("/tmp/test.html",renderer.reportFromScanInstance(scanInstance));
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

	private void writeFile(String path, String data) {
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(path), "utf-8"));
		    writer.write(data);
		} catch (IOException e) {
	        Status status = new Status(IStatus.ERROR, "com.subgraph.vega.export", 0,
	        		"Error writing alerts to file: " + e.getMessage(), null);
	        ErrorDialog.openError(Display.getCurrent().getActiveShell(),
	                "File I/O Error", "Alerts export failed.", status);
			logger.log(Level.WARNING, "I/O error on write to file." + e.getMessage());
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
		
	}
	
	
}
