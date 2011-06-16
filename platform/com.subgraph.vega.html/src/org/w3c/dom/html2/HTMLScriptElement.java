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
package org.w3c.dom.html2;

/**
 * Script statements. See the SCRIPT element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLScriptElement extends HTMLElement {
    /**
     * The script content of the element. 
     */
    public String getText();
    /**
     * The script content of the element. 
     */
    public void setText(String text);

    /**
     * Reserved for future use. 
     */
    public String getHtmlFor();
    /**
     * Reserved for future use. 
     */
    public void setHtmlFor(String htmlFor);

    /**
     * Reserved for future use. 
     */
    public String getEvent();
    /**
     * Reserved for future use. 
     */
    public void setEvent(String event);

    /**
     * The character encoding of the linked resource. See the charset 
     * attribute definition in HTML 4.01.
     */
    public String getCharset();
    /**
     * The character encoding of the linked resource. See the charset 
     * attribute definition in HTML 4.01.
     */
    public void setCharset(String charset);

    /**
     * Indicates that the user agent can defer processing of the script. See 
     * the defer attribute definition in HTML 4.01.
     */
    public boolean getDefer();
    /**
     * Indicates that the user agent can defer processing of the script. See 
     * the defer attribute definition in HTML 4.01.
     */
    public void setDefer(boolean defer);

    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating an external script. See the src attribute definition 
     * in HTML 4.01.
     */
    public String getSrc();
    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating an external script. See the src attribute definition 
     * in HTML 4.01.
     */
    public void setSrc(String src);

    /**
     * The content type of the script language. See the type attribute 
     * definition in HTML 4.01.
     */
    public String getType();
    /**
     * The content type of the script language. See the type attribute 
     * definition in HTML 4.01.
     */
    public void setType(String type);

}
