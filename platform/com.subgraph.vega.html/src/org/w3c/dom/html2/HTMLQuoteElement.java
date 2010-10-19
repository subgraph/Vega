package org.w3c.dom.html2;

/**
 * For the <code>Q</code> and <code>BLOCKQUOTE</code> elements. See the Q 
 * element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLQuoteElement extends HTMLElement {
    /**
     * A URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating a source document or message. See the cite attribute 
     * definition in HTML 4.01.
     */
    public String getCite();
    /**
     * A URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating a source document or message. See the cite attribute 
     * definition in HTML 4.01.
     */
    public void setCite(String cite);

}
