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

import org.w3c.dom.Document;

/**
 * Create a frame. See the FRAME element definition in HTML 4.01.
 * <p>See also the <a href='http://www.w3.org/TR/2003/REC-DOM-Level-2-HTML-20030109'>Document Object Model (DOM) Level 2 HTML Specification</a>.
 */
public interface HTMLFrameElement extends HTMLElement {
    /**
     * Request frame borders. See the frameborder attribute definition in HTML 
     * 4.01.
     */
    public String getFrameBorder();
    /**
     * Request frame borders. See the frameborder attribute definition in HTML 
     * 4.01.
     */
    public void setFrameBorder(String frameBorder);

    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating a long description of this image or frame. See the 
     * longdesc attribute definition in HTML 4.01.
     */
    public String getLongDesc();
    /**
     * URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating a long description of this image or frame. See the 
     * longdesc attribute definition in HTML 4.01.
     */
    public void setLongDesc(String longDesc);

    /**
     * Frame margin height, in pixels. See the marginheight attribute 
     * definition in HTML 4.01.
     */
    public String getMarginHeight();
    /**
     * Frame margin height, in pixels. See the marginheight attribute 
     * definition in HTML 4.01.
     */
    public void setMarginHeight(String marginHeight);

    /**
     * Frame margin width, in pixels. See the marginwidth attribute definition 
     * in HTML 4.01.
     */
    public String getMarginWidth();
    /**
     * Frame margin width, in pixels. See the marginwidth attribute definition 
     * in HTML 4.01.
     */
    public void setMarginWidth(String marginWidth);

    /**
     * The frame name (object of the <code>target</code> attribute). See the 
     * name attribute definition in HTML 4.01.
     */
    public String getName();
    /**
     * The frame name (object of the <code>target</code> attribute). See the 
     * name attribute definition in HTML 4.01.
     */
    public void setName(String name);

    /**
     * When true, forbid user from resizing frame. See the noresize attribute 
     * definition in HTML 4.01.
     */
    public boolean getNoResize();
    /**
     * When true, forbid user from resizing frame. See the noresize attribute 
     * definition in HTML 4.01.
     */
    public void setNoResize(boolean noResize);

    /**
     * Specify whether or not the frame should have scrollbars. See the 
     * scrolling attribute definition in HTML 4.01.
     */
    public String getScrolling();
    /**
     * Specify whether or not the frame should have scrollbars. See the 
     * scrolling attribute definition in HTML 4.01.
     */
    public void setScrolling(String scrolling);

    /**
     * A URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating the initial frame contents. See the src attribute 
     * definition in HTML 4.01.
     */
    public String getSrc();
    /**
     * A URI [<a href='http://www.ietf.org/rfc/rfc2396.txt'>IETF RFC 2396</a>] designating the initial frame contents. See the src attribute 
     * definition in HTML 4.01.
     */
    public void setSrc(String src);

    /**
     * The document this frame contains, if there is any and it is available, 
     * or <code>null</code> otherwise.
     * @since DOM Level 2
     */
    public Document getContentDocument();

}
