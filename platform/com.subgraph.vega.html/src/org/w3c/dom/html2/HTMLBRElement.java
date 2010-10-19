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
