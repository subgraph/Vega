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
 * Parameters fed to the <code>OBJECT</code> element. See the PARAM element 
 * definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLParamElement extends HTMLElement {
    /**
     * The name of a run-time parameter. See the name attribute definition in 
     * HTML 4.01.
     */
    public String getName();
    /**
     * The name of a run-time parameter. See the name attribute definition in 
     * HTML 4.01.
     */
    public void setName(String name);

    /**
     * Content type for the <code>value</code> attribute when 
     * <code>valuetype</code> has the value "ref". See the type attribute 
     * definition in HTML 4.01.
     */
    public String getType();
    /**
     * Content type for the <code>value</code> attribute when 
     * <code>valuetype</code> has the value "ref". See the type attribute 
     * definition in HTML 4.01.
     */
    public void setType(String type);

    /**
     * The value of a run-time parameter. See the value attribute definition 
     * in HTML 4.01.
     */
    public String getValue();
    /**
     * The value of a run-time parameter. See the value attribute definition 
     * in HTML 4.01.
     */
    public void setValue(String value);

    /**
     * Information about the meaning of the <code>value</code> attribute 
     * value. See the valuetype attribute definition in HTML 4.01.
     */
    public String getValueType();
    /**
     * Information about the meaning of the <code>value</code> attribute 
     * value. See the valuetype attribute definition in HTML 4.01.
     */
    public void setValueType(String valueType);

}
