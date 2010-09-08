package com.subgraph.vega.ui.httpeditor.hover;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.subgraph.vega.ui.httpeditor.HttpRequestViewer;
import com.subgraph.vega.ui.httpeditor.dom.Element;
import com.subgraph.vega.ui.httpeditor.dom.RequestModel;

public class HttpRequestImageHover implements ITextHover, ITextHoverExtension2 {

	private final HttpRequestViewer requestViewer;
	
	public HttpRequestImageHover(HttpRequestViewer requestViewer) {
		this.requestViewer = requestViewer;
	}
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		RequestModel model = requestViewer.getRequestModel();
		if(model != null) {
			Element e = model.findElementAtOffset(offset);
			System.out.println("Element is "+ e);
		}
		
		return new Region(offset, 0);
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		return requestViewer.getCurrentImage();
	}

}