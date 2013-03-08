var module = {
    name: "Cross Domain Policy Auditor",
    category: "Injection Modules"
};

function initialize(ctx) {
  var ps = ctx.getPathState();
  if (ps.isRootPath() || !ps.isParametric()) {
    ctx.submitAlteredRequest(process, "crossdomain.xml", false, 0);
  }
}

function process(req, res, ctx) {
  ctx.responseChecks(req, res);
}


