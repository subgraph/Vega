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
 * A selectable choice. See the OPTION element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLOptionElement extends HTMLElement {
    /**
     * Returns the <code>FORM</code> element containing this control. Returns 
     * <code>null</code> if this control is not within the context of a 
     * form. 
     */
    public HTMLFormElement getForm();

    /**
     * Represents the value of the HTML selected attribute. The value of this 
     * attribute does not change if the state of the corresponding form 
     * control, in an interactive user agent, changes. See the selected 
     * attribute definition in HTML 4.01.
     * @version DOM Level 2
     */
    public boolean getDefaultSelected();
    /**
     * Represents the value of the HTML selected attribute. The value of this 
     * attribute does not change if the state of the corresponding form 
     * control, in an interactive user agent, changes. See the selected 
     * attribute definition in HTML 4.01.
     * @version DOM Level 2
     */
    public void setDefaultSelected(boolean defaultSelected);

    /**
     * The text contained within the option element. 
     */
    public String getText();

    /**
     * The index of this <code>OPTION</code> in its parent <code>SELECT</code>
     * , starting from 0.
     * @version DOM Level 2
     */
    public int getIndex();

    /**
     * The control is unavailable in this context. See the disabled attribute 
     * definition in HTML 4.01.
     */
    public boolean getDisabled();
    /**
     * The control is unavailable in this context. See the disabled attribute 
     * definition in HTML 4.01.
     */
    public void setDisabled(boolean disabled);

    /**
     * Option label for use in hierarchical menus. See the label attribute 
     * definition in HTML 4.01.
     */
    public String getLabel();
    /**
     * Option label for use in hierarchical menus. See the label attribute 
     * definition in HTML 4.01.
     */
    public void setLabel(String label);

    /**
     * Represents the current state of the corresponding form control, in an 
     * interactive user agent. Changing this attribute changes the state of 
     * the form control, but does not change the value of the HTML selected 
     * attribute of the element.
     */
    public boolean getSelected();
    /**
     * Represents the current state of the corresponding form control, in an 
     * interactive user agent. Changing this attribute changes the state of 
     * the form control, but does not change the value of the HTML selected 
     * attribute of the element.
     */
    public void setSelected(boolean selected);

    /**
     * The current form control value. See the value attribute definition in 
     * HTML 4.01.
     */
    public String getValue();
    /**
     * The current form control value. See the value attribute definition in 
     * HTML 4.01.
     */
    public void setValue(String value);

}
