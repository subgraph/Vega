var module = {
  name: "HTTP Trace Probes",
  category: "Injection Modules"
};

function initialize(ctx) {

  var ps = ctx.getPathState();
  var res = ps.getResponse();
  var server = "";
  var rootDone = 0;

  if (res == null) {
    return; // Temporary, possible bug here with null responses in some cases.
  }
  
  var alertkey = "vinfo-http-trace:"+server;

  if (ctx.alertExists(alertkey) || (ps.isParametric())) {
    return;
  }

  /* Temporary hack to make sure that the root path gets a TRACE probe. */
  /* Unavoidale race condition. TODO fix this */


  var req = ps.createRawRequest(ps.path.getHttpHost(), "TRACE", ps.path.getFullPath());
  req.addHeader("SQUEEM1SH", "OSS1FR4GE");

  rootDone = ctx.getIntegerProperty(alertkey);

  if (rootDone == null) {
      var rootReq = ps.createRawRequest(ps.path.getHttpHost(), "TRACE", "/");
      ctx.submitRequest(rootReq, process);
      ctx.setIntegerProperty(alertkey, 1);
  }

  ctx.submitRequest(req, process);

}

function process(req, res, ctx) {

  if ((res.bodyAsString.indexOf("TRACE") >= 0) && (res.bodyAsString.indexOf("OSS1FR4GE") >= 0)) {

    var server = headerValue(res, "Server");  

    ctx.alert("vinfo-http-trace", req, res, {
      message: "HTTP Trace Detected",
      output: res.bodyAsString,
      key: "vinfo-http-trace:"+server,
      resource: server
    });
  }
}

function headerValue(res, name) {
  var hdr = res.getFirstHeader(name);
  if (hdr) return hdr.value
  else return "";
}


