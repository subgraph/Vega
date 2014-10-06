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
package com.subgraph.vega.internal.model;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.config.QueryEvaluationMode;
import com.db4o.diagnostic.DiagnosticToConsole;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.ta.TransparentPersistenceSupport;
import com.subgraph.vega.internal.model.alerts.ScanAlert;
import com.subgraph.vega.internal.model.requests.RequestLogRecord;
import com.subgraph.vega.internal.model.tags.Tag;

public class DatabaseConfigurationFactory {
	final private static boolean LAZY_EVALUATION = true;
	final private static boolean DIAGNOSTICS_ENABLED = false;
	final private static boolean DEBUG_OUTPUT_ENABLED = false;
	final private static int DEBUG_OUTPUT_LEVEL = 4;

	public EmbeddedConfiguration createDefaultConfiguration() {
		EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
		configuration.common().reflectWith(new JdkReflector(this.getClass().getClassLoader()));
		configuration.common().add(new TransparentPersistenceSupport());
		configuration.common().objectClass(RequestLogRecord.class).objectField("requestId").indexed(true);
		configuration.common().objectClass(RequestLogRecord.class).objectField("requestOrigin").indexed(true);
		configuration.common().objectClass(RequestLogRecord.class).objectField("hostname").indexed(true);
		configuration.common().objectClass(RequestLogRecord.class).objectField("requestMethod").indexed(true);
		configuration.common().objectClass(RequestLogRecord.class).objectField("responseCode").indexed(true);
		configuration.common().objectClass(RequestLogRecord.class).objectField("responseLength").indexed(true);
		configuration.common().objectClass(RequestLogRecord.class).objectField("tagList").indexed(true);
		configuration.common().objectClass(ScanAlert.class).objectField("key").indexed(true);
		configuration.common().objectClass(ScanAlert.class).objectField("resource").indexed(true);
		configuration.common().objectClass(ScanAlert.class).objectField("requestId").indexed(true);
		configuration.common().objectClass(Tag.class).objectField("name").indexed(true);

		configuration.common().weakReferences(true);
		
		if(DIAGNOSTICS_ENABLED) {
			configuration.common().diagnostic().addListener(new DiagnosticToConsole());
		}
		if(DEBUG_OUTPUT_ENABLED) {
			configuration.common().messageLevel(DEBUG_OUTPUT_LEVEL);
		}

		if(LAZY_EVALUATION) {
			configuration.common().queries().evaluationMode(QueryEvaluationMode.LAZY);
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
