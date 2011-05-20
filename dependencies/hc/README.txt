
How to build OSGi compatible HttpComponents bundles for Vega.

(
1) Download the latest versions of the source packages:

    httpcomponents-client-4.1.1-src.tar.gz
    httpcomponents-core-4.1-src.tar.gz

2) Unpack both archives and build them with the command 'mvn package'

3) Build artifacts can be found in

   httpcomponents-client-4.1.1/httpclient/target
   httpcomponents-core-4.1/httpcore/target

4) Copy the following 6 jar files into the lib/ directory:

  From httpcomponents-client-4.1.1/httpclient/target

    httpclient-4.1.1.jar
    httpclient-4.1.1-sources.jar

  From httpcomponents-core-4.1/httpcore/target

    httpcore-4.1-sources.jar
    httpcore-4.1.jar
    httpcore-nio-4.1.jar
    httpcore-nio-4.1-sources.jar

5) Run 'ant', generated jars appear in output/ directory

    org.apache.http.client_4.1.1.vega.jar
    org.apache.http.core_4.1.vega.jar 
    org.apache.http.core.nio.source_4.1.vega.jar
    org.apache.http.client.source_4.1.1.vega.jar
    org.apache.http.core.nio_4.1.vega.jar
    org.apache.http.core.source_4.1.vega.jar

6) Move all of these to

  Vega/platform/com.subgraph.vega.application/extra-bundles

  Delete the bundles which are begin upgraded 


7) possibly change feature.xml, reset target platform, etc...
