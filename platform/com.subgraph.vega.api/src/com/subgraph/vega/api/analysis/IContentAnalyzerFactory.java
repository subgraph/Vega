package com.subgraph.vega.api.analysis;

import com.subgraph.vega.api.model.alerts.IScanInstance;

public interface IContentAnalyzerFactory {
	IContentAnalyzer createContentAnalyzer(IScanInstance scanInstance);
}
