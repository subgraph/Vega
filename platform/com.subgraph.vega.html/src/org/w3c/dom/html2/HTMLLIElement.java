package org.w3c.dom.html2;

/**
 * List item. See the LI element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLLIElement extends HTMLElement {
    /**
     * List item bullet style. See the type attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public String getType();
    /**
     * List item bullet style. See the type attribute definition in HTML 4.01. 
     * This attribute is deprecated in HTML 4.01.
     */
    public void setType(String type);

    /**
     * Reset sequence number when used in <code>OL</code>. See the value 
     * attribute definition in HTML 4.01. This attribute is deprecated in 
     * HTML 4.01.
     */
    public int getValue();
    /**
     * Reset sequence number when used in <code>OL</code>. See the value 
     * attribute definition in HTML 4.01. This attribute is deprecated in 
     * HTML 4.01.
     */
    public void setValue(int value);

}
