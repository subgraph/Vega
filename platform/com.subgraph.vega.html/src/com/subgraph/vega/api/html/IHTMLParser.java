package com.subgraph.vega.api.html;

import java.net.URI;

public interface IHTMLParser {
	IHTMLParseResult parseString(String html, URI baseURI);
}
