package com.subgraph.vega.internal.http.requests;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.subgraph.vega.api.http.requests.IHttpResponse;

public class EngineHttpResponse implements IHttpResponse {
	private final Logger logger = Logger.getLogger("request-engine");
	private final HttpResponse rawResponse;
	private String cachedString;
	private boolean stringExtractFailed;
	
	EngineHttpResponse(HttpResponse rawResponse) {
		this.rawResponse = rawResponse;
	}

	@Override
	public HttpResponse getRawResponse() {
		return rawResponse;
	}

	@Override
	public String getBodyAsString() {
		synchronized (rawResponse) {
			
			if(cachedString != null)
				return cachedString;
			if(stringExtractFailed || rawResponse.getEntity() == null)
				return null;
			try {
				cachedString = EntityUtils.toString(rawResponse.getEntity());
				return cachedString;
			} catch (ParseException e) {
				logger.log(Level.WARNING, "Error parsing response headers: "+ e.getMessage(), e);
				stringExtractFailed = true;
				return null;
			} catch (IOException e) {
				logger.log(Level.WARNING, "IO error extracting response entity: "+ e.getMessage(), e);
				stringExtractFailed = true;
				return null;
			}
		}
	}

}
