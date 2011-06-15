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
 *  Style information. See the STYLE element definition in HTML 4.01, the CSS 
 * module [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>DOM Level 2 Style Sheets and CSS</a>] and the <code>LinkStyle</code> interface in the StyleSheets 
 * module [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>DOM Level 2 Style Sheets and CSS</a>]. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLStyleElement extends HTMLElement {
    /**
     * Enables/disables the style sheet. 
     */
    public boolean getDisabled();
    /**
     * Enables/disables the style sheet. 
     */
    public void setDisabled(boolean disabled);

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
     * The content type of the style sheet language. See the type attribute 
     * definition in HTML 4.01.
     */
    public String getType();
    /**
     * The content type of the style sheet language. See the type attribute 
     * definition in HTML 4.01.
     */
    public void setType(String type);

}
