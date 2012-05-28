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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;

public class HttpMessageEntity {
	
	static HttpMessageEntity createFromEntity(HttpEntity entity) {
		if(entity == null || entity.getContentLength() == 0) {
			return createEmptyEntity();
		}
		try {
			final String asString = EntityUtils.toString(entity);
			if(isBodyAscii(asString)) {
				return createAsciiEntity(entity, asString);
			} else {
				return createFromBinaryEntity(entity, EntityUtils.toByteArray(entity));
			}
		} catch (ParseException e) {
			return createEmptyEntity();
		} catch (IOException e) {
			return createEmptyEntity();
		}
	}
	
	private static HttpMessageEntity createFromBinaryEntity(HttpEntity entity, byte[] binaryData) {
		if(binaryData == null || binaryData.length == 0) {
			return createEmptyEntity();
		}
		final ImageData imageData = binaryToImageData(binaryData);
		if(imageData != null) {
			return createImageEntity(entity, binaryData, imageData);
		} else {
			return createBinaryEntity(entity, binaryData);
		}
	}
	
	private static boolean isBodyAscii(String body) {
		if(body == null || body.isEmpty())
			return false;
		
		final int total = (body.length() > 500) ? (500) : (body.length());
		int printable = 0;
		for(int i = 0; i < total; i++) {
			char c = body.charAt(i);
			if((c >= 0x20 && c <= 0x7F) || Character.isWhitespace(c))
				printable += 1;
		}
		return ((printable * 100) / total > 90);
	}
	
	private static ImageData binaryToImageData(byte[] binaryData) {
		try {
			return new ImageData(new ByteArrayInputStream(binaryData));
		} catch (SWTException e) {
			return null;
		}
	}

	private static HttpMessageEntity createEmptyEntity() {
		return new HttpMessageEntity(EntityType.ENTITY_NONE, null, "", new byte[0], null);
	}
	
	private static HttpMessageEntity createImageEntity(HttpEntity entity, byte[] imageBytes, ImageData imageData) {
		return new HttpMessageEntity(EntityType.ENTITY_IMAGE, entity, "", imageBytes, imageData);
	}
	
	private static HttpMessageEntity createBinaryEntity(HttpEntity entity, byte[] data) {
		return new HttpMessageEntity(EntityType.ENTITY_BINARY, entity, "", data, null);
	}
	
	private static HttpMessageEntity createAsciiEntity(HttpEntity entity, String text) {
		return new HttpMessageEntity(EntityType.ENTITY_ASCII, entity, text, new byte[0], null);
	}
	
    enum EntityType { ENTITY_NONE, ENTITY_IMAGE, ENTITY_BINARY, ENTITY_ASCII };

	private final HttpEntity entity;
	private final EntityType entityType;
	private final ImageData entityImage;
	private final String entityText;
	private final byte[] entityBytes;
	private final String contentType;
	private final String contentEncoding;
		
	private HttpMessageEntity(EntityType type, HttpEntity entity, String text, byte[] data, ImageData image) {
		this.entityType = type;
		this.entity = entity;
		this.entityText = text;
		this.entityBytes = data;
		this.entityImage = image;
		this.contentType = getContentType(entity);
		this.contentEncoding = getContentEncoding(entity);
	}

	public String getContentType() {
		return contentType;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	private String getContentType(HttpEntity entity) {
		if(entity == null) {
			return "";
		}
		return getHeaderValue(entity.getContentType());
	}
	
	
	private String getContentEncoding(HttpEntity entity) {
		if(entity == null) {
			return "";
		}
		return getHeaderValue(entity.getContentEncoding());
	}
	
	public HttpEntity getEntity() {
		return entity;
	}

	private String getHeaderValue(Header header) {
		if(header == null || header.getValue() == null) {
			return "";
		}
		return header.getValue();
	}
	
	public boolean isEmptyEntity() {
		return entityType == EntityType.ENTITY_NONE;
	}
	
	public boolean isImageEntity() {
		return entityType == EntityType.ENTITY_IMAGE;
	}
	
	public boolean isBinaryEntity() {
		return entityType == EntityType.ENTITY_BINARY;
	}
	
	public boolean isAsciiEntity() {
		return entityType == EntityType.ENTITY_ASCII;
	}
	
	public ImageData getImageData() {
		return entityImage;
	}
	
	public byte[] getBinaryData() {
		return entityBytes;
	}
	
	public String getTextData() {
		return entityText;
	}
}
