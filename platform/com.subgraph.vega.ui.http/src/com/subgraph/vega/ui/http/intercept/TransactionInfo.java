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
package com.subgraph.vega.ui.http.intercept;

import java.net.URISyntaxException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.requests.IHttpRequestBuilder;
import com.subgraph.vega.api.http.requests.IHttpResponseBuilder;

/**
 * Provides a modifiable snapshot of a transaction held by the TransactionManager. The information contained here is
 * used to display information in the UI.
 */
public class TransactionInfo {
	private int currentSerial; // Serial of the pending transaction

	private int requestTransactionSerial;
	private final IHttpRequestBuilder requestBuilder;
	private boolean requestHasContent;
	private TransactionManager.TransactionStatus requestStatus;
	private String requestStatusMessage;

	private int responseTransactionSerial;
	private final IHttpResponseBuilder responseBuilder;
	private boolean responseHasContent;
	private TransactionManager.TransactionStatus responseStatus;
	private String responseStatusMessage;
	
	public TransactionInfo(IHttpRequestBuilder requestBuilder, IHttpResponseBuilder responseBuilder) {
		currentSerial = -1;
		requestTransactionSerial = -1;
		this.requestBuilder = requestBuilder;
		requestHasContent = false;
		setRequestStatus(TransactionManager.TransactionStatus.STATUS_INACTIVE);
		responseTransactionSerial = -1;
		this.responseBuilder = responseBuilder;
		responseHasContent = false;
		setResponseStatus(TransactionManager.TransactionStatus.STATUS_INACTIVE);
	}

	public void setCurrentSerial(int serial) {
		this.currentSerial = serial;
	}

	public int getCurrentSerial() {
		return currentSerial;
	}
	
	public boolean isPending() {
		return (requestStatus == TransactionManager.TransactionStatus.STATUS_PENDING ||
				responseStatus == TransactionManager.TransactionStatus.STATUS_PENDING);
	}

	public void setRequestTransactionSerial(int requestTransactionSerial) {
		this.requestTransactionSerial = requestTransactionSerial;
	}
	
	public int getRequestTransactionSerial() {
		return requestTransactionSerial;
	}

	public void setFromRequest(HttpRequest request) throws URISyntaxException {
		requestBuilder.setFromRequest(request);
	}

	public IHttpRequestBuilder getRequestBuilder() {
		return requestBuilder;
	}

	public void setRequestHasContent(boolean requestHasContent) {
		this.requestHasContent = requestHasContent;
	}
	
	public boolean requestHasContent() {
		return requestHasContent;
	}

	public void setRequestStatus(TransactionManager.TransactionStatus requestStatus) {
		this.requestStatus = requestStatus;
		switch (requestStatus) {
		case STATUS_INACTIVE:
			requestStatusMessage = "No request pending";
			break;
		case STATUS_PENDING:
			requestStatusMessage = "Request pending to " + getRequestHostPart();
			break;
		case STATUS_SENT:
			requestStatusMessage = "Request sent, awaiting response";
			break;
		}
	}
	
	private String getRequestHostPart() {
		final StringBuilder buf = new StringBuilder();
		buf.append(requestBuilder.getScheme());
		buf.append("://");
		buf.append(requestBuilder.getHost());
		if (requestBuilder.getHostPort() != -1) {
			buf.append(':');
			buf.append(Integer.toString(requestBuilder.getHostPort()));
		}
		return buf.toString();
	}

	public TransactionManager.TransactionStatus getRequestStatus() {
		return requestStatus; 
	}

	public boolean requestIsPending() {
		return (requestStatus == TransactionManager.TransactionStatus.STATUS_PENDING);
	}

	public String getRequestStatusMessage() {
		return requestStatusMessage;
	}
	
	public void setResponseTransactionSerial(int responseTransactionSerial) {
		this.responseTransactionSerial = responseTransactionSerial;
	}
	
	public int getResponseTransactionSerial() {
		return responseTransactionSerial;
	}

	public void setFromResponse(HttpResponse response) throws URISyntaxException {
		responseBuilder.setFromResponse(response);
	}

	public IHttpResponseBuilder getResponseBuilder() {
		return responseBuilder;
	}

	public void setResponseHasContent(boolean responseHasContent) {
		this.responseHasContent = responseHasContent;
	}
	
	public boolean responseHasContent() {
		return responseHasContent;
	}

	public void setResponseStatus(TransactionManager.TransactionStatus responseStatus) {
		this.responseStatus = responseStatus;
		switch (responseStatus) {
		case STATUS_INACTIVE:
			responseStatusMessage = "No response pending";
			break;
		case STATUS_PENDING:
			responseStatusMessage = "Response pending";
			break;
		case STATUS_SENT:
			break;
		default:
			break;
		}
	}

	public TransactionManager.TransactionStatus getResponseStatus() {
		return responseStatus; 
	}

	public boolean responseIsPending() {
		return (responseStatus == TransactionManager.TransactionStatus.STATUS_PENDING);
	}

	public String getResponseStatusMessage() {
		return responseStatusMessage;
	}

}
