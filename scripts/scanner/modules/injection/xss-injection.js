var module = {
  name: "XSS Injection checks",
  category: "Injection Modules"
};

var payloads = ["", ".htaccess.aspx", "</textarea>", "\" src=", " src=", "\n ", "*/ ", "' ", 
                "javascript:", "vbscript:", "\" onMouseOver=", "\" style=", "' onMouseOver=", 
                "' style=", "\" ", "\' "];

function createRequest(ps, index) {
  if (index == 0) {
    var xid = ps.allocateXssId();
    var tag = ps.createXssTag(xid);
    var req = ps.createAlteredRequest(tag, true);
    req.addHeader("Referer", tag);
    ps.registerXssRequest(req, xid);
    return req;
  } else {
    var xid = ps.allocateXssId();
    var tag = ps.createXssTag(payloads[index], xid);
    var req = ps.createAlteredRequest(tag, true);
    ps.registerXssRequest(req, xid);
    return req;
  }
};

function initialize(ctx) {
  var ps = ctx.getPathState();

  if (ps.isParametric()) {  
    for (var i = 0; i < payloads.length; i++) {
      ctx.submitRequest(createRequest(ps, i), process, i);      
    }
  }
};

function process(req, res, ctx) {
  ctx.contentChecks(req, res);
};
