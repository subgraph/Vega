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
 * This contains generic meta-information about the document. See the META 
 * element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLMetaElement extends HTMLElement {
    /**
     * Associated information. See the content attribute definition in HTML 
     * 4.01.
     */
    public String getContent();
    /**
     * Associated information. See the content attribute definition in HTML 
     * 4.01.
     */
    public void setContent(String content);

    /**
     * HTTP response header name [<a href='http://www.ietf.org/rfc/rfc2616.txt'>IETF RFC 2616</a>]. See the http-equiv attribute definition in 
     * HTML 4.01.
     */
    public String getHttpEquiv();
    /**
     * HTTP response header name [<a href='http://www.ietf.org/rfc/rfc2616.txt'>IETF RFC 2616</a>]. See the http-equiv attribute definition in 
     * HTML 4.01.
     */
    public void setHttpEquiv(String httpEquiv);

    /**
     * Meta information name. See the name attribute definition in HTML 4.01.
     */
    public String getName();
    /**
     * Meta information name. See the name attribute definition in HTML 4.01.
     */
    public void setName(String name);

    /**
     * Select form of content. See the scheme attribute definition in HTML 
     * 4.01.
     */
    public String getScheme();
    /**
     * Select form of content. See the scheme attribute definition in HTML 
     * 4.01.
     */
    public void setScheme(String scheme);

}
