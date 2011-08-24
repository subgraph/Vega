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
package com.subgraph.vega.application;

import org.apache.http.HttpHost;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.subgraph.vega.api.console.IConsole;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.application.preferences.IPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.subgraph.vega.application"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker<IModel, IModel> modelTracker;
	private ServiceTracker<IConsole, IConsole> consoleTracker;
	private ServiceTracker<IPathFinder, IPathFinder> pathFinderTracker;
	private ServiceTracker<IHttpRequestEngineFactory, IHttpRequestEngineFactory> httpRequestEngineFactoryServiceTracker;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		modelTracker = new ServiceTracker<IModel, IModel>(context, IModel.class.getName(), null);
		modelTracker.open();
		
		consoleTracker = new ServiceTracker<IConsole, IConsole>(context, IConsole.class.getName(), null);
		consoleTracker.open();
		
		pathFinderTracker = new ServiceTracker<IPathFinder, IPathFinder>(context, IPathFinder.class.getName(), null);
		pathFinderTracker.open();

		httpRequestEngineFactoryServiceTracker = new ServiceTracker<IHttpRequestEngineFactory, IHttpRequestEngineFactory>(context, IHttpRequestEngineFactory.class.getName(), null);
		httpRequestEngineFactoryServiceTracker.open();

		getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				configureSocks();
				configureHttpProxy();
			}
		});
		configureSocks();
		configureHttpProxy();
	}

	private void configureSocks() {
		final IPreferenceStore store = getPreferenceStore();
		if(!store.getBoolean(IPreferenceConstants.P_SOCKS_ENABLED)) {
			System.getProperties().remove("socksProxyHost");
			System.getProperties().remove("socksProxyPort");
			System.getProperties().remove("socksEnabled");
			return;
		}
		
		System.setProperty("socksProxyHost", store.getString(IPreferenceConstants.P_SOCKS_ADDRESS));
		System.setProperty("socksProxyPort", store.getString(IPreferenceConstants.P_SOCKS_PORT));
		System.setProperty("socksEnabled", "true");
	}
	
	private void configureHttpProxy() {
		final IPreferenceStore store = getPreferenceStore();
		final IHttpRequestEngineFactory requestEngineFactory = getHttpRequestEngineFactoryService();

		if (store.getBoolean(IPreferenceConstants.P_PROXY_ENABLED)) {
			final String proxyAddress = store.getString(IPreferenceConstants.P_PROXY_ADDRESS);
			final Integer proxyPort = store.getInt(IPreferenceConstants.P_PROXY_PORT);
			requestEngineFactory.setProxy(new HttpHost(proxyAddress, proxyPort));
		} else {
			requestEngineFactory.setProxy(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public IModel getModel() {
		return modelTracker.getService();
	}
	
	public IConsole getConsole() {
		return consoleTracker.getService();
	}
	
	public IPathFinder getPathFinder() {
		return pathFinderTracker.getService();
	}

	public IHttpRequestEngineFactory getHttpRequestEngineFactoryService() {
		return httpRequestEngineFactoryServiceTracker.getService();
	}

}
