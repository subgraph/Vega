package com.subgraph.vega.ui.http;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.subgraph.vega.api.analysis.IContentAnalyzerFactory;
import com.subgraph.vega.api.http.proxy.IHttpProxyService;
import com.subgraph.vega.api.http.proxy.IHttpProxyTransactionManipulator;
import com.subgraph.vega.api.http.requests.IHttpRequestEngineFactory;
import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;
import com.subgraph.vega.internal.ui.http.ProxyStatusLineContribution;
import com.subgraph.vega.ui.http.preferencepage.PreferenceConstants;

public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.subgraph.vega.ui.http"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker modelTracker;
	private ProxyServiceTrackerCustomizer proxyServiceTrackerCustomizer;
	private ServiceTracker proxyServiceTracker;
	private ServiceTracker httpRequestEngineFactoryServiceTracker;
	private ServiceTracker contentAnalyzerFactoryTracker;
	private ServiceTracker scannerModuleRegistryTracker;

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
		
		modelTracker = new ServiceTracker(context, IModel.class.getName(), null);
		modelTracker.open();
		
		proxyServiceTrackerCustomizer = new ProxyServiceTrackerCustomizer(context, statusLineContribution);
		proxyServiceTracker = new ServiceTracker(context, IHttpProxyService.class.getName(), proxyServiceTrackerCustomizer);
		proxyServiceTracker.open();
		setProxyTransactionManipulator();
		
		httpRequestEngineFactoryServiceTracker = new ServiceTracker(context, IHttpRequestEngineFactory.class.getName(), null);
		httpRequestEngineFactoryServiceTracker.open();

		contentAnalyzerFactoryTracker = new ServiceTracker(context, IContentAnalyzerFactory.class.getName(), null);
		contentAnalyzerFactoryTracker.open();

		scannerModuleRegistryTracker = new ServiceTracker(context, IScannerModuleRegistry.class.getName(), null);
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
		return (IModel) modelTracker.getService();
	}
	
	public IHttpProxyService getProxyService() {
		return (IHttpProxyService) proxyServiceTracker.getService();
	}
	
	public IHttpRequestEngineFactory getHttpRequestEngineFactoryService() {
		return (IHttpRequestEngineFactory) httpRequestEngineFactoryServiceTracker.getService();
	}

	public IContentAnalyzerFactory getContentAnalyzerFactoryService() {
		return (IContentAnalyzerFactory) contentAnalyzerFactoryTracker.getService();
	}
	
	public IScannerModuleRegistry getScannerModuleRegistry() {
		return (IScannerModuleRegistry) scannerModuleRegistryTracker.getService();
	}

	public ContributionItem getStatusLineContribution() {
		return statusLineContribution;
	}
	
	public void setStatusLineProxyRunning(int port) {
		statusLineContribution.setProxyRunning(port);
	}
	
	public void setStatusLineProxyStopped() {
		statusLineContribution.setProxyStopped();
	}

	private void setProxyTransactionManipulator() {
		final IHttpProxyTransactionManipulator manipulator = getProxyService().getTransactionManipulator();
		final IPreferenceStore preferenceStore = getDefault().getPreferenceStore();
		manipulator.setUserAgent(preferenceStore.getString(PreferenceConstants.P_USER_AGENT));
		manipulator.setUserAgentOverride(preferenceStore.getBoolean(PreferenceConstants.P_USER_AGENT_OVERRIDE));
		manipulator.setBrowserCacheDisable(preferenceStore.getBoolean(PreferenceConstants.P_DISABLE_BROWSER_CACHE));
		manipulator.setProxyCacheDisable(preferenceStore.getBoolean(PreferenceConstants.P_DISABLE_PROXY_CACHE));
		
		preferenceStore.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				final IHttpProxyTransactionManipulator manipulator = getProxyService().getTransactionManipulator();
				final String property = event.getProperty();
				if (property == PreferenceConstants.P_USER_AGENT) {
					manipulator.setUserAgent(preferenceStore.getString(PreferenceConstants.P_USER_AGENT));
				} else if (property == PreferenceConstants.P_USER_AGENT_OVERRIDE) {
					manipulator.setUserAgentOverride(preferenceStore.getBoolean(PreferenceConstants.P_USER_AGENT_OVERRIDE));
				} else if (property == PreferenceConstants.P_DISABLE_BROWSER_CACHE) {
					manipulator.setBrowserCacheDisable(preferenceStore.getBoolean(PreferenceConstants.P_DISABLE_BROWSER_CACHE));
				} else if (property == PreferenceConstants.P_DISABLE_PROXY_CACHE) {
					manipulator.setProxyCacheDisable(preferenceStore.getBoolean(PreferenceConstants.P_DISABLE_PROXY_CACHE));
				}
			}
		});
	}

}
