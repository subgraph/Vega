package com.subgraph.vega.internal.paths;

import java.io.File;

import com.subgraph.vega.api.paths.IPathFinder;

public class PathFinder implements IPathFinder {

	
	@Override
	public File getDataDirectory() {
		if(!isRunningInEclipse())
			throw new UnsupportedOperationException("Path finder not implemented for launching Vega outside of Eclipse.");
		
		return getDataDirectoryForEclipseLaunch();
	}
	
	private boolean isRunningInEclipse() {
		return System.getProperty("osgi.dev") != null;
	}
	
	private File getDataDirectoryForEclipseLaunch() {
		final String uglyHack = System.getProperty("osgi.splashPath");
		final String splashPath = (uglyHack.startsWith("file:")) ? (uglyHack.substring(5)) : (uglyHack);
		final File splashPathFile = new File(splashPath);
		return splashPathFile.getParentFile().getParentFile();		
	}

}
