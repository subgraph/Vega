package com.subgraph.vega.api.analysis;

public interface IContentAnalyzerFactory {
	IContentAnalyzer createContentAnalyzer(long scanId);
}
