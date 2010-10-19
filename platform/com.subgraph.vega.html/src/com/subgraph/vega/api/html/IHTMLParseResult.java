package com.subgraph.vega.api.html;

import org.jsoup.nodes.Document;
import org.w3c.dom.html2.HTMLDocument;

public interface IHTMLParseResult {
	Document getJsoupDocument();
	HTMLDocument getDOMDocument();

}
