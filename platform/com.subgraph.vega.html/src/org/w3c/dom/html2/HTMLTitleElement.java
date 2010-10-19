package org.w3c.dom.html2;

/**
 * The document title. See the TITLE element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLTitleElement extends HTMLElement {
    /**
     * The specified title as a string. 
     */
    public String getText();
    /**
     * The specified title as a string. 
     */
    public void setText(String text);

}
