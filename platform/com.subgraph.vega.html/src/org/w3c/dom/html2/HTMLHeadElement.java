package org.w3c.dom.html2;

/**
 * Document head information. See the HEAD element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLHeadElement extends HTMLElement {
    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating a metadata profile. See the profile attribute 
     * definition in HTML 4.01.
     */
    public String getProfile();
    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating a metadata profile. See the profile attribute 
     * definition in HTML 4.01.
     */
    public void setProfile(String profile);

}
