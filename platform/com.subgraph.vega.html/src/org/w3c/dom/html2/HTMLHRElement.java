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
 * Create a horizontal rule. See the HR element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLHRElement extends HTMLElement {
    /**
     * Align the rule on the page. See the align attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getAlign();
    /**
     * Align the rule on the page. See the align attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setAlign(String align);

    /**
     * Indicates to the user agent that there should be no shading in the 
     * rendering of this element. See the noshade attribute definition in 
     * HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public boolean getNoShade();
    /**
     * Indicates to the user agent that there should be no shading in the 
     * rendering of this element. See the noshade attribute definition in 
     * HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setNoShade(boolean noShade);

    /**
     * The height of the rule. See the size attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public String getSize();
    /**
     * The height of the rule. See the size attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public void setSize(String size);

    /**
     * The width of the rule. See the width attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public String getWidth();
    /**
     * The width of the rule. See the width attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public void setWidth(String width);

}
