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
package com.subgraph.vega.ui.web.views;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.model.scope.ITargetScopeManager;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.api.model.web.IWebResponse;
import com.subgraph.vega.ui.tree.web.WebModelAdapter;
import com.subgraph.vega.ui.util.ImageCache;
import com.subgraph.vega.ui.web.Activator;

public class WebsiteLabelProvider extends LabelProvider implements IColorProvider {
	private final static Color UNVISITED_COLOR = new Color(
			Display.getCurrent(), new RGB(180, 180, 180));
	private final static Color IN_SCOPE_BACKGROUND = new Color(Display.getCurrent(), new RGB(230, 230, 250));

	private final static String WEBSITE = "icons/websites.png";

	private final static String ARCHIVE = "icons/mimetype/archive.png";
	private final static String AUDIO = "icons/mimetype/audio.png";
	private final static String BINARY = "icons/mimetype/binary.png";
	private final static String EXECUTABLE = "icons/mimetype/executable.png.png";
	private final static String FLASH = "icons/mimetype/flash.png";
	private final static String FONT = "icons/mimetype/font.png";
	private final static String HTML = "icons/mimetype/html.png";
	private final static String IMAGE = "icons/mimetype/image.png";
	private final static String PDF = "icons/mimetype/pdf.png";
	private final static String POSTSCRIPT = "icons/mimetype/postscript.png";
	private final static String RSS = "icons/mimetype/rss.png";
	private final static String SCRIPT = "icons/mimetype/script.png";
	private final static String TEXT = "icons/mimetype/text.png";
	private final static String VIDEO = "icons/mimetype/video.png";
	private final static String XML = "icons/mimetype/xml.png";
	private final static String MSWORD = "icons/mimetype/msword.png";
	private final static String PRESENTATION = "icons/mimetype/presentation.png";
	private final static String SPREADSHEET = "icons/mimetype/spreadsheet.png";

	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);
	private final WebModelAdapter webAdapter = new WebModelAdapter();
	private ITargetScopeManager scopeManager;
	
	void setTargetScopeManager(ITargetScopeManager scopeManager) {
		this.scopeManager = scopeManager;
	}
	public String getText(Object element) { 
		return webAdapter.getLabel(element);
	}
	
	public Image getImage(Object element) {
		if(element instanceof IWebHost) {
			IWebHost wh = (IWebHost) element;
			if(wh.isVisited())
				return imageCache.get(WEBSITE);
			else
				return imageCache.getDisabled(WEBSITE);
		} else {
			final String mimeType = getMimeTypeForElement(element);
			return getMimeImage(mimeType);
		}
	}
	
	private String getMimeTypeForElement(Object element) {
		if(element instanceof IWebResponse) {
			return ((IWebResponse)element).getMimeType();
		} else if(element instanceof IWebPath) {
			return ((IWebPath) element).getMimeType();
		} else {
			return null;
		}
	}
	
	@Override
	public void dispose() {
		imageCache.dispose();
		super.dispose();
	}

	@Override
	public Color getForeground(Object element) {
		if(element instanceof IWebEntity) {
			IWebEntity we = (IWebEntity) element;
			return (we.isVisited()) ? (null) : (UNVISITED_COLOR);
		}			
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		if(element instanceof IWebEntity) {
			if(isInScope((IWebEntity) element)) {
				return IN_SCOPE_BACKGROUND;
			}
		}
		return null;
	}
	
	private boolean isInScope(IWebEntity entity) {
		if(scopeManager == null) {
			return false;
		} else if(entity instanceof IWebHost) {
			return scopeManager.getActiveScope().filter(((IWebHost)entity).getUri());
		} else if(entity instanceof IWebPath) {
			return scopeManager.getActiveScope().filter(((IWebPath)entity).getUri());
		} else if(entity instanceof IWebResponse){
			IWebPath path = ((IWebResponse)entity).getPathEntity();
			return scopeManager.getActiveScope().filter(path.getUri());
		} else {
			return false;
		}
	}

	private Image getMimeImage(String contentType) {
		String path = getMimeImagePath(contentType);
		if(path == null) {
			return null;
		} else {
			return imageCache.get(path);
		}
	}
	
	private String getMimeImagePath(String contentType) {
		if(contentType == null) return null;
		else if(contentType.matches("text/html.*")) return HTML;
		else if(contentType.matches("text/(javascript|vbscript|tcl)|application/(x-)?(javascript|perl|tcl|c?sh)")) return SCRIPT;
		else if(contentType.matches("((text|application)/xml|application/x-(xhtml|xml)).*")) return XML;
		else if(contentType.matches("text/.*")) return TEXT;
		else if(contentType.matches("image/.*")) return IMAGE;
		else if(contentType.matches("audio/.*")) return AUDIO;
		else if(contentType.matches("video/.*")) return VIDEO;
		else if(contentType.matches("application/x-(archive|arj|.?zip(-compressed)?|compress|cpio|jar|lha|lhz|rar|rpm|deb|stuffit|g?tar|shar).*")) return ARCHIVE;
		else if(contentType.matches("application/java-archive")) return ARCHIVE;
		else if(contentType.matches("application/.*zip.*")) return ARCHIVE;
		else if(contentType.matches("application.*executable.*")) return EXECUTABLE;
		else if(contentType.matches("application/x-shockwave.*")) return FLASH;
		else if(contentType.matches("application/pdf")) return PDF;
		else if(contentType.matches("application/postscript")) return POSTSCRIPT;
		else if(contentType.matches("application/msword")) return MSWORD;
		else if(contentType.matches("application/.*excel")) return SPREADSHEET;
		else if(contentType.matches("application/.*powerpoint")) return PRESENTATION;
		else if(contentType.matches("application/.*font.*")) return FONT;
		else if(contentType.matches("application/(rss|atom)\\+xml.*")) return RSS;
		else if(contentType.matches("application/octet-stream")) return BINARY;
		else return null;
		
	}
}
