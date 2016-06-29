package com.subgraph.vega.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
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
import com.subgraph.vega.api.model.alerts.IScanAlert.Severity;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.api.model.requests.IRequestLog;
import com.subgraph.vega.api.model.requests.IRequestLogRecord;
import com.subgraph.vega.api.xml.IXmlRepository;

import freemarker.cache.TemplateLoader;
import freemarker.core.ParseException;
import freemarker.ext.dom.NodeModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class ReportRenderer {
	private final Logger logger = Logger.getLogger("report-render");
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
	
	public ReportRenderer(TemplateLoader templateLoader) {
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
	
	private String reportHeader() {
		String output = "<html>\n" +
	                    "  <head>" +
				        "    <title>Subgraph Vega Scan Results</title>" +
	                    "    <style>" + reportCss() + "</style>" + 
				        "  </head>" +
	                    "  <body> ";
		
		return output;
		
	}
	
	public String reportSummary(List<IScanAlert> alerts) {
		
		HashMap<Severity,HashMap<String, Integer>> reportSummary = reportScanInstanceSummary(alerts);
		String output = "";
		
		if (reportSummary != null && reportSummary.isEmpty() == false) {
			
			output += "<div class=summary-page>\n";
			output += "<a name=summary-page></a>\n";

            output += "<div class=vegabanner id=vegabanner>\n";
            output += "<img class=bannerLogo width=173px height=24px>";
            output += "<span>Open Source Web Security Platform</span></div>\n";
           
			output += "<div class=summary-title-block>\n";
			output += "<h1>Subgraph Vega Vulnerability Scan Report</h1>\n";
			output += "</div>";
			output += "<div class=summary-table>\n";
			output += "<span class=table-title>Summary</span>\n";
			output += "<table>\n";
			output += "<col style=\"width: 95%;\">\n<col style=\"width: 5%;\">\n";
			output += "<thead><th style=\"text-align: left;\">FINDINGS</th><th>TOTAL</th></thead>\n<tbody>\n";
			
			HashMap<Severity, Integer> severityTotals = new HashMap<Severity, Integer>();
			
			for (Severity s: Severity.values()) {
				severityTotals.put(s, new Integer(0));
					for (String alertTitle : reportSummary.get(s).keySet()) {
						int i = severityTotals.get(s);
						severityTotals.put(s, new Integer(i + reportSummary.get(s).get(alertTitle).intValue()));
					}
			}
			
			
			for (Severity s : Severity.values()) {
				if ((reportSummary.get(s) != null) && (reportSummary.get(s).isEmpty() == false)) {
					output += "<tr class=severity-row><td class=severity-name><a href=\"#section-" + s.name() +"\">"+ this.severityToString(s) + "</td><td class=severity-"+s.name() +">"+severityTotals.get(s)+"</td></tr>\n";
					for (String alertTitle : reportSummary.get(s).keySet()) {
						output += "<tr class=alert-row><td>" + alertTitle + "</td><td class=alert-count>" + reportSummary.get(s).get(alertTitle) + "</td></tr>\n";
					}	
				}	
			}
			
			output += "</tbody>\n</table></div>\n" +
			          "<div class=detailed-findings><a href=\"#alert-0\">Detailed Findings</a></div>" +
			          "<span class=summary-bottom>\n<hr>\n" +
					  "<span class=footertext>Report generated "+ this.currentDateTimeString() + " by the Vega open source web application security testing framework."+
                      "<br />"+
                      "<a href=\"https://subgraph.com\">https://subgraph.com</a><br />"+
                      "</span></span></div>\n";
		}	
		return output;
	}
	
	
	
	private HashMap<Severity, HashMap<String, Integer>> reportScanInstanceSummary(List<IScanAlert> alerts) {
				
		HashMap<Severity, HashMap<String, Integer>> alertSeverityCounts = new HashMap<Severity, HashMap<String, Integer>>();

		for (Severity s : Severity.values()){
			HashMap<String, Integer> h = new HashMap<String, Integer>();
			alertSeverityCounts.put(s, h);
		}
		
		for (IScanAlert alert : alerts) {
			HashMap<String,Integer> severityAlertsHashMap;
			severityAlertsHashMap = alertSeverityCounts.get(alert.getSeverity());
			
			if (severityAlertsHashMap == null) {
				HashMap<String, Integer> hm = new HashMap<String, Integer>();
				hm.put(alert.getTitle(), new Integer(1));
				alertSeverityCounts.put(alert.getSeverity(), hm);
			} else {
				if (severityAlertsHashMap.get(alert.getTitle()) == null) {
					severityAlertsHashMap.put(alert.getTitle(), new Integer(1));
				} else 
				{
					Integer i = severityAlertsHashMap.get(alert.getTitle());
					severityAlertsHashMap.put(alert.getTitle(), new Integer(i.intValue() + 1));
				}
			}
		}
					

		return alertSeverityCounts;
	}
	
	

	
	public String renderList(List<IScanAlert> alerts) {
		final int maxAlertString = 400; //Activator.getDefault().getPreferenceStore().getInt(IPreferenceConstants.P_MAX_ALERT_STRING);

		Map<String, Object> root = new HashMap<String, Object>();
		String output = "";
		int i = 0;
		
		output += reportHeader();
		output += reportSummary(alerts);
		
		List<IScanAlert> alertsArrayList = new ArrayList<IScanAlert>(alerts);	
		Collections.sort(alertsArrayList, new Comparator<IScanAlert>(){

			@Override
			public int compare(IScanAlert alert1, IScanAlert alert2) {				
				return alert1.getSeverity().compareTo(alert2.getSeverity());
			}	
		});

		String severity = "";
		for (IScanAlert alert : alertsArrayList) {
			try {
				
				if (alert.getSeverity().name().equals(severity) == false) {
					output += "<a name=section-"+alert.getSeverity().name()+"></a>";
					severity = alert.getSeverity().name();
				}
												
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
				if(bulletPointURL != null) {
					vars.put("bulletPointURL", bulletPointURL);
				}
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
						
						/* TODO: Render the response, with highlights */
						
						vars.put("requestId", Long.toString(alert.getRequestId()));
					}
				}
				
				vars.put("num", Integer.toString(i));
				
				if (i < alerts.size()-1) {
					vars.put("next", Integer.toString(i+1));
				} else {
					vars.put("next", null);
				}
				
				if (i > 0) {
					vars.put("prev", Integer.toString(i-1));
				} else {
					vars.put("prev", null);
				}
				
				vars.put("datetime", currentDateTimeString());
				
				if (alert.getDiscretionaryHostname() != null) {
					vars.put("hostname", alert.getDiscretionaryHostname());
				}
				
				long requestID = alert.getRequestId();
				String responseText = renderResponse(requestID);
				vars.put("responseText", responseText);
				
				root.put("vars", vars);
				
				StringWriter out = new StringWriter();
				t.process(root, out);
				out.flush();
				output = output.concat(out.toString());
				i += 1;
				
			} catch (IOException e) {
				return "I/O error reading alert template file alerts/"+ alert.getName() + ".xml :<br><br>"+ e.getMessage();
			} catch (TemplateException e) {
				return "Error processing alert template file alerts/"+ alert.getName() +".xml :<br><br>"+ e.getMessage();
			}
		}
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

	private String reportCss() {

		Map<String,Object> vars = new HashMap<String,Object>();
		Map<String, Object> root = new HashMap<String, Object>();
		
		try {
			Template t = configuration.getTemplate("report-style.ftl");
			
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
			
			root.put("vars", vars);
			StringWriter out = new StringWriter();
			t.process(root, out);
			out.flush();
			return out.toString();
			
		} catch (TemplateNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedTemplateNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;
	}

	private String currentDateTimeString() {
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		Date date = new Date();
		return dateFormatter.format(date);
	}

	public String reportFromScanInstance(IScanInstance scanInstance) {

		String output = reportHeader();
		output += reportSummary(scanInstance.getAllAlerts());
		output += renderList(scanInstance.getAllAlerts());
		output = output.concat("</body></html>");
		return output;
	}
	
	public String renderResponse(long requestID) {
		
		String responseString = "";
		String body = "";
		
		IRequestLogRecord request = requestLog.lookupRecord(requestID);
		if (request != null) {
		HttpResponse response = request.getResponse();
		
			for(Header h: response.getAllHeaders()) {
				responseString += h + "\n";
			}
			try {
				body = toString(response.getEntity(), null);
			}
		
			catch (ParseException e) {
				//	logger.log(Level.WARNING, "Error parsing response headers: "+ e.getMessage(), e);
				body = "";
			} catch (IOException e) {
				//	logger.log(Level.WARNING, "IO error extracting response entity for request "+ request.getRequestLine().getUri() +" : "+ e.getMessage(), e);
				body = "";
			}
		} else {
			responseString = "Not stored.";
		}
		return responseString + "\n\n" + body;
	}

	
	   public static String toString(
	            final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
	        if (entity == null) {
	            throw new IllegalArgumentException("HTTP entity may not be null");
	        }
	        InputStream instream = entity.getContent();
	        if (instream == null) {
	            return null;
	        }
	        try {
	            if (entity.getContentLength() > Integer.MAX_VALUE) {
	                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
	            }
	            int i = (int)entity.getContentLength();
	            if (i < 0) {
	                i = 4096;
	            }
	            Charset charset = null;
	            try {
	                ContentType contentType = ContentType.getOrDefault(entity);
	                charset = contentType.getCharset();
	            } catch (UnsupportedCharsetException ex) {
	            	// In EntityUtils an exception is thrown here.
	            	charset = null;
	            }
	            if (charset == null) {
	                charset = defaultCharset;
	            }
	            if (charset == null) {
	                charset = HTTP.DEF_CONTENT_CHARSET;
	            }
	            Reader reader = new InputStreamReader(instream, charset);
	            CharArrayBuffer buffer = new CharArrayBuffer(i);
	            char[] tmp = new char[1024];
	            int l;
	            while((l = reader.read(tmp)) != -1) {
	                buffer.append(tmp, 0, l);
	            }
	            return buffer.toString();
	        } finally {
	            instream.close();
	        }
	    }

	
}

