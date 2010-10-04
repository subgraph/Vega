package com.subgraph.vega.ui.scanner.info;

import java.net.URL;

import org.osgi.framework.Bundle;

import com.subgraph.vega.ui.scanner.Activator;

import freemarker.cache.URLTemplateLoader;

public class TemplateLoader extends URLTemplateLoader {
	@Override
	protected URL getURL(String template) {
		final Bundle bundle = Activator.getDefault().getBundle();
		return bundle.getEntry("/templates/"+ template);
	}

}
