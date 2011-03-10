package com.subgraph.vega.ui.scanner.info;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;

import com.subgraph.vega.api.events.IEvent;
import com.subgraph.vega.api.events.IEventHandler;
import com.subgraph.vega.api.model.IWorkspace;
import com.subgraph.vega.api.model.WorkspaceCloseEvent;
import com.subgraph.vega.api.model.WorkspaceOpenEvent;
import com.subgraph.vega.api.model.WorkspaceResetEvent;
import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.xml.IXmlRepository;
import com.subgraph.vega.ui.scanner.Activator;

import freemarker.cache.TemplateLoader;
import freemarker.ext.dom.NodeModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class AlertRenderer {
	private final Logger logger = Logger.getLogger("alert-render");
	private Configuration configuration;
	private final String imageURL;
	private final Map<String, Document> alertDocumentCache = new HashMap<String, Document>();
	private IRequestLog requestLog;
	
	AlertRenderer(TemplateLoader templateLoader) {
		imageURL = findImage();
		configuration = new Configuration();
		configuration.setTemplateLoader(templateLoader);
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		final IWorkspace currentWorkspace = Activator.getDefault().getModel().addWorkspaceListener(new IEventHandler() {
			@Override
			public void handleEvent(IEvent event) {
				if(event instanceof WorkspaceOpenEvent)
					handleWorkspaceOpen((WorkspaceOpenEvent) event);
				else if(event instanceof WorkspaceCloseEvent)
					handleWorkspaceClose((WorkspaceCloseEvent) event);
				else if(event instanceof WorkspaceResetEvent)
					handleWorkspaceReset((WorkspaceResetEvent) event);				
			}
		});
		if(currentWorkspace != null)
			requestLog = currentWorkspace.getRequestLog();
	}
	
	private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
		requestLog = event.getWorkspace().getRequestLog();
	}
	
	private void handleWorkspaceClose(WorkspaceCloseEvent event) {
		requestLog = null;
	}
	
	private void handleWorkspaceReset(WorkspaceResetEvent event) {
		requestLog = event.getWorkspace().getRequestLog();
	}
	
	public String render(IScanAlert alert) {
		
		Map<String, Object> root = new HashMap<String, Object>();
		try {
			Template t = configuration.getTemplate("main.ftl");
			Document xmlRoot = getAlertDocument(alert.getName());
			if(xmlRoot == null)
				return "";
			NodeModel nodeModel = NodeModel.wrap(xmlRoot);
			root.put("doc", nodeModel);
			Map<String,Object> vars = new HashMap<String,Object>();
			for(String k: alert.propertyKeys()) 
				vars.put(k, alert.getProperty(k));
			String severityVar = severityToString(alert.getSeverity());
			if(severityVar != null) {
				vars.put("severity", severityVar);
			}
			String severityCSSVar = severityToSeverityCSSClass(alert.getSeverity());
			if(severityCSSVar != null) {
				vars.put("severityCSS", severityCSSVar);
			}
			if(imageURL != null)
				vars.put("imageURL", imageURL);
			
			if(alert.getRequestId() >= 0 && requestLog != null) {
				final IRequestLogRecord record = requestLog.lookupRecord(alert.getRequestId());
				if(record != null) {
					vars.put("requestId", Long.toString(alert.getRequestId()));
					vars.put("requestText", record.getRequest().getRequestLine().getUri());
				}
			}
			root.put("vars", vars);
			
			StringWriter out = new StringWriter();
			t.process(root, out);
			out.flush();
			return out.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Document getAlertDocument(String name) {
		if(alertDocumentCache.containsKey(name))
			return alertDocumentCache.get(name);
		final IXmlRepository xmlRepository = Activator.getDefault().getXmlRepository();
		if(xmlRepository == null) {
			logger.warn("Could not render alert because xml repository service is not available");
			return null;
		}
		
		Document alertDocument = xmlRepository.getDocument("alerts/"+ name + ".xml");
		if(alertDocument == null)
			alertDocument = xmlRepository.getDocument("alerts/default.xml");
		if(alertDocument == null) {
			logger.warn("Could not load XML data for alert named '"+ name + "'");
			return null;
		}
		alertDocumentCache.put(name, alertDocument);
		return alertDocument;
	}
	private String findImage() {
		Bundle b = Activator.getDefault().getBundle();
		IPath relativePagePath = new Path("icons/vega_logo.png");
		URL fileInPlugin = FileLocator.find(b, relativePagePath, null);
		try {
			URL pageUrl = FileLocator.toFileURL(fileInPlugin);
			return pageUrl.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private String severityToString(IScanAlert.Severity s) {
		switch(s) {
		case HIGH:
			return "High";
		case MEDIUM:
			return "Medium";
		case LOW:
			return "Low";
		case INFO:
			return "Info";
		case UNKNOWN:
			return "Unknown";
		}
		return null;
	}
	
	private String severityToSeverityCSSClass(IScanAlert.Severity s) {
		switch(s) {
		case HIGH:
			return "highrisk";
		case MEDIUM:
			return "medrisk";
		case LOW:
			return "lowrisk";
		case INFO:
			return "inforisk";
		case UNKNOWN:
			return "unknownrisk";
			
		}
		return null;
	}
}
