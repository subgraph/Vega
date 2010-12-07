package com.subgraph.vega.internal.model;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.ta.TransparentPersistenceSupport;

public class DatabaseConfigurationFactory {
	final private static boolean DIAGNOSTICS_ENABLED = false;
	final private static boolean DEBUG_OUTPUT_ENABLED = false;
	final private static int DEBUG_OUTPUT_LEVEL = 4;

	public EmbeddedConfiguration createDefaultConfiguration() {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.common().reflectWith(new JdkReflector(this.getClass().getClassLoader()));
		configuration.common().add(new TransparentPersistenceSupport());
		if(DIAGNOSTICS_ENABLED) {
			configuration.common().diagnostic().addListener(new DiagnosticToConsole());
		}
		if(DEBUG_OUTPUT_ENABLED) {
			configuration.common().messageLevel(DEBUG_OUTPUT_LEVEL);
		}
		
		return configuration;
		
	}
	
	public ObjectContainer openContainer(String path) {
		EmbeddedConfiguration config = createDefaultConfiguration();
		return Db4oEmbedded.openFile(config, path);
	}
	
	public ObjectContainer openContainer(EmbeddedConfiguration config, String path) {
		return Db4oEmbedded.openFile(config, path);
	}

}
