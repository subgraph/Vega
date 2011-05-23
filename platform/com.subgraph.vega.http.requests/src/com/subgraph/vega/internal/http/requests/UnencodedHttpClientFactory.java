package com.subgraph.vega.internal.http.requests;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.internal.http.requests.connection.UnencodingThreadSafeClientConnectionManager;

public class UnencodedHttpClientFactory extends AbstractHttpClientFactory {
	private static final String userAgent = 
		"User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; "+
          "Trident/4.0; .NET CLR 1.1.4322; InfoPath.1; .NET CLR "+
          "2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; Vega/1.0";
          
	static HttpClient createHttpClient() {
		final HttpParams params = createHttpParams();
		final ClientConnectionManager ccm = createConnectionManager(params);
		final DefaultHttpClient client = new DefaultHttpClient(ccm, params);
		
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		client.addRequestInterceptor(new RequestCopyHeadersInterceptor());
		return client;
	}

	private static ClientConnectionManager createConnectionManager(HttpParams params) {
		final SchemeRegistry sr = createSchemeRegistry();
		return new UnencodingThreadSafeClientConnectionManager(sr);
	}

	private static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params, userAgent);
		return params;
	}

}
