package com.subgraph.vega.api.http.requests;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public interface IHttpResponseBuilder extends IHttpMessageBuilder {
	/**
	 * Set Response fields from a HttpResponse provided by IRequestLogRecord. Any previously set fields are unset.
	 *
	 * @param record IRequestLogRecord containing HttpResponse.
	 */
	void setFromResponse(IRequestLogRecord record);

	/**
	 * Set Response fields from a HttpResponse. Any previously set fields are unset.
	 *
	 * @param response HttpResponse.
	 */
	void setFromResponse(HttpResponse response);

	/**
	 * Set the response protocol version, status code, and reason pharse fields from a ResponseLine.
	 *
	 * @param statusLine StatusLine 
	 */
	void setFromStatusLine(StatusLine statusLine);

	/**
	 * Get the status line.
	 * 
	 * @return Status line.
	 */
	String getStatusLine();

	/**
	 * Build a HttpResponse.
	 * 
	 * @return HttpResponse.
	 */
	HttpResponse buildResponse();
}
