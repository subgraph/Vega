package com.subgraph.vega.api.xml;

import org.w3c.dom.Document;

public interface IXmlRepository {
	Document getDocument(String path);
}
