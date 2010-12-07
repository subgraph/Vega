package com.subgraph.vega.api.model;

import java.io.File;

/**
 * A workspace entry describes a single workspace on the file system.  
 */
public interface IWorkspaceEntry {
	String getName();
	int getIndex();
	boolean isAutostart();
	File getPath();
}
