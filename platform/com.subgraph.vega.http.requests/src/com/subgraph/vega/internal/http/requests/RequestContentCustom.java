package com.subgraph.vega.internal.http.requests;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * Custom version of RequestContent interceptor for outgoing requests. Instead of disallowing Content-Length or
 * Transfer-Encoding headers, overwrite them with valid values. Also ensure they are not set when the request
 * does not contain an entity body.
 */
public class RequestContentCustom implements HttpRequestInterceptor {

    public RequestContentCustom() {
        super();
    }
    
	@Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }

        if (request instanceof HttpEntityEnclosingRequest) {
            ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity == null) {
                request.setHeader(HTTP.CONTENT_LEN, "0");
                return;
            }

            // Must specify a transfer encoding or a content length 
            if (entity.isChunked() || entity.getContentLength() < 0) {
                if (ver.lessEquals(HttpVersion.HTTP_1_0)) {
                    throw new ProtocolException("Chunked transfer encoding not allowed for " + ver);
                }
                request.removeHeaders(HTTP.CONTENT_LEN);
                request.setHeader(HTTP.TRANSFER_ENCODING, HTTP.CHUNK_CODING);
            } else {
                request.removeHeaders(HTTP.TRANSFER_ENCODING);
                request.setHeader(HTTP.CONTENT_LEN, Long.toString(entity.getContentLength()));
            }

            // Specify a content type if known
            if (entity.getContentType() != null && !request.containsHeader(HTTP.CONTENT_TYPE)) {
                request.addHeader(entity.getContentType()); 
            }

            // Specify a content encoding if known
            if (entity.getContentEncoding() != null && !request.containsHeader(HTTP.CONTENT_ENCODING)) {
                request.addHeader(entity.getContentEncoding()); 
            }
        } else {
            request.removeHeaders(HTTP.CHUNK_CODING);
            request.removeHeaders(HTTP.TRANSFER_ENCODING);
            request.removeHeaders(HTTP.CONTENT_TYPE);
            request.removeHeaders(HTTP.CONTENT_ENCODING);
        }
    }

}
