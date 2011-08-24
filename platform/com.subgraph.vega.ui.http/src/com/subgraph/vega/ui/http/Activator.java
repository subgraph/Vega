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
package com.subgraph.vega.ui.http;

import java.util.ArrayList;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.http.proxy.IHttpProxyListenerConfig;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IHttpProxyTransactionManipulator;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.internal.ui.http.ProxyServiceTrackerCustomizer;
import com.subgraph.vega.internal.ui.http.ProxyStatusLineContribution;
import com.subgraph.vega.ui.http.preferencepage.IPreferenceConstants;
import com.subgraph.vega.ui.http.preferencepage.ProxyListenerPreferencePage;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.subgraph.vega.ui.http"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker<IModel, IModel> modelTracker;
	private ProxyServiceTrackerCustomizer proxyServiceTrackerCustomizer;
	private ServiceTracker<IHttpProxyService, IHttpProxyService> proxyServiceTracker;
	private ServiceTracker<IHttpRequestEngineFactory, IHttpRequestEngineFactory> httpRequestEngineFactoryServiceTracker;
	private ServiceTracker<IContentAnalyzerFactory, IContentAnalyzerFactory> contentAnalyzerFactoryTracker;
	private ServiceTracker<IScannerModuleRegistry, IScannerModuleRegistry> scannerModuleRegistryTracker;

	private ProxyStatusLineContribution statusLineContribution = new ProxyStatusLineContribution();
	
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
		
		proxyServiceTrackerCustomizer = new ProxyServiceTrackerCustomizer(context, statusLineContribution);
		proxyServiceTracker = new ServiceTracker<IHttpProxyService, IHttpProxyService>(context, IHttpProxyService.class.getName(), proxyServiceTrackerCustomizer);
		proxyServiceTracker.open();
		setProxyListenerAddresses();
		setProxyTransactionManipulator();
		
		httpRequestEngineFactoryServiceTracker = new ServiceTracker<IHttpRequestEngineFactory, IHttpRequestEngineFactory>(context, IHttpRequestEngineFactory.class.getName(), null);
		httpRequestEngineFactoryServiceTracker.open();

		contentAnalyzerFactoryTracker = new ServiceTracker<IContentAnalyzerFactory, IContentAnalyzerFactory>(context, IContentAnalyzerFactory.class.getName(), null);
		contentAnalyzerFactoryTracker.open();

		scannerModuleRegistryTracker = new ServiceTracker<IScannerModuleRegistry, IScannerModuleRegistry>(context, IScannerModuleRegistry.class.getName(), null);
		scannerModuleRegistryTracker.open();
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
	
	public IHttpProxyService getProxyService() {
		return proxyServiceTracker.getService();
	}
	
	public IHttpRequestEngineFactory getHttpRequestEngineFactoryService() {
		return httpRequestEngineFactoryServiceTracker.getService();
	}

	public IContentAnalyzerFactory getContentAnalyzerFactoryService() {
		return contentAnalyzerFactoryTracker.getService();
	}
	
	public IScannerModuleRegistry getScannerModuleRegistry() {
		return scannerModuleRegistryTracker.getService();
	}

	public ContributionItem getStatusLineContribution() {
		return statusLineContribution;
	}
	
	private void setProxyListenerAddresses() {
		updateProxyListenerAddresses();
		getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty() == IPreferenceConstants.P_PROXY_LISTENERS) {
					updateProxyListenerAddresses();
				}
			}
		});
	}

	private void updateProxyListenerAddresses() {
		final ArrayList<IHttpProxyListenerConfig> listenerList = new ArrayList<IHttpProxyListenerConfig>();
		final String prefListeners = getPreferenceStore().getString(IPreferenceConstants.P_PROXY_LISTENERS);
		ProxyListenerPreferencePage.parsePreferencesString(listenerList, prefListeners);
		final IHttpProxyService proxyService = getProxyService();
		proxyService.setListenerConfigs((IHttpProxyListenerConfig[]) listenerList.toArray(new IHttpProxyListenerConfig[0]));
	}
	
	private void setProxyTransactionManipulator() {
		final IHttpProxyTransactionManipulator manipulator = getProxyService().getTransactionManipulator();
		final IPreferenceStore preferenceStore = getPreferenceStore();
		manipulator.setUserAgent(preferenceStore.getString(IPreferenceConstants.P_USER_AGENT));
		manipulator.setUserAgentOverride(preferenceStore.getBoolean(IPreferenceConstants.P_USER_AGENT_OVERRIDE));
		manipulator.setBrowserCacheDisable(preferenceStore.getBoolean(IPreferenceConstants.P_DISABLE_BROWSER_CACHE));
		manipulator.setProxyCacheDisable(preferenceStore.getBoolean(IPreferenceConstants.P_DISABLE_PROXY_CACHE));
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				final IHttpProxyTransactionManipulator manipulator = getProxyService().getTransactionManipulator();
				final String property = event.getProperty();
				if (property == IPreferenceConstants.P_USER_AGENT) {
					manipulator.setUserAgent(preferenceStore.getString(IPreferenceConstants.P_USER_AGENT));
				} else if (property == IPreferenceConstants.P_USER_AGENT_OVERRIDE) {
					manipulator.setUserAgentOverride(preferenceStore.getBoolean(IPreferenceConstants.P_USER_AGENT_OVERRIDE));
				} else if (property == IPreferenceConstants.P_DISABLE_BROWSER_CACHE) {
					manipulator.setBrowserCacheDisable(preferenceStore.getBoolean(IPreferenceConstants.P_DISABLE_BROWSER_CACHE));
				} else if (property == IPreferenceConstants.P_DISABLE_PROXY_CACHE) {
					manipulator.setProxyCacheDisable(preferenceStore.getBoolean(IPreferenceConstants.P_DISABLE_PROXY_CACHE));
				}
			}
		});
	}

}
