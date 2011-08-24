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
	
	private ServiceTracker<IScanner, IScanner> scannerTracker;
	private ServiceTracker<IPathFinder, IPathFinder> pathFinderTracker;
	private ServiceTracker<IModel, IModel> modelTracker;
	private ServiceTracker<IXmlRepository, IXmlRepository> xmlRepositoryTracker;
	private ServiceTracker<IScannerModuleRegistry, IScannerModuleRegistry> moduleRegistryTracker;

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

		scannerTracker = new ServiceTracker<IScanner, IScanner>(context, IScanner.class.getName(), null);
		scannerTracker.open();

		pathFinderTracker = new ServiceTracker<IPathFinder, IPathFinder>(context, IPathFinder.class.getName(), null);
		pathFinderTracker.open();

		modelTracker = new ServiceTracker<IModel, IModel>(context, IModel.class.getName(), null);
		modelTracker.open();

		xmlRepositoryTracker = new ServiceTracker<IXmlRepository, IXmlRepository>(context, IXmlRepository.class.getName(), null);
		xmlRepositoryTracker.open();

		moduleRegistryTracker = new ServiceTracker<IScannerModuleRegistry, IScannerModuleRegistry>(context, IScannerModuleRegistry.class.getName(), null);
		moduleRegistryTracker.open();
		
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
	
	public IScanner getScanner() {
		return scannerTracker.getService();
	}
	
	public IPathFinder getPathFinder() {
		return pathFinderTracker.getService();
	}
	
	public IModel getModel() {
		return modelTracker.getService();
	}
	
	public IXmlRepository getXmlRepository() {
		return xmlRepositoryTracker.getService();
	}
	
	public IScannerModuleRegistry getIScannerModuleRegistry() {
		return moduleRegistryTracker.getService();
	}
}
