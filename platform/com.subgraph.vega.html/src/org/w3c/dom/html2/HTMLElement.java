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

import org.w3c.dom.Element;

/**
 *  All HTML element interfaces derive from this class. Elements that only 
 * expose the HTML core attributes are represented by the base 
 * <code>HTMLElement</code> interface. These elements are as follows: 
 * special: SUB, SUP, SPAN, BDOfont: TT, I, B, U, S, STRIKE, BIG, SMALL
 * phrase: EM, STRONG, DFN, CODE, SAMP, KBD, VAR, CITE, ACRONYM, ABBRlist: 
 * DD, DTNOFRAMES, NOSCRIPTADDRESS, CENTERThe <code>style</code> attribute 
 * of an HTML element is accessible through the 
 * <code>ElementCSSInlineStyle</code> interface which is defined in the CSS 
 * module [<a href='http://www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113'>DOM Level 2 Style Sheets and CSS</a>]. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLElement extends Element {
    /**
     * The element's identifier. See the id attribute definition in HTML 4.01.
     */
    public String getId();
    /**
     * The element's identifier. See the id attribute definition in HTML 4.01.
     */
    public void setId(String id);

    /**
     * The element's advisory title. See the title attribute definition in 
     * HTML 4.01.
     */
    public String getTitle();
    /**
     * The element's advisory title. See the title attribute definition in 
     * HTML 4.01.
     */
    public void setTitle(String title);

    /**
     * Language code defined in RFC 1766. See the lang attribute definition in 
     * HTML 4.01.
     */
    public String getLang();
    /**
     * Language code defined in RFC 1766. See the lang attribute definition in 
     * HTML 4.01.
     */
    public void setLang(String lang);

    /**
     * Specifies the base direction of directionally neutral text and the 
     * directionality of tables. See the dir attribute definition in HTML 
     * 4.01.
     */
    public String getDir();
    /**
     * Specifies the base direction of directionally neutral text and the 
     * directionality of tables. See the dir attribute definition in HTML 
     * 4.01.
     */
    public void setDir(String dir);

    /**
     * The class attribute of the element. This attribute has been renamed due 
     * to conflicts with the "class" keyword exposed by many languages. See 
     * the class attribute definition in HTML 4.01.
     */
    public String getClassName();
    /**
     * The class attribute of the element. This attribute has been renamed due 
     * to conflicts with the "class" keyword exposed by many languages. See 
     * the class attribute definition in HTML 4.01.
     */
    public void setClassName(String className);

}
