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
 * Force a line break. See the BR element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLBRElement extends HTMLElement {
    /**
     * Control flow of text around floats. See the clear attribute definition 
     * in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getClear();
    /**
     * Control flow of text around floats. See the clear attribute definition 
     * in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setClear(String clear);

}
