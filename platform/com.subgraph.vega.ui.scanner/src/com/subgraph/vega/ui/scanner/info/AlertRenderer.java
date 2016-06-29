/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.scanner.info;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;
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
import com.subgraph.vega.ui.scanner.preferences.IPreferenceConstants;

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
	private final String bulletPointURL;
	private final String bannerPatternURL;
	private final String bannerLogoURL;
	private final String titlePatternURL;
	private final String redArrowURL;
	private final String sectionGradientURL;
	private final String linkArrowURL;
	private final Map<String, Document> alertDocumentCache = new HashMap<String, Document>();
	private IRequestLog requestLog;
	
	public AlertRenderer(TemplateLoader templateLoader) {
		imageURL = findImage("icons/vega_logo.png");
		bulletPointURL = findImage("icons/doubleArrow.png");
		bannerPatternURL = findImage("icons/bannerPattern.png");
		bannerLogoURL = findImage("icons/bannerLogo.png");
		titlePatternURL = findImage("icons/titlePattern.png");
		redArrowURL = findImage("icons/redArrow.png");
		sectionGradientURL = findImage("icons/sectionGradient.png");
		linkArrowURL = findImage("icons/linkArrow.png");
		
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
		final int maxAlertString = Activator.getDefault().getPreferenceStore().getInt(IPreferenceConstants.P_MAX_ALERT_STRING);
		Map<String, Object> root = new HashMap<String, Object>();
		try {
			Template t = configuration.getTemplate("main.ftl");
			Document xmlRoot = getAlertDocument(alert.getName());
			if(xmlRoot == null)
				return "";
			NodeModel nodeModel = NodeModel.wrap(xmlRoot);
			root.put("doc", nodeModel);
			Map<String,Object> vars = new HashMap<String,Object>();
			for(String k: alert.propertyKeys()) {
				Object value = alert.getProperty(k);
				if(value instanceof String) {
					String s = (String) value;
					if(s.length() > maxAlertString) {
						s = s.substring(0, maxAlertString) + "...";
					} 
					vars.put(k, s);
				} else {
					vars.put(k, alert.getProperty(k));
				}
			}
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
			if(bulletPointURL != null)
				vars.put("bulletPointURL", bulletPointURL);
			if(bannerPatternURL != null)
				vars.put("bannerPatternURL", bannerPatternURL);
			if(bannerLogoURL != null)
				vars.put("bannerLogoURL", bannerLogoURL);
			if(titlePatternURL != null)
				vars.put("titlePatternURL", titlePatternURL);
			if(redArrowURL != null)
				vars.put("redArrowURL", redArrowURL);
			if(redArrowURL != null)
				vars.put("sectionGradientURL", sectionGradientURL);
			if(linkArrowURL != null)
				vars.put("linkArrowURL", linkArrowURL);
			
			if(alert.getRequestId() >= 0 && requestLog != null) {
				final IRequestLogRecord record = requestLog.lookupRecord(alert.getRequestId());
				if(record != null) {
					if(record.getRequest() instanceof HttpEntityEnclosingRequest) {
						vars.put("requestText", renderEntityEnclosingRequest((HttpEntityEnclosingRequest) record.getRequest()));
					} else {
						vars.put("requestText", renderBasicRequest(record.getRequest()));
					}
					vars.put("requestId", Long.toString(alert.getRequestId()));
				}
			}
			root.put("vars", vars);
			
			StringWriter out = new StringWriter();
			t.process(root, out);
			out.flush();
			return out.toString();
		} catch (IOException e) {
			return "I/O error reading alert template file alerts/"+ alert.getName() + ".xml :<br><br>"+ e.getMessage();
		} catch (TemplateException e) {
			return "Error processing alert template file alerts/"+ alert.getName() +".xml :<br><br>"+ e.getMessage();
		}
	}
	
	public String renderList(List<IScanAlert> alerts) {
		final int maxAlertString = Activator.getDefault().getPreferenceStore().getInt(IPreferenceConstants.P_MAX_ALERT_STRING);

		Map<String, Object> root = new HashMap<String, Object>();
		String output = "<html><head><title>Report</title></head><body>";

		for (IScanAlert alert : alerts) {
			try {
				Template t = configuration.getTemplate("report-alert.ftl");
				Document xmlRoot = getAlertDocument(alert.getName());
				if(xmlRoot == null)
					return "";
				NodeModel nodeModel = NodeModel.wrap(xmlRoot);
				root.put("doc", nodeModel);
				Map<String,Object> vars = new HashMap<String,Object>();
				for(String k: alert.propertyKeys()) {
					Object value = alert.getProperty(k);
					if(value instanceof String) {
						String s = (String) value;
						if(s.length() > maxAlertString) {
							s = s.substring(0, maxAlertString) + "...";
						} 
						vars.put(k, s);
					} else {
						vars.put(k, alert.getProperty(k));
					}
				}
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
				if(bulletPointURL != null)
					vars.put("bulletPointURL", bulletPointURL);
				if(bannerPatternURL != null)
					vars.put("bannerPatternURL", bannerPatternURL);
				if(bannerLogoURL != null)
					vars.put("bannerLogoURL", bannerLogoURL);
				if(titlePatternURL != null)
					vars.put("titlePatternURL", titlePatternURL);
				if(redArrowURL != null)
					vars.put("redArrowURL", redArrowURL);
				if(redArrowURL != null)
					vars.put("sectionGradientURL", sectionGradientURL);
				if(linkArrowURL != null)
					vars.put("linkArrowURL", linkArrowURL);
				
				if(alert.getRequestId() >= 0 && requestLog != null) {
					final IRequestLogRecord record = requestLog.lookupRecord(alert.getRequestId());
					if(record != null) {
						if(record.getRequest() instanceof HttpEntityEnclosingRequest) {
							vars.put("requestText", renderEntityEnclosingRequest((HttpEntityEnclosingRequest) record.getRequest()));
						} else {
							vars.put("requestText", renderBasicRequest(record.getRequest()));
						}
						vars.put("requestId", Long.toString(alert.getRequestId()));
					}
				}
				root.put("vars", vars);
				
				StringWriter out = new StringWriter();
				t.process(root, out);
				out.flush();
				output += out.toString();
			} catch (IOException e) {
				return "I/O error reading alert template file alerts/"+ alert.getName() + ".xml :<br><br>"+ e.getMessage();
			} catch (TemplateException e) {
				return "Error processing alert template file alerts/"+ alert.getName() +".xml :<br><br>"+ e.getMessage();
			}
		}
		output += "</body></html>";
		return output;
	}
	
	private String renderEntityEnclosingRequest(HttpEntityEnclosingRequest request) {
		final HttpEntity entity = request.getEntity();
		if(entity == null || !URLEncodedUtils.isEncoded(entity)) {
			return renderBasicRequest(request);
		}
		
		try {
			List<NameValuePair> args = URLEncodedUtils.parse(entity);
			StringBuilder sb = new StringBuilder();
			sb.append(renderBasicRequest(request));
			sb.append("\n[");
			for(NameValuePair nvp: args) {
				sb.append(nvp.getName());
				if(nvp.getValue() != null) {
					sb.append("=");
					sb.append(nvp.getValue());
				}
				sb.append("\n");
			}
			sb.append("]");
			return sb.toString();
		} catch (IOException e) {
			return renderBasicRequest(request);
		}
	}
	
	private String renderBasicRequest(HttpRequest request) {
		final RequestLine line = request.getRequestLine();
		return line.getMethod() +" "+ line.getUri();
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
	private String findImage(String imagePath) {
		Bundle b = Activator.getDefault().getBundle();
		IPath relativePagePath = new Path(imagePath);
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
