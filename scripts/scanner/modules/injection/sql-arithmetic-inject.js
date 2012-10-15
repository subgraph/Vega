var module = {
  name: "Blind SQL Injection Arithmetic Evaluation Differential Checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  var numeric = isNumericParameter(ctx.getPathState());
  if (numeric) {
    ctx.submitAlteredRequest(process, "-0", true, 0);
    ctx.submitAlteredRequest(process, "-0-0", true, 1);
    ctx.submitAlteredRequest(process, "-0-9", true, 2);
  } else {
    ctx.submitAlteredRequest(process, "9-8", 0);
    ctx.submitAlteredRequest(process, "8-7", 1);
    ctx.submitAlteredRequest(process, "9-1", 2);
  }
  submit(ctx, 3, "\\\'\\\"");
  submit(ctx, 4, "\'\"");
  submit(ctx, 5, "\\\\\'\\\\\"");

  if (numeric) {
    ctx.submitAlteredRequest(process, " - 0 - 0", true, 6);
    ctx.submitAlteredRequest(process, " 0 0 - -", true, 7);
  } else {
    ctx.submitAlteredRequest(process, "9 - 1", 6);
    ctx.submitAlteredRequest(process, "9 1 -", 7);
  }

}

function submit(ctx, idx, val) {
  var req = ctx.getPathState().createAlteredRequest(val, true);
  var s1 = "vega" + val;
  var s2 = s1 + ",en";
  req.addHeader("User-Agent", s1);
  req.addHeader("Referer", s1);
  req.addHeader("Accept-Language", s2);
  ctx.submitRequest(req, process, idx);
}


function isNumericParameter(ps) {
  if (!ps.isParametric()) return false;
  var p = ps.getFuzzableParameter();
  if (!(p && p.value)) return false;
  var v = p.value;
  var numchars = "01234567890.+-";
  for (var i = 0; i < v.length; i++) {
    if (numchars.indexOf(v[i]) == -1) return false;
  }
  return true;

}

function process(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;
  var ps = ctx.getPathState();

  if (res.fetchFail) {
    ctx.error(req, res, "During SQL injection checks");
    ctx.setModuleFailed();
    return;
  }

  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 8) return;

  if (ctx.isFingerprintMatch(0, 1) && !ctx.isFingerprintMatch(0, 2)) {
	var uri = String(ctx.getSavedRequest(0).requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(0), ctx.getSavedResponse(0), {
      output: ctx.getSavedResponse(0).bodyAsString,
      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Blind Arithmetic Evaluation Differential"

    });

    ctx.responseChecks(0);
    ctx.responseChecks(2);
  }
  if (ctx.isFingerprintMatch(1, 6) && !ctx.isFingerprintMatch(6, 7)) {
	var uri = String(ctx.getSavedRequest(7).requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(7), ctx.getSavedResponse(7), {
      output: ctx.getSavedResponse(7).bodyAsString,
      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Blind Arithmetic Evaluation Differential"
    });
    ctx.responseChecks(6);
    ctx.responseChecks(7);
  }

  if (!ctx.isFingerprintMatch(3, 4) && !ctx.isFingerprintMatch(3, 5)) {
	var uri = String(ctx.getSavedRequest(4).requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");


    ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(4), ctx.getSavedResponse(4), {
      output: ctx.getSavedResponse(4).bodyAsString,
      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Blind Arithmetic Evaluation Differential"
    });

    ctx.responseChecks(3);
    ctx.responseChecks(4);
  }
}
