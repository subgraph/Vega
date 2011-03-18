package com.subgraph.vega.api.analysis;

import java.net.URI;
import java.util.List;

public interface IContentAnalyzerResult {
	List<URI> getDiscoveredURIs();
	MimeType getDeclaredMimeType();
	MimeType getSniffedMimeType();
}
