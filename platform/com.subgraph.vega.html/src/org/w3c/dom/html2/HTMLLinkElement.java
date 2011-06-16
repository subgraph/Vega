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
 * The <code>LINK</code> element specifies a link to an external resource, and 
 * defines this document's relationship to that resource (or vice versa). 
 * See the LINK element definition in HTML 4.01 (see also the 
 * <code>LinkStyle</code> interface in the StyleSheet module [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>DOM Level 2 Style Sheets and CSS</a>]).
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLLinkElement extends HTMLElement {
    /**
     * Enables/disables the link. This is currently only used for style sheet 
     * links, and may be used to activate or deactivate style sheets. 
     */
    public boolean getDisabled();
    /**
     * Enables/disables the link. This is currently only used for style sheet 
     * links, and may be used to activate or deactivate style sheets. 
     */
    public void setDisabled(boolean disabled);

    /**
     * The character encoding of the resource being linked to. See the charset 
     * attribute definition in HTML 4.01.
     */
    public String getCharset();
    /**
     * The character encoding of the resource being linked to. See the charset 
     * attribute definition in HTML 4.01.
     */
    public void setCharset(String charset);

    /**
     * The URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] of the linked resource. See the href attribute definition in 
     * HTML 4.01.
     */
    public String getHref();
    /**
     * The URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] of the linked resource. See the href attribute definition in 
     * HTML 4.01.
     */
    public void setHref(String href);

    /**
     * Language code of the linked resource. See the hreflang attribute 
     * definition in HTML 4.01.
     */
    public String getHreflang();
    /**
     * Language code of the linked resource. See the hreflang attribute 
     * definition in HTML 4.01.
     */
    public void setHreflang(String hreflang);

    /**
     * Designed for use with one or more target media. See the media attribute 
     * definition in HTML 4.01.
     */
    public String getMedia();
    /**
     * Designed for use with one or more target media. See the media attribute 
     * definition in HTML 4.01.
     */
    public void setMedia(String media);

    /**
     * Forward link type. See the rel attribute definition in HTML 4.01.
     */
    public String getRel();
    /**
     * Forward link type. See the rel attribute definition in HTML 4.01.
     */
    public void setRel(String rel);

    /**
     * Reverse link type. See the rev attribute definition in HTML 4.01.
     */
    public String getRev();
    /**
     * Reverse link type. See the rev attribute definition in HTML 4.01.
     */
    public void setRev(String rev);

    /**
     * Frame to render the resource in. See the target attribute definition in 
     * HTML 4.01.
     */
    public String getTarget();
    /**
     * Frame to render the resource in. See the target attribute definition in 
     * HTML 4.01.
     */
    public void setTarget(String target);

    /**
     * Advisory content type. See the type attribute definition in HTML 4.01.
     */
    public String getType();
    /**
     * Advisory content type. See the type attribute definition in HTML 4.01.
     */
    public void setType(String type);

}
