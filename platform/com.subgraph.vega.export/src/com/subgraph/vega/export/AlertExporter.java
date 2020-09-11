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
import java.util.ArrayList;
import java.util.Date;
import java.lang.Thread;

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
	private String path = "";
	
	public void setPath(String path)
	{
		this.path = path;
	}


	public AlertExporter(IWorkspace workspace) {
		alertRepository = workspace.getScanAlertRepository();
		renderer = new ReportRenderer(createTemplateLoader());
	}
	
	public AlertExporter() {
		renderer = new ReportRenderer(createTemplateLoader());
		//alertRepository = Activator.getDefault().getModel().getCurrentWorkspace().getScanAlertRepository();
	}
	
	public void exportAlertsbyList(List<IScanAlert> alerts) {
		writeFile("/tmp/test2.html", renderer.renderList(alerts));
	}
	
	public void exportAlertsOfLastScan(){
		System.out.println("started exporting...");
		if(alertRepository == null)
		{
			alertRepository = Activator.getDefault().getModel().getCurrentWorkspace().getScanAlertRepository();
		}
		if(path.length() == 0)
		{
			path = "/tmp/test.html";
		}
		List <IScanInstance> scanInstances = alertRepository.getAllScanInstances();

		IScanInstance newestInstance = null; //scanInstances.get(0);
		Date newestInstanceStartTime = null; //newestInstance.getStartTime();

		int i = 0;
		for (IScanInstance s : scanInstances) {
			i++;
			if(s == null || s.getStartTime() == null)
			{
				continue;
			}
			
			if(newestInstance == null || 
					(s.getStartTime().after(newestInstanceStartTime) &
					((s.getScanStatus() == IScanInstance.SCAN_CANCELLED) || (s.getScanStatus() == IScanInstance.SCAN_COMPLETED))))
			{
				newestInstance = s;
				newestInstanceStartTime = s.getStartTime();
			}
		}
		
		writeFile(path,renderer.renderList(newestInstance.getAllAlerts()));
		
		
	}
	
	public void exportAllAlerts() {
		System.out.println("started exporting...");
		if(alertRepository == null)
		{
			alertRepository = Activator.getDefault().getModel().getCurrentWorkspace().getScanAlertRepository();
		}
		if(path.length() == 0)
		{
			path = "/tmp/test.html";
		}
		List <IScanInstance> scanInstances = alertRepository.getAllScanInstances();
		System.out.println("finished finding all instances.");
		int i = 0;
		List<IScanAlert> scanInstanceAlerts = new ArrayList<IScanAlert>();
		scanInstanceAlerts.addAll(scanInstances.get(0).getAllAlerts());
		for (IScanInstance s : scanInstances) {
			i++;
			if(s.getStartTime() == null || !((s.getScanStatus() == IScanInstance.SCAN_CANCELLED) || (s.getScanStatus() == IScanInstance.SCAN_COMPLETED)))
			{
				continue;
			}
			System.out.println("save instance nr. " + i);
			System.out.println("alerts: " + s.getAllAlerts().size());
			System.out.println("starttime: " + s.getStartTime());
			scanInstanceAlerts.addAll(s.getAllAlerts());
			
		}
		writeFile(path,renderer.renderList(scanInstanceAlerts));
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
