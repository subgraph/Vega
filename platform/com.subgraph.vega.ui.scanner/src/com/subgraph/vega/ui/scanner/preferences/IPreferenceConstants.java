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
package com.subgraph.vega.ui.scanner.preferences;

public interface IPreferenceConstants {
	public static final String P_USER_AGENT = "UserAgent";
	public static final String P_MAX_SCAN_DESCENDANTS = "MaxScanDescendants";
	public static final String P_MAX_SCAN_CHILDREN = "MaxScanChildren";
	public static final String P_MAX_SCAN_DEPTH = "MaxScanDepth";
	public static final String P_MAX_SCAN_DUPLICATE_PATHS = "MaxScanDuplicatePaths";
	public static final String P_MAX_ALERT_STRING = "MaxAlertString";
	public static final String P_MAX_REQUESTS_PER_SECOND = "MaxRequestsPerSecond";
	public static final String P_MAX_RESPONSE_LENGTH = "MaxResponseLength";

	public static final String P_LOG_ALL_REQUESTS = "LogAllRequests";
	public static final String P_DISPLAY_DEBUG_OUTPUT = "DisplayDebugOutput";
}
