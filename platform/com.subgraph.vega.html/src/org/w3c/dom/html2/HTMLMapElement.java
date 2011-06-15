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
 * Client-side image map. See the MAP element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLMapElement extends HTMLElement {
    /**
     * The list of areas defined for the image map. 
     */
    public HTMLCollection getAreas();

    /**
     * Names the map (for use with <code>usemap</code>). See the name 
     * attribute definition in HTML 4.01.
     */
    public String getName();
    /**
     * Names the map (for use with <code>usemap</code>). See the name 
     * attribute definition in HTML 4.01.
     */
    public void setName(String name);

}
