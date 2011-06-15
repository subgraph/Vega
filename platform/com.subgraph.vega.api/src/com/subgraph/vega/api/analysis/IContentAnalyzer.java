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
package com.subgraph.vega.api.analysis;

import java.util.List;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.modules.IResponseProcessingModule;

public interface IContentAnalyzer {
	IContentAnalyzerResult processResponse(IHttpResponse response);
	IContentAnalyzerResult processResponse(IHttpResponse response, boolean addToRequestLog, boolean scrapePage);
	void setResponseProcessingModules(List<IResponseProcessingModule> modules);
	void setAddLinksToModel(boolean flag);
	void setDefaultAddToRequestLog(boolean flag);
}
