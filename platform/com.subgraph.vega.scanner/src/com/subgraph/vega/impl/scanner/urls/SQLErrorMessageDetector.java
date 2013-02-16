package com.subgraph.vega.impl.scanner.urls;

import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;

public class SQLErrorMessageDetector {
	private final static String SQL_INJECTION_ALERT = "vinfo-sql-inject";
	private final static List<String> ERROR_STRINGS = Arrays.asList(
			"<b>Warning</b>:  MySQL: ",
			"Unclosed quotation mark"
			);
	
	private final ResponseAnalyzer responseAnalyzer; 
	
	public SQLErrorMessageDetector(ResponseAnalyzer responseAnalyzer) {
		this.responseAnalyzer = responseAnalyzer;
	}
	
	public void detectErrorMessages(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		final String body = response.getBodyAsString();
		if(body == null || body.isEmpty()) {
			return;
		}
		for(String errorString: ERROR_STRINGS) {
			if(body.contains(errorString)) {
				processDetectedErrorMessage(ctx, request, response, errorString);
			}
		}
	}
	
	private void processDetectedErrorMessage(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response, String errorString) {
		ctx.addStringHighlight(errorString);
		responseAnalyzer.alert(ctx, SQL_INJECTION_ALERT, "SQL Error Message Detected", request, response);
	}
}
