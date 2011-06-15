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
 * Base font. See the BASEFONT element definition in HTML 4.01. This element 
 * is deprecated in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLBaseFontElement extends HTMLElement {
    /**
     * Font color. See the color attribute definition in HTML 4.01. This 
     * attribute is deprecated in HTML 4.01.
     */
    public String getColor();
    /**
     * Font color. See the color attribute definition in HTML 4.01. This 
     * attribute is deprecated in HTML 4.01.
     */
    public void setColor(String color);

    /**
     * Font face identifier. See the face attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public String getFace();
    /**
     * Font face identifier. See the face attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public void setFace(String face);

    /**
     * Computed font size. See the size attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     * @version DOM Level 2
     */
    public int getSize();
    /**
     * Computed font size. See the size attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     * @version DOM Level 2
     */
    public void setSize(int size);

}
