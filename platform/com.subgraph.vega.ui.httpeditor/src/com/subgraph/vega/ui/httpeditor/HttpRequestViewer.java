package com.subgraph.vega.ui.httpeditor;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewerExtension8.EnrichMode;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.ui.httpeditor.annotation.Base64DataAnnotation;
import com.subgraph.vega.ui.httpeditor.annotation.HexDataAnnotation;
import com.subgraph.vega.ui.httpeditor.annotation.ImageAnnotation;
import com.subgraph.vega.ui.httpeditor.annotation.ImageDrawingStrategy;
import com.subgraph.vega.ui.httpeditor.dom.RequestModel;
import com.subgraph.vega.ui.httpeditor.dom.RequestParser;
import com.subgraph.vega.ui.httpeditor.hover.IHttpImage;
import com.subgraph.vega.ui.httpeditor.scanners.PartitionScanner;

public class HttpRequestViewer implements IImageSource {
	private static boolean displayImages = true;
	public static final String HTTP_PARTITIONING = "http_partitioning";
	final private SourceViewer viewer;
	final private IDocument document;
	private final RequestParser requestParser = new RequestParser();
	private AnnotationPainter painter;
	private IHttpImage currentImage;
	private RequestModel requestModel;
	
	public HttpRequestViewer(Composite parent) {
		viewer = new SourceViewer(parent, new VerticalRuler(0), SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.configure(new HttpRequestSourceViewerConfiguration(this));
		painter = createAnnotationPainter(viewer);
		viewer.addPainter(painter);
		viewer.addTextPresentationListener(painter);
		document = createDocument();
		viewer.setDocument(document, new AnnotationModel());
		viewer.setHoverEnrichMode(EnrichMode.AFTER_DELAY);
	}
	
	private AnnotationPainter createAnnotationPainter(SourceViewer sv) {
		final IAnnotationAccess annotationAccess = new IAnnotationAccess() {

			@Override
			public Object getType(Annotation annotation) {
				return annotation.getType();
			}

			@Override
			public boolean isMultiLine(Annotation annotation) {
				return true;
			}

			@Override
			public boolean isTemporary(Annotation annotation) {
				return true;
			}
			
		};
		AnnotationPainter painter = new AnnotationPainter(sv, annotationAccess);
		painter.addHighlightAnnotationType(HexDataAnnotation.TYPE);
		painter.addHighlightAnnotationType(Base64DataAnnotation.TYPE);
		Display display = Display.getDefault();
		painter.setAnnotationTypeColor(HexDataAnnotation.TYPE, new Color(display, new RGB(228,142,52)));
		painter.setAnnotationTypeColor(Base64DataAnnotation.TYPE, new Color(display, new RGB(228,242,52)));
		if(displayImages) {
			painter.addDrawingStrategy(ImageAnnotation.TYPE, new ImageDrawingStrategy(sv));
			painter.addAnnotationType(ImageAnnotation.TYPE, ImageAnnotation.TYPE);
			painter.setAnnotationTypeColor(ImageAnnotation.TYPE, sv.getTextWidget().getForeground());
		}
		return painter;
	}
	
	public IAnnotationModel getAnnotationModel() {
		return viewer.getAnnotationModel();
	}
	
	public void setEditable(boolean editable) {
		viewer.setEditable(editable);
	}

	public void clearContent() {
		setContent("");
	}
	
	public RequestModel getRequestModel() {
		return requestModel;
	}
	
	public void setContent(String content) {
		setContent(content, null);
	}
	
	public void setContent(String content, IHttpImage image) {
		clearAnnotations();
		final StringBuilder sb = new StringBuilder(content);
		
		if(image != null) {
			setCurrentImage(image);
			sb.append(imageTag(image));
			sb.append("\n");
		} else {
			setCurrentImage(null);
		}
		
		if(image != null && displayImages) {
			Position pos = addImage(image, sb);
			ImageAnnotation annotation = new ImageAnnotation(image.getImageInstance());
			document.set(sb.toString());
			getAnnotationModel().addAnnotation(annotation, pos);
		} else {
			document.set(sb.toString());
		}
		requestModel = requestParser.parse(document, getAnnotationModel());
		painter.paint(IPainter.CONFIGURATION);
	}
	
	private void clearAnnotations() {
		IAnnotationModel annotationModel = getAnnotationModel();
		if(annotationModel instanceof IAnnotationModelExtension) 
			((IAnnotationModelExtension) annotationModel).removeAllAnnotations();
	}
	
	public void setRequestModel(RequestModel model) {
		this.requestModel = model;
	}
	
	private String imageTag(IHttpImage image) {
		return "[Image: "+ image.getImageName() +" ("+ image.getImageByteSize() + " bytes) ]";
	}
	
	private Position addImage(IHttpImage image, StringBuilder buffer) { 
		buffer.append("\n");
		int start = buffer.length();
		GC gc = new GC(viewer.getTextWidget());
		int height = image.getImageInstance().getBounds().height;
		gc.setFont(viewer.getTextWidget().getFont());
		Point extent = gc.textExtent("[X]");
		gc.dispose();
		if(extent.y > 0) {
			int numLines = ((int) Math.ceil( ((double) height) / ((double) extent.y))) + 1;
			for(int i = 0; i < numLines && i < 1000; i++)
				buffer.append("\n");
		}
		return new Position(start, buffer.length() - start);
	}
	
	private void setCurrentImage(IHttpImage newImage) {
		if(currentImage != null)
			currentImage.dispose();
		currentImage = newImage;
	}
	public Control getControl() {
		return viewer.getControl();
	}
	
	public String getContent() {
		return document.get();
	}
	
	private IDocument createDocument() {
		final Document doc = new Document();
		final IPartitionTokenScanner scanner = new PartitionScanner();
		final IDocumentPartitioner partitioner = new FastPartitioner(scanner, PartitionScanner.TYPES);
		doc.setDocumentPartitioner(HTTP_PARTITIONING, partitioner);
		partitioner.connect(doc);
		return doc;
	}

	@Override
	public IHttpImage getCurrentImage() {
		return currentImage;
	}

}