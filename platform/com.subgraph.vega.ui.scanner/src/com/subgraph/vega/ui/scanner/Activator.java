package com.subgraph.vega.ui.scanner;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.scanner.IScanner;
import com.subgraph.vega.api.xml.IXmlRepository;
import com.subgraph.vega.api.scanner.modules.IScannerModuleRegistry;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.subgraph.vega.ui.scanner"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker scannerTracker;
	private ServiceTracker pathFinderTracker;
	private ServiceTracker modelTracker;
	private ServiceTracker xmlRepositoryTracker;
	private ServiceTracker moduleRegistryTracker;

	private ScanExceptionNotifier exceptionNotifier;
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

		scannerTracker = new ServiceTracker(context, IScanner.class.getName(), null);
		scannerTracker.open();

		pathFinderTracker = new ServiceTracker(context, IPathFinder.class.getName(), null);
		pathFinderTracker.open();

		modelTracker = new ServiceTracker(context, IModel.class.getName(), null);
		modelTracker.open();

		xmlRepositoryTracker = new ServiceTracker(context, IXmlRepository.class.getName(), null);
		xmlRepositoryTracker.open();

		moduleRegistryTracker = new ServiceTracker(context, IScannerModuleRegistry.class.getName(), null);
		moduleRegistryTracker.open();
		
		exceptionNotifier = new ScanExceptionNotifier();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		exceptionNotifier.stop();
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
	
	public IScanner getScanner() {
		return (IScanner) scannerTracker.getService();
	}
	
	public IPathFinder getPathFinder() {
		return (IPathFinder) pathFinderTracker.getService();
	}
	
	public IModel getModel() {
		return (IModel) modelTracker.getService();
	}
	
	public IXmlRepository getXmlRepository() {
		return (IXmlRepository) xmlRepositoryTracker.getService();
	}
	
	public IScannerModuleRegistry getIScannerModuleRegistry() {
		return (IScannerModuleRegistry) moduleRegistryTracker.getService();
	}
}
