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
package com.subgraph.vega.api.vuge;

public interface IConstants {
	/**
	 * Major version number.
	 */
	static final int VERSION_MAJOR = 1;
	
	/**
	 * Minor version number.
	 */
	static final int VERSION_MINOR = 0;

	/**
	 * Version string.
	 */
	static final String VERSION_STRING = "1.0";
	
	/**
	 * Build number (injected by Ant/Hudson)
	 */
	static final int BUILD_NUMBER = 0x00000000;
}
