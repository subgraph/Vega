importPackage(java.net);

var module = {
  name: "Oracle Application Server Fingerprint Module",
  type: "response-processor"
};

function run(request, response, ctx) {


/*
  var oraclefingerprints = ["Oracle-Application-Server-10g",
                            "Oracle-Application-Server-10g/10.1.2.0.0 Oracle-HTTP-Server".
                            "Oracle-Application-Server-10g/9.0.4.1.0 Oracle-HTTP-Server",
                            "Oracle-Application-Server-10g OracleAS-Web-Cache-10g/9.0.4.2.0 (N)",
"Oracle-Application-Server-10g/9.0.4.0.0",
"Oracle HTTP Server Powered by Apache",
"Oracle HTTP Server Powered by Apache/1.3.19 (Unix) mod_plsql/3.0.9.8.3a",
"Oracle HTTP Server Powered by Apache/1.3.19 (Unix) mod_plsql/3.0.9.8.3d",
"Oracle HTTP Server Powered by Apache/1.3.12 (Unix) mod_plsql/3.0.9.8.5e",
"Oracle HTTP Server Powered by Apache/1.3.12 (Win32) mod_plsql/3.0.9.8.5e",
"Oracle HTTP Server Powered by Apache/1.3.19 (Win32) mod_plsql/3.0.9.8.3c",
"Oracle HTTP Server Powered by Apache/1.3.22 (Unix) mod_plsql/3.0.9.8.3b",
"Oracle HTTP Server Powered by Apache/1.3.22 (Unix) mod_plsql/9.0.2.0.0",
"Oracle_Web_Listener/4.0.7.1.0EnterpriseEdition",
"Oracle_Web_Listener/4.0.8.2EnterpriseEdition",
"Oracle_Web_Listener/4.0.8.1.0EnterpriseEdition",
"Oracle_Web_listener3.0.2.0.0/2.14FC1",
"Oracle9iAS/9.0.2 Oracle HTTP Server",
"Oracle9iAS/9.0.3.1 Oracle HTTP Server"];
*/
  // Maybe we should not match these exact strings?  Will have to investigate.
  // var oraclefingerprints = ["Oracle"];
  // This module is incomplete but we're leaving it here.

  // IBM fingerprint IBM_HTTP_SERVER
  // Sun-ONE-Web-Server/6.1

  if (response.bodyAsString.indexOf("<!-- Created by Oracle ") >= 0) {
    var uri = String(request.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");

    ctx.addStringHighlight("<!-- Created by Oracle");
    ctx.alert("vinfo-oracle", request, response, {
      "output": "<!-- Created by Oracle..",
      "resource": uripart,
      key: "vinfo-oracle:" + uripart
    });
  }
}
