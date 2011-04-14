package com.subgraph.vega.api.paths;

import java.io.File;

public interface IPathFinder {
	File getVegaDirectory();
	File getWorkspaceDirectory();
	File getConfigFilePath();
	File getDataDirectory();
}
