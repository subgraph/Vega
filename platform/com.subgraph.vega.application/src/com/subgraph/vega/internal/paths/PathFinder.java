package com.subgraph.vega.internal.paths;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import com.subgraph.vega.api.paths.IPathFinder;

public class PathFinder implements IPathFinder {

	private final Properties configProperties = new Properties();
	
	public File getConfigFilePath() {
		final File homeDirectory = new File(System.getProperty("user.home"));
		final File vegaDirectory = new File(homeDirectory, ".vega");
		return new File(vegaDirectory, "config");
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
		if(!configFile.exists() || !configFile.canRead())
			return;
		try {
			final Reader r = new FileReader(configFile);
			configProperties.clear();
			configProperties.load(r);
		} catch (IOException e) {
			
		}
	}
}
