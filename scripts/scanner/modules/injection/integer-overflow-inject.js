var module = {
  name: "Integer Overflow Injection Checks",
  category: "Injection Modules",
  differential: true,
  defaultDisabled: true
};

function initialize(ctx) {
  ctx.submitMultipleAlteredRequests(process, ["-0000012345", "-2147483649", "-2147483648", "0000012345", "2147483647", "2147483648", "4294967295", "4294967296", "0000023456"]);
}

function process(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;

  if (res.fetchFail) {
    ctx.error(req, res, "During integer overflow injection checks");
    ctx.setModuleFailed();
    return;
  }

  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 9) return;

  if (!ctx.isFingerprintMatch(3, 8)) return;

  if (!ctx.isFingerprintMatch(0, 1)) {
    ctx.alert("vinfo-integer-overflow", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      message: "Response to -(2^31-1) different than to -12345",
      resource: ctx.getSavedRequest(1).requestLine.uri
    });
    ctx.responseChecks(1);
  }

  if (!ctx.isFingerprintMatch(0, 2)) {
    ctx.alert("vinfo-integer-overflow", ctx.getSavedRequest(2), ctx.getSavedResponse(2), {
      message: "Response to -2^31 different than to -12345",
      resource: ctx.getSavedRequest(2).requestLine.uri
    });
    ctx.responseChecks(2);
  }

  if (!ctx.isFingerprintMatch(3, 4)) {
    ctx.alert("vinfo-integer-overflow", ctx.getSavedRequest(4), ctx.getSavedResponse(4), {
      message: "Response to 2^31-1 different than to 12345",
      resource: ctx.getSavedRequest(4).requestLine.uri
    });
    ctx.responseChecks(4);
  }

  if (!ctx.isFingerprintMatch(3, 5)) {
    ctx.alert("vinfo-integer-overflow", ctx.getSavedRequest(5), ctx.getSavedResponse(5), {
      message: "Response to 2^31 different than to 12345",
      resource: ctx.getSavedRequest(5).requestLine.uri
    });
    ctx.responseChecks(5);
  }

  if (!ctx.isFingerprintMatch(3, 6)) {
    ctx.alert("vinfo-integer-overflow", ctx.getSavedRequest(6), ctx.getSavedResponse(6), {
      message: "Response to 2^32-1 different than to 12345",
      resource: ctx.getSavedRequest(6).requestLine.uri
    });
    ctx.responseChecks(6);
  }

  if (!ctx.isFingerprintMatch(3, 7)) {
    ctx.alert("vinfo-integer-overflow", ctx.getSavedRequest(7), ctx.getSavedResponse(7), {
      message: "Response to 2^32 different than to 12345",
      resource: ctx.getSavedRequest(7).requestLine.uri
    });
    ctx.responseChecks(7);
  }
}
