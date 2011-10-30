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
package com.subgraph.vega.api.scanner;

import com.subgraph.vega.api.events.IEventHandler;

public interface IScanner {
	IScan createScan();
	void runDomTests();

	// TODO: remove these. we should be able to run more than one scan simultaneously
	void addLockStatusListener(IEventHandler listener);
	void removeLockStatusListener(IEventHandler listener);
	boolean lock(IScan scan);
	void unlock();
}
