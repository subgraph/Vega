package com.subgraph.vega.internal.paths;

import java.io.File;

import com.subgraph.vega.api.paths.IPathFinder;

public class PathFinder implements IPathFinder {

	
	@Override
	public File getDataDirectory() {
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
}
