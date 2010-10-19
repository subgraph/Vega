package com.subgraph.vega.ui.scanner;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.IScannerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.subgraph.vega.ui.scanner"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker scannerFactoryTracker;
	private ServiceTracker pathFinderTracker;
	
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
		
		scannerFactoryTracker = new ServiceTracker(context, IScannerFactory.class.getName(), null);
		scannerFactoryTracker.open();
		
		pathFinderTracker = new ServiceTracker(context, IPathFinder.class.getName(), null);
		pathFinderTracker.open();
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
	
	public IScannerFactory getScannerFactory() {
		return (IScannerFactory) scannerFactoryTracker.getService();
	}
	
	public IPathFinder getPathFinder() {
		return (IPathFinder) pathFinderTracker.getService();
	}

}
