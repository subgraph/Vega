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

	static VegaHttpClient createHttpClient() {
		final HttpParams params = createHttpParams();
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
