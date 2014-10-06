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
	if (ctx.hasModuleFailed()) return;

  if (res.fetchFail) {
    ctx.error(req, res, "During cross-domain policy auditor checks");
    ctx.setModuleFailed();
    return;
  }
  
  if (ctx.allResponsesReceived()) {
    ctx.responseChecks(req, res);
  }
}


