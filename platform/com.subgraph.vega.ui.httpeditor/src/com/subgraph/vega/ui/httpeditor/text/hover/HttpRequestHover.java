package com.subgraph.vega.ui.httpeditor.text.hover;

import java.util.Iterator;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;

import com.subgraph.vega.ui.httpeditor.text.annotation.Base64DataAnnotation;
import com.subgraph.vega.ui.httpeditor.text.annotation.HexDataAnnotation;
import com.subgraph.vega.ui.httpeditor.text.dom.Element;
import com.subgraph.vega.ui.httpeditor.text.dom.RequestModel;
import com.subgraph.vega.ui.text.httpeditor.HttpRequestViewer;

public class HttpRequestHover implements ITextHover, ITextHoverExtension2 {

	private final HttpRequestViewer viewer;
	
	public HttpRequestHover(HttpRequestViewer viewer) {
		this.viewer = viewer;
	}
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		RequestModel model = viewer.getRequestModel();
		if(model != null) {
			Element e = model.findElementAtOffset(offset);
			System.out.println("Element is "+ e);
		}
		return new Region(offset, 0);
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		System.out.println("get info at "+ hoverRegion);
		IAnnotationModelExtension2 model = (IAnnotationModelExtension2) viewer.getAnnotationModel();
		Iterator<?> it = model.getAnnotationIterator(hoverRegion.getOffset(), 0, true, true);
		while(it.hasNext()) {
			Annotation a = (Annotation) it.next();
			if(a.getType().equals(HexDataAnnotation.TYPE)) {
				return processHexDataAnnotation((HexDataAnnotation) a);
			} else if(a.getType().equals(Base64DataAnnotation.TYPE)) {
				return processBase64DataAnnotation((Base64DataAnnotation) a);
			}
		}
		
		return null;
	}
	
	IBinaryEncodedData processHexDataAnnotation(HexDataAnnotation annotation) {
		return new HexEncodedData(annotation.getEncodedData());
	}
	
	IBinaryEncodedData processBase64DataAnnotation(Base64DataAnnotation annotation) {
		return new Base64EncodedData(annotation.getEncodedData(), annotation.isUrlSafeEncoding());
	}
	
}