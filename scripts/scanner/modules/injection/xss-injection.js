var module = {
  name: "XSS Injection checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  var ps = ctx.getPathState();
  ctx.submitRequest(createReq0(ctx, ps), process, 0);
  ctx.submitRequest(createReq1(ctx, ps), process, 1);
  ctx.submitRequest(createReq2(ctx, ps), process, 2);
}

function createReq0(ctx, ps) {
  var xid = ps.allocateXssId();
  var tag = ps.createXssTag(xid);
  var req = ps.createAlteredRequest(tag, true);
  req.addHeader("Referer", tag);
  ps.registerXssRequest(req, xid);
  return req;
}

function createReq1(ctx, ps) {
  var xid = ps.allocateXssId();
  var tag = ps.createXssTag(".htaccess.aspx", xid);
  var req = ps.createAlteredRequest(tag, true);
  ps.registerXssRequest(req, xid);
  return req;
}

function createReq2(ctx, ps) {
  var xid = ps.allocateXssId();
  var tag = ps.createXssTag("</textarea>", xid);
  var req = ps.createAlteredRequest(tag, true);
  ps.registerXssRequest(req, xid);
  return req;
}

function process(req, res, ctx) {
  ctx.contentChecks(req, res);
}
