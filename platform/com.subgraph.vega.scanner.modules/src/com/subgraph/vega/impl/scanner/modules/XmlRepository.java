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
package com.subgraph.vega.impl.scanner.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.subgraph.vega.api.paths.IPathFinder;
import com.subgraph.vega.api.xml.IXmlRepository;

public class XmlRepository implements IXmlRepository {
	private final Logger logger = Logger.getLogger("xml-repository");

	private IPathFinder pathFinder;
	private File xmlDirectory;
	private DocumentBuilderFactory documentBuilderFactory;
	
	private Map<String, Document> xmlCache = new HashMap<String, Document>();
	
	void activate() {
		final File dataDirectory = pathFinder.getDataDirectory();
		xmlDirectory = new File(dataDirectory, "xml");
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
	}
	
	@Override
	public synchronized Document getDocument(String path) {
		if(xmlCache.containsKey(path))
			return xmlCache.get(path);
		
		final Document document = loadXML(path);
		if(document != null) {
			xmlCache.put(path, document);
		}
		return document;
	}
	
	
	private Document loadXML(String path) {
		if(File.separatorChar != '/')
			path = path.replace('/', File.separatorChar);
		final File xmlFile = new File(xmlDirectory, path);
		try {
			final InputStream input = new FileInputStream(xmlFile);
			final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			return builder.parse(input);
		} catch (FileNotFoundException e) {
			logger.warning("Could not find XML file: "+ xmlFile);
		} catch (ParserConfigurationException e) {
			logger.log(Level.WARNING, "Error setting up XML parser:" + e.getMessage(), e);
		} catch (SAXException e) {
			logger.log(Level.WARNING, "Error parsing XML document: "+ xmlFile + " : "+ e.getMessage(), e);
		} catch (IOException e) {
			logger.log(Level.WARNING, "I/O error reading XML file: "+ xmlFile + " : "+ e.getMessage(), e);
		}
		return null;
	}
	
	protected void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}
	
	protected void unsetPathFinder(IPathFinder pathFinder) {
		this.pathFinder = null;
	}
}
