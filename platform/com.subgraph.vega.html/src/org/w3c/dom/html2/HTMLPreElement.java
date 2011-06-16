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
 * Preformatted text. See the PRE element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLPreElement extends HTMLElement {
    /**
     * Fixed width for content. See the width attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public int getWidth();
    /**
     * Fixed width for content. See the width attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setWidth(int width);

}
