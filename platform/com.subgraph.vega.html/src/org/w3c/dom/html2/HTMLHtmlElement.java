package org.w3c.dom.html2;

/**
 * Root of an HTML document. See the HTML element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLHtmlElement extends HTMLElement {
    /**
     * Version information about the document's DTD. See the version attribute 
     * definition in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public String getVersion();
    /**
     * Version information about the document's DTD. See the version attribute 
     * definition in HTML 4.01. This attribute is deprecated in HTML 4.01.
     */
    public void setVersion(String version);

}
