package com.subgraph.vega.internal.http.requests;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpVersion;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.subgraph.vega.internal.http.requests.unencoding.UnencodingThreadSafeClientConnectionManager;

public class HttpClientFactory {

	private static final String userAgent = 
		"User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; "+
          "Trident/4.0; .NET CLR 1.1.4322; InfoPath.1; .NET CLR "+
          "2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; Vega/1.0";
          
	static VegaHttpClient createHttpClient() {
		final HttpParams params = createHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setUserAgent(params, userAgent);
		final ClientConnectionManager ccm = createConnectionManager(params);
		final VegaHttpClient client = new VegaHttpClient(ccm, params);
		client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
		return client;
	}

	private static ClientConnectionManager createConnectionManager(HttpParams params) {
		final SchemeRegistry sr = createSchemeRegistry();
		return new UnencodingThreadSafeClientConnectionManager(params, sr);
	}

	private static HttpParams createHttpParams() {
		final HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		ConnManagerParams.setMaxTotalConnections(params, 10);
		return params;
	}

	private static SchemeRegistry createSchemeRegistry() {
		final SchemeRegistry sr = new SchemeRegistry();
		sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

	
		SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

			       public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			       }

			       public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			       }

			       public X509Certificate[] getAcceptedIssuers() {
			           return null;
			        }
			 };
			 ctx.init(null, new X509TrustManager[]{tm}, null);
			 SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			 ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			 sr.register(new Scheme("https", ssf, 443));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sr;
	}
}
