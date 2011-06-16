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
 * The HTML document body. This element is always present in the DOM API, even 
 * if the tags are not present in the source document. See the BODY element 
 * definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLBodyElement extends HTMLElement {
    /**
     * Color of active links (after mouse-button down, but before mouse-button 
     * up). See the alink attribute definition in HTML 4.01. This attribute 
     * is deprecated in HTML 4.01.
     */
    public String getALink();
    /**
     * Color of active links (after mouse-button down, but before mouse-button 
     * up). See the alink attribute definition in HTML 4.01. This attribute 
     * is deprecated in HTML 4.01.
     */
    public void setALink(String aLink);

    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] of the background texture tile image. See the background attribute 
     * definition in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getBackground();
    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] of the background texture tile image. See the background attribute 
     * definition in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setBackground(String background);

    /**
     * Document background color. See the bgcolor attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getBgColor();
    /**
     * Document background color. See the bgcolor attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setBgColor(String bgColor);

    /**
     * Color of links that are not active and unvisited. See the link 
     * attribute definition in HTML 4.01. This attribute is deprecated in 
     * HTML 4.01.
     */
    public String getLink();
    /**
     * Color of links that are not active and unvisited. See the link 
     * attribute definition in HTML 4.01. This attribute is deprecated in 
     * HTML 4.01.
     */
    public void setLink(String link);

    /**
     * Document text color. See the text attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public String getText();
    /**
     * Document text color. See the text attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public void setText(String text);

    /**
     * Color of links that have been visited by the user. See the vlink 
     * attribute definition in HTML 4.01. This attribute is deprecated in 
     * HTML 4.01.
     */
    public String getVLink();
    /**
     * Color of links that have been visited by the user. See the vlink 
     * attribute definition in HTML 4.01. This attribute is deprecated in 
     * HTML 4.01.
     */
    public void setVLink(String vLink);

}
