var module = {
	name: "Blind XPath Injection Checks",
	category: "Injection Modules",
	differential: true,
	defaultDisabled: true
};


var commands = [
  {command: "e'", type: "Test"},
  {command: "e' or 1 eq 1 or 'a' = 'a", type: "XPath 2.0"},
  {command: 'e" or 1 eq 1 or "a" = "a', type: "XPath 2.0"},
  {command: "e' or true() or 'a'='a", type: "XPath"},	
  {command: 'e" or true() or "a"="a', type: "XPath"}
];

var alteredRequests = [];

for (var i = 0; i < commands.length; i++) {
	alteredRequests.push(commands[i]);
}

function initialize(ctx) {
	var ps = ctx.getPathState();
	if (ps.isParametric()) {
		ctx.submitMultipleAlteredRequests(process, [alteredRequests[0].command, alteredRequests[1].command], false);
    ps.incrementFuzzCounter();
	}
};

function checkResponse(ctx) {
	var currentIndex = ctx.getCurrentIndex();
  if (!ctx.isFingerprintMatch(currentIndex, ctx.getSavedResponse(0).fingerprint)) {
	  return true;
  } else {
	  return false;
	}
};

function process(req, res, ctx) {
	if (ctx.hasModuleFailed()) return;
	if (res.fetchFail) {
    ctx.error(req, res, "During XPath injection checks");
    ctx.setModuleFailed();
    return;
  }
	
	var ps = ctx.getPathState();
	ctx.addRequestResponse(req, res);
	var currentIndex = ctx.getCurrentIndex();
	
	if (ctx.incrementResponseCount() < 2 || currentIndex < 1) return;
	
	var detected = checkResponse(ctx);
	  
	if (detected) {
		var uri = String(req.requestLine.uri);
		var uripart = uri.replace(/\?.*/, "");
		ctx.alert("vinfo-differential-xpath", ctx.getSavedRequest(currentIndex), ctx.getSavedResponse(currentIndex), {
			output: res.bodyAsString,
			key: "vinfo-differential-xpath:" + uripart + ":" + ps.getFuzzableParameter().name,
			resource: uripart,
			detectiontype: alteredRequests[currentIndex].type + " Blind Injection Differential Checks",
			param: ps.getFuzzableParameter().name
		});
	} else {
		if (currentIndex + 1 < alteredRequests.length) {
			ctx.submitAlteredRequest(process, alteredRequests[currentIndex + 1].command, false, currentIndex + 1);
		}
	};

	if (ctx.allResponsesReceived()) {
		ps.decrementFuzzCounter();
	}
};
