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
package com.subgraph.vega.internal.html.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DocumentImpl extends NodeImpl implements Document {
	
	private final static DOMImplementation implementation = new DomImplementationImpl();
	
	protected final org.jsoup.nodes.Document jsoupDocument;
	private Element cachedOwnerDocument;
	
	protected DocumentImpl(org.jsoup.nodes.Document jsoupDocument) {
		super(jsoupDocument, null);
		this.jsoupDocument = jsoupDocument;
	}
	
	public String getNodeName() {
		return "#document";
	}

	@Override
	public short getNodeType() {
		return Node.DOCUMENT_NODE;
	}
	
	@Override
	public DocumentType getDoctype() {
		throw createNoLevel3SupportException();
	}

	@Override
	public DOMImplementation getImplementation() {
		return implementation;
	}

	@Override
	public Element getDocumentElement() {
		if(cachedOwnerDocument != null)
			return cachedOwnerDocument;
		
		org.jsoup.nodes.Element htmlElement = jsoupDocument.select("html").first();
		if(htmlElement == null)
			return null;
		
		cachedOwnerDocument =  HTMLElementImpl.create(jsoupDocument.select("html").first(), getOwnerDocument());
		return cachedOwnerDocument;
	}

	@Override
	public Element createElement(String tagName) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public DocumentFragment createDocumentFragment() {
		throw createReadOnlyException();
	}

	@Override
	public Text createTextNode(String data) {
		throw createReadOnlyException();
	}

	@Override
	public Comment createComment(String data) {
		throw createReadOnlyException();
	}

	@Override
	public CDATASection createCDATASection(String data) throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public ProcessingInstruction createProcessingInstruction(String target,
			String data) throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public Attr createAttribute(String name) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public EntityReference createEntityReference(String name)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public NodeList getElementsByTagName(String tagname) {
		Element e = getDocumentElement();
		if(e == null)
			return NodeListImpl.emptyList;
		else
			return e.getElementsByTagName(tagname);
	}

	@Override
	public Node importNode(Node importedNode, boolean deep) throws DOMException {
		throw createReadOnlyException();
	}

	@Override
	public Element createElementNS(String namespaceURI, String qualifiedName)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public Attr createAttributeNS(String namespaceURI, String qualifiedName)
			throws DOMException {
		throw createNoXMLSupportException();
	}

	@Override
	public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
		throw createNoXMLSupportException();
	}

	@Override
	public Element getElementById(String elementId) {
		Element e = getDocumentElement();
		if(e == null)
			return null;
		else {
			org.jsoup.nodes.Element jsoupElement = jsoupDocument.getElementById(elementId);
			if(jsoupElement == null)
				return null;
			return HTMLElementImpl.create(jsoupElement, this);
		}
	}

	@Override
	public String getInputEncoding() {
		throw createNoLevel3SupportException();
	}

	@Override
	public String getXmlEncoding() {
		throw createNoLevel3SupportException();
	}

	@Override
	public boolean getXmlStandalone() {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		throw createNoLevel3SupportException();		
	}

	@Override
	public String getXmlVersion() {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setXmlVersion(String xmlVersion) throws DOMException {
		throw createNoLevel3SupportException();		
	}

	@Override
	public boolean getStrictErrorChecking() {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setStrictErrorChecking(boolean strictErrorChecking) {
		throw createNoLevel3SupportException();		
	}

	@Override
	public String getDocumentURI() {
		throw createNoLevel3SupportException();
	}

	@Override
	public void setDocumentURI(String documentURI) {
		throw createNoLevel3SupportException();		
	}

	@Override
	public Node adoptNode(Node source) throws DOMException {
		throw createNoLevel3SupportException();
	}

	@Override
	public DOMConfiguration getDomConfig() {
		throw createNoLevel3SupportException();
	}

	@Override
	public void normalizeDocument() {
		throw createNoLevel3SupportException();		
	}

	@Override
	public Node renameNode(Node n, String namespaceURI, String qualifiedName)
			throws DOMException {
		throw createNoLevel3SupportException();
	}
}
