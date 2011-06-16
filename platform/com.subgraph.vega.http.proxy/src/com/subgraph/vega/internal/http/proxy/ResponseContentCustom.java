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
package com.subgraph.vega.internal.http.proxy;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * Custom version of ResponseContent interceptor for outgoing responses. Instead of disallowing Content-Length or
 * Transfer-Encoding headers, overwrite them with valid values. Also ensure they are not set when the response
 * does not contain an entity body.
 */
public class ResponseContentCustom implements HttpResponseInterceptor {
	
    public ResponseContentCustom() {
        super();
    }
    
    public void process(final HttpResponse response, final HttpContext context)  throws HttpException, IOException {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }

        ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            long len = entity.getContentLength();

            if (entity.isChunked() && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
            	response.removeHeaders(HTTP.CONTENT_LEN);
            	response.setHeader(HTTP.TRANSFER_ENCODING, HTTP.CHUNK_CODING);
            } else if (len >= 0) {
            	response.removeHeaders(HTTP.TRANSFER_ENCODING);
            	response.setHeader(HTTP.CONTENT_LEN, Long.toString(entity.getContentLength()));
            }

            // Specify a content type if known
            if (entity.getContentType() != null && !response.containsHeader(HTTP.CONTENT_TYPE)) {
                response.addHeader(entity.getContentType()); 
            }

            // Specify a content encoding if known
            if (entity.getContentEncoding() != null && !response.containsHeader(HTTP.CONTENT_ENCODING)) {
                response.addHeader(entity.getContentEncoding()); 
            }
        } else {
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_NO_CONTENT 
                    && status != HttpStatus.SC_NOT_MODIFIED
                    && status != HttpStatus.SC_RESET_CONTENT) {
                response.setHeader(HTTP.CONTENT_LEN, "0");
            }
        }
    }

}
