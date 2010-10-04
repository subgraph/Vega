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

import com.subgraph.vega.api.scanner.model.IScanAlert;
import com.subgraph.vega.ui.scanner.Activator;

import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class AlertRenderer {
	
	private Configuration configuration;
	private final String imageURL;
	AlertRenderer() {
		imageURL = findImage();
		configuration = new Configuration();
		configuration.setTemplateLoader(new TemplateLoader());
		configuration.setObjectWrapper(new DefaultObjectWrapper());
	}
	
	public String render(IScanAlert alert) {
		
		Map<String, Object> root = new HashMap<String, Object>();
		try {
			Template t = configuration.getTemplate("main.ftl");
			Document xmlRoot = alert.getReportXML();
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
