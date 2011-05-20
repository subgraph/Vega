package com.subgraph.vega.internal.http.requests.connection;

import java.io.IOException;

import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.impl.io.AbstractMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;

import com.subgraph.vega.http.requests.custom.RawRequestLine;


public class VegaHttpRequestWriter extends AbstractMessageWriter {

    public VegaHttpRequestWriter(final SessionOutputBuffer buffer, final LineFormatter formatter, final HttpParams params) {
    	super(buffer, formatter, params);
    }

	@Override
	protected void writeHeadLine(HttpMessage message) throws IOException {
		RequestLine requestLine = ((HttpRequest) message).getRequestLine();
		if (requestLine instanceof RawRequestLine) {
			lineBuf.clear();
			lineBuf.append(((RawRequestLine) requestLine).toString());
		} else {
			lineFormatter.formatRequestLine(lineBuf, ((HttpRequest) message).getRequestLine());
		}
        this.sessionBuffer.writeLine(lineBuf);
	}

}
