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
 * Ordered list. See the OL element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLOListElement extends HTMLElement {
    /**
     * Reduce spacing between list items. See the compact attribute definition 
     * in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public boolean getCompact();
    /**
     * Reduce spacing between list items. See the compact attribute definition 
     * in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setCompact(boolean compact);

    /**
     * Starting sequence number. See the start attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public int getStart();
    /**
     * Starting sequence number. See the start attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setStart(int start);

    /**
     * Numbering style. See the type attribute definition in HTML 4.01. This 
     * attribute is deprecated in HTML 4.01.
     */
    public String getType();
    /**
     * Numbering style. See the type attribute definition in HTML 4.01. This 
     * attribute is deprecated in HTML 4.01.
     */
    public void setType(String type);

}
