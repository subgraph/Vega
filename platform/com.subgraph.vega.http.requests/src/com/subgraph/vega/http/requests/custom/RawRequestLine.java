package com.subgraph.vega.http.requests.custom;

import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicRequestLine;

public class RawRequestLine extends BasicRequestLine {
	private String rawLine;

	/**
	 * @param rawLine Raw request line to be sent to server, excluding trailing CRLF. Contents should match the other
	 * parameters. Optional; when null the request line is generated using the LineFormatter.
	 */
	public RawRequestLine(final String rawLine, final String method, final String uri, final ProtocolVersion version) {
		super(method, uri, version);
		this.rawLine = rawLine;
	}

	@Override
	public String toString() {
		if (rawLine != null) {
			return rawLine;
		} else {
			return super.toString();
		}
	}

}
