package com.subgraph.vega.export;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.subgraph.vega.api.model.IModel;
import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.xml.IXmlRepository;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.subgraph.vega.export"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	private ServiceTracker<IModel, IModel> modelTracker;
	private ServiceTracker<IXmlRepository, IXmlRepository> xmlRepositoryTracker;
	private ServiceTracker<IPathFinder, IPathFinder> pathFinderTracker;

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
		
		
	/*	modelTracker = new ServiceTracker<IModel, IModel>(context, IModel.class.getName(), null);
		System.out.println("Starting exporter");
		*/
		super.start(context);
		

		pathFinderTracker = new ServiceTracker<IPathFinder, IPathFinder>(context, IPathFinder.class.getName(), null);
		pathFinderTracker.open();

		modelTracker = new ServiceTracker<IModel, IModel>(context, IModel.class.getName(), null);
		modelTracker.open();

		xmlRepositoryTracker = new ServiceTracker<IXmlRepository, IXmlRepository>(context, IXmlRepository.class.getName(), null);
		xmlRepositoryTracker.open();
		
		plugin = this;
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

	public IPathFinder getPathFinder() {
		return pathFinderTracker.getService();
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
	
	public IXmlRepository getXmlRepository() {
		return xmlRepositoryTracker.getService();
	}
	
}
