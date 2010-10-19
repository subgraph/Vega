package org.w3c.dom.html2;

/**
 * Generic block container. See the DIV element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLDivElement extends HTMLElement {
    /**
     * Horizontal text alignment. See the align attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getAlign();
    /**
     * Horizontal text alignment. See the align attribute definition in HTML 
     * 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setAlign(String align);

}
