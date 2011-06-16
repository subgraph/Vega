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
package com.subgraph.vega.internal.paths;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.subgraph.vega.api.paths.IPathFinder;

public class PathFinder implements IPathFinder {
	private final Logger logger = Logger.getLogger("paths");
	private final Properties configProperties = new Properties();
	
	@Override
	public File getVegaDirectory() {
		final File homeDirectory = new File(System.getProperty("user.home"));
		return new File(homeDirectory, ".vega");
	}

	@Override
	public File getWorkspaceDirectory() {
		return new File(getVegaDirectory(), "workspaces");
	}
	
	@Override
	public File getConfigFilePath() {
		return new File(getVegaDirectory(), "config");
	}
	
	@Override
	public File getDataDirectory() {
		final File dataDirectoryFromConfig = getDataDirectoryFromConfig();
		if(dataDirectoryFromConfig != null)
			return dataDirectoryFromConfig;
		
		if(isRunningInEclipse())
			return getDataDirectoryForEclipseLaunch();
		else
			return getInstallDataDirectory();
	}
	
	private boolean isRunningInEclipse() {
		return System.getProperty("osgi.dev") != null;
	}
	
	private File getDataDirectoryForEclipseLaunch() {
		final String uglyHack = System.getProperty("osgi.splashPath");
		final File splashPathFile = new File(fileURLTrim(uglyHack));
		return splashPathFile.getParentFile().getParentFile();		
	}

	private File getInstallDataDirectory() {
		final String install = System.getProperty("osgi.install.area");
		return new File(fileURLTrim(install));
	}
	
	private String fileURLTrim(String fileURL) {
		return (fileURL.startsWith("file:")) ? (fileURL.substring(5)) : fileURL;
	}
	
	private File getDataDirectoryFromConfig() {
		loadConfigProperties();
		String pathProp = configProperties.getProperty("vega.scanner.datapath");
		if(pathProp != null) {
			final File dataDir = new File(pathProp);
			if(dataDir.exists() && dataDir.isDirectory())
				return dataDir;
		}
		return null;
	}
				
	
	private void loadConfigProperties() {
		final File configFile = getConfigFilePath();
		if(!configFile.exists() || !configFile.canRead()) {
			return;
		}
		Reader r = null;
		try {
			r = new FileReader(configFile);
			configProperties.clear();
			configProperties.load(r);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error opening config file: "+ e.getMessage(), e);
		} finally {
			if(r != null) {
				try {
					r.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, "Error closing config file: "+ e.getMessage(), e);
				}
			}
		}
	}
}
