package com.subgraph.vega.impl.scanner.forms;

import org.apache.http.client.methods.HttpUriRequest;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLDocument;

import com.subgraph.vega.api.html.IHTMLParseResult;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IModuleContext;
import com.subgraph.vega.api.scanner.IPathState;
import com.subgraph.vega.api.scanner.IScannerConfig;
import com.subgraph.vega.impl.scanner.urls.UriFilter;
import com.subgraph.vega.impl.scanner.urls.UriParser;

public class FormProcessor {

	private final IScannerConfig config;
	private final UriFilter uriFilter;
	private final UriParser uriParser;

	public FormProcessor(IScannerConfig config, UriFilter uriFilter, UriParser uriParser) {
		this.config = config;
		this.uriFilter = uriFilter;
		this.uriParser = uriParser;
	}

	public void processForms(IModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		final IHTMLParseResult html = response.getParsedHTML();
		if(html == null)
			return;

		final HTMLDocument document = html.getDOMDocument();
		final HTMLCollection forms = document.getForms();

		for(int i = 0; i < forms.getLength(); i++) {
			Node n = forms.item(i);
			if(n instanceof Element) {
				processFormElement(ctx, request, (Element) n);
			}
		}
	}

	private void processFormElement(IModuleContext ctx, HttpUriRequest request, Element form) {
		final FormProcessingState fps = new FormProcessingState(request.getURI(), form.getAttribute("action"), form.getAttribute("method"), config.getFormCredentials());
		if(!fps.isValid())
			return;

		NodeList es = form.getElementsByTagName("*");
		for(int i = 0; i < es.getLength(); i++) {
			Node n = es.item(i);
			if(n instanceof Element)
				processSingleFormElement(fps, (Element) n);

		}
		if(fps.getFileFieldFlag()) {
			ctx.debug("Cannot process form because file input handling not implemented");
			return;
		}
		ctx.debug("Processed form: "+ fps);
		submitNewForm(fps);

	}

	private void submitNewForm(FormProcessingState formData) {
		if(!uriFilter.isAllowed(formData.getTargetURI()))
			return;
			
		final IPathState ps = uriParser.processUri(formData.getTargetURI());
		if(formData.isPostMethod())
			ps.maybeAddPostParameters(formData.getParameters());
		else
			ps.maybeAddParameters(formData.getParameters());
	}

	private void processSingleFormElement(FormProcessingState fps, Element element) {
		if(element.getTagName().toLowerCase().equals("input"))
			processInputElement(fps, element);
		else
			processOtherFormElement(fps, element);
	}

	private void processInputElement(FormProcessingState fps, Element input) {
		final String name = decodeAttribute(input, "name");
		if(name == null)
			return;
		final String rawType = input.getAttribute("type");
		final String type = (rawType == null) ? ("text") : (rawType.toLowerCase());
		final String value = decodeAttribute(input, "value");
		if(type.equals("file")) {
			// XXX
			fps.setFileFieldFlag();
			return;
		} else if(type.equals("checkbox")) {
			fps.add(name, "on");
		} else if(type.equals("hidden") && value != null && !value.isEmpty()) {
			fps.add(name, value);
		} else if(type.equals("reset")) {
			// do nothing
		} else if(value != null && !value.isEmpty()) {
			fps.add(name, value);
		} else {
			fps.addGuessedValue(name);
		}

		if(type.equals("password"))
			fps.setPasswordFieldFlag();
	}

	private void processOtherFormElement(FormProcessingState fps, Element element) {
		final String tag = element.getTagName().toLowerCase();
		if(tag.equals("textarea") || tag.equals("select") || tag.equals("button")) {
			final String name = decodeAttribute(element, "name");
			if(name == null)
				return;
			final String value = decodeAttribute(element, "value");
			if(value == null || value.isEmpty())
				fps.addGuessedValue(name);
			else
				fps.add(name, value);
		}
	}

	private String decodeAttribute(Element e, String a) {
		final String v = e.getAttribute(a);
		if(v == null)
			return null;
		// XXX do html decode
		return v;
	}
}
