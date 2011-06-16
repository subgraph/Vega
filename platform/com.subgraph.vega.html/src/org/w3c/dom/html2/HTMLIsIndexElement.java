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
 * This element is used for single-line text input. See the ISINDEX element 
 * definition in HTML 4.01. This element is deprecated in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLIsIndexElement extends HTMLElement {
    /**
     * Returns the <code>FORM</code> element containing this control. Returns 
     * <code>null</code> if this control is not within the context of a 
     * form. 
     */
    public HTMLFormElement getForm();

    /**
     * The prompt message. See the prompt attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public String getPrompt();
    /**
     * The prompt message. See the prompt attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public void setPrompt(String prompt);

}
