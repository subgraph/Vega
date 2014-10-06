var module = {
	name: "Blind OS Command Injection Timing",
	category: "Injection Modules",
	differential: false,
	defaultDisabled: true
};

var unixMetaChars = ['; ', '" ; ', "' ; ", "| ", '"| ', "'| "]; 
var windowsMetaChars = ['', '" '];

var commands = [
  {command: "/bin/sleep 31 ;", os: "Linux/Unix"},
  {command: "ping.exe -n 31 127.0.0.1", os: "Windows"},

];

var alteredRequests = [];

for (var i = 0; i < commands.length; i++) {
	for (var j = 0; j < unixMetaChars.length; j++) {
		if (commands[i].os === "Linux/Unix") { 
			alteredRequests.push({ 
				commandString: unixMetaChars[j] + commands[i].command,
				os: commands[i].os
			});
		}
	}
	for (var k = 0; k < windowsMetaChars.length; k++) {
		if (commands[i].os === "Windows") { 
			alteredRequests.push({ 
				commandString: windowsMetaChars[k] + commands[i].command,
				os: commands[i].os
			});
		}
	}
}

var checkTiming = function(ctx, currentIndex) {
	if (ctx.getSavedResponse(currentIndex).milliseconds > 30000) {
		return true;
	}
	return false;
};

function initialize(ctx) {
	var ps = ctx.getPathState();
	if (ps.isParametric()) {
		ctx.submitAlteredRequest(process, alteredRequests[0].commandString, false, 0);
    ps.incrementFuzzCounter();
	}
};

function process(req, res, ctx) {
	if (ctx.hasModuleFailed()) return;
	if (res.fetchFail) {
    ctx.error(req, res, "During command injection checks");
    ctx.setModuleFailed();
    return;
  }
	
	var ps = ctx.getPathState();
	var currentIndex = ctx.getCurrentIndex();
	ctx.addRequestResponse(req, res);
  ctx.incrementResponseCount();
  var detected = checkTiming(ctx, currentIndex);
  
	if (detected) {
		var uri = String(req.requestLine.uri);
		var uripart = uri.replace(/\?.*/, "");
		ctx.alert("vinfo-shell-inject", ctx.getSavedRequest(currentIndex), ctx.getSavedResponse(currentIndex), {
			output: res.bodyAsString,
			key: "vinfo-shell-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
			resource: uripart,
			detectiontype: alteredRequests[currentIndex].os + " Blind Timing Analysis Checks",
			param: ps.getFuzzableParameter().name
		});
	} else {
		if (currentIndex + 1 < alteredRequests.length) {
			ctx.submitAlteredRequest(process, alteredRequests[currentIndex + 1].commandString, false, currentIndex + 1);
		}
	};

	if (ctx.allResponsesReceived()) {
		ps.decrementFuzzCounter();
	}
};
