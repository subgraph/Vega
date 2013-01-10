
How to build OSGi compatible HttpComponents bundles for Vega.

(
1) Download the latest versions of the source packages:

    httpcomponents-client-4.2.2-src.tar.gz
    httpcomponents-core-4.2.3-src.tar.gz

2) Unpack both archives.

3) Patch httpclient file: 

     httpclient/src/main/java/org/apache/http/impl/client/DefaultRequestDirector

   Change method wrapRequest() from private to protected:

     DefaultRequestDirector#wrapRequest(HttpRequest request)


4) Build both packages with the command 'mvn package'

5) Build artifacts can be found in

   httpcomponents-client-4.2.2/httpclient/target
   httpcomponents-core-4.2.3/httpcore/target

6) Copy the following 4 jar files into the lib/ directory:

  From httpcomponents-client-4.2.2/httpclient/target

    httpclient-4.2.2.jar
    httpclient-4.2.2-sources.jar

  From httpcomponents-core-4.2.3/httpcore/target

    httpcore-4.2.3-sources.jar
    httpcore-4.2.3.jar

7) Run 'ant', generated jars appear in output/ directory

    org.apache.http.client_4.2.2.vega.jar
    org.apache.http.core_4.2.3.vega.jar 
    org.apache.http.client.source_4.2.2.vega.jar
    org.apache.http.core.source_4.2.3.vega.jar

8) Move all of these to

  Vega/platform/com.subgraph.vega.application/extra-bundles

  Delete the bundles which are begin upgraded 


9) possibly change feature.xml, reset target platform, etc...
