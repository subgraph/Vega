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
package com.subgraph.vega.ui.httpeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;
import com.subgraph.vega.ui.httpeditor.html.HtmlTextEntityRenderer;
import com.subgraph.vega.ui.httpeditor.http.HeaderPartitionRule;
import com.subgraph.vega.ui.httpeditor.http.RequestLinePartitionRule;
import com.subgraph.vega.ui.httpeditor.http.ResponseLinePartitionRule;
import com.subgraph.vega.ui.httpeditor.js.JavascriptTextEntityRenderer;

public class HttpMessageDocumentFactory {
	private final static int MAX_LONG_LINE = 200;
	private final static int ABSOLUTE_MAX_LONG_LINE = MAX_LONG_LINE + 20;

	public final static String PARTITION_REQUEST_LINE = "http-request-line";
	public final static String PARTITION_RESPONSE_LINE = "http-response-line";
	public final static String PARTITION_MESSAGE_HEADER = "http-header";

	public final static String[] PARTITION_TYPES = { 
		PARTITION_REQUEST_LINE, 
		PARTITION_RESPONSE_LINE, 
		PARTITION_MESSAGE_HEADER 
	};
	
	public final static ITextEntityRenderer[] ENTITY_RENDERERS = {
		new HtmlTextEntityRenderer(),
		new JavascriptTextEntityRenderer(),
		new DefaultTextEntityRenderer()
	};

	private final HttpHeaderTextRenderer messageRenderer = new HttpHeaderTextRenderer();
	
	HttpMessageDocument createForRequest(HttpRequest request) {
		if(request instanceof HttpEntityEnclosingRequest) {
			final HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			return createForText(messageRenderer.getRequestAsText(request), entity);
		} else {
			return createForText(messageRenderer.getRequestAsText(request));
		}
	}

	HttpMessageDocument createForResponse(HttpResponse response) {
		return createForText(messageRenderer.getResponseAsText(response), response.getEntity());
	}

	HttpMessageDocument createForRequest(IHttpRequestBuilder builder) {
		return createForText(messageRenderer.getRequestAsText(builder), builder.getEntity());
	}

	HttpMessageDocument createForResponse(IHttpResponseBuilder builder) {
		return createForText(messageRenderer.getResponseAsText(builder), builder.getEntity());
	}
	
	private HttpMessageDocument createForText(String text) {
		return createForText(text, null);
	}

	private HttpMessageDocument createForText(String text, HttpEntity entity) {
		final HttpMessageEntity messageEntity = HttpMessageEntity.createFromEntity(entity);
		if(messageEntity.isAsciiEntity()) {
			final ITextEntityRenderer renderer = getTextEntityRendererForType(messageEntity.getContentType());
			return createForHeaderTextWithRenderer(text, messageEntity, renderer);
		} else {
			return createForHeaderTextWithRenderer(text, messageEntity, null);
		}
	}
	
	private HttpMessageDocument createForHeaderTextWithRenderer(String text, HttpMessageEntity messageEntity, ITextEntityRenderer renderer) {
		final String bodyText = getBodyText(messageEntity, renderer);

		final IDocument document = createDocument(text, bodyText, renderer);
		return new HttpMessageDocument(document, messageEntity);
	}
	
	private String getBodyText(HttpMessageEntity messageEntity, ITextEntityRenderer renderer) {
		if(!messageEntity.isAsciiEntity() || renderer == null || messageEntity.getTextData().isEmpty()) {
			return "";
		}
		final String formattedText = renderer.formatText(messageEntity.getTextData());
		return longLineFormatter(formattedText, renderer.getLineSplitChars());
	}

	private String longLineFormatter(String input, String splitAt) {
		final StringBuilder sb = new StringBuilder();
		int count = 0;
		char c;
		for(int i = 0; i < input.length(); i++) {
			count += 1;
			c = input.charAt(i);
			sb.append(c);
			if(c == '\n') {
				count = 0;
			}
			if(count > ABSOLUTE_MAX_LONG_LINE || 
					(count > MAX_LONG_LINE && splitAt.indexOf(c) != -1)) {
				sb.append("\n");
				count = 0;
			} 
		}
		return sb.toString();
	}

	private ITextEntityRenderer getTextEntityRendererForType(String contentType) {
		for(ITextEntityRenderer ter: ENTITY_RENDERERS) {
			if(ter.matchContentType(contentType.toLowerCase())) {
				return ter;
			}
		}
		return new DefaultTextEntityRenderer();
	}
	
	private IDocument createDocument(String headerText, String bodyText, ITextEntityRenderer renderer) {
		final IDocument document = createEmptyDocument(renderer);
		document.addPositionCategory(HttpMessageDocument.SECTION_POSITION_CATEGORY);
		document.addPositionUpdater(new DefaultPositionUpdater(HttpMessageDocument.SECTION_POSITION_CATEGORY));
		
		if(bodyText != null && !bodyText.isEmpty()) {
			document.set(headerText + bodyText);
			addSectionPosition(document, 0, headerText.length());
			addSectionPosition(document, headerText.length(), bodyText.length());
		} else {
			document.set(headerText);
			addSectionPosition(document, 0, headerText.length());
		}
		return document;
	}
		
	private void addSectionPosition(IDocument document, int offset, int length) {
		try {
			document.addPosition(HttpMessageDocument.SECTION_POSITION_CATEGORY, new Position(offset, length));
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		} catch (BadPositionCategoryException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private IDocument createEmptyDocument(ITextEntityRenderer renderer) {
		final Document document = new Document();
		final IDocumentPartitioner partitioner = createPartitioner(renderer);
		document.setDocumentPartitioner(partitioner);
		partitioner.connect(document);
		return document;
	}
	
	private IDocumentPartitioner createPartitioner(ITextEntityRenderer renderer) {
		final RuleBasedPartitionScanner scanner = new RuleBasedPartitionScanner();
		scanner.setPredicateRules(createRules(renderer));
		if(renderer != null && renderer.getDefaultPartitionType() != null) {
			scanner.setDefaultReturnToken(new Token(renderer.getDefaultPartitionType()));
		}
		return new FastPartitioner(scanner, getPartitionTypes(renderer));
	}
	
	private IPredicateRule[] createRules(ITextEntityRenderer renderer) {
		final List<IPredicateRule> rules = new ArrayList<IPredicateRule>();
		addHeaderPartitionRules(rules);
		if(renderer != null) {
			renderer.addPartitionScannerRules(rules);
		}

		return rules.toArray(new IPredicateRule[0]);
	}

	private void addHeaderPartitionRules(List<IPredicateRule> rules) {
		rules.add(new RequestLinePartitionRule(new Token(PARTITION_REQUEST_LINE)));
		rules.add(new ResponseLinePartitionRule(new Token(PARTITION_RESPONSE_LINE)));
		rules.add(new HeaderPartitionRule(new Token(PARTITION_MESSAGE_HEADER)));
	}
	
	private String[] getPartitionTypes(ITextEntityRenderer renderer) {
		final List<String> types = new ArrayList<String>();
		types.addAll(Arrays.asList(PARTITION_TYPES));
		if(renderer != null) {
			types.addAll(renderer.getPartitionTypes());
		}
		return types.toArray(new String[0]);
	}

}
