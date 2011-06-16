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
 * Table caption See the CAPTION element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLTableCaptionElement extends HTMLElement {
    /**
     * Caption alignment with respect to the table. See the align attribute 
     * definition in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getAlign();
    /**
     * Caption alignment with respect to the table. See the align attribute 
     * definition in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setAlign(String align);

}
