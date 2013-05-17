var module = {
  name: "Blind OS Command Injection",
  category: "Injection Modules",
  differential: false
};

var detected = false;
var alerted = false;
var average = 0;

var unixMetaChars = ['; ', '" ; ', "' ; ", "| ", '"| ', "'| "]; 
var windowsMetaChars = ['&& ', '& ', '|| '];

var commands = [
  {command: "/bin/sleep 20 ;", os: "Linux/Unix"},
  {command: "/usr/bin/sleep 20 ;", os: "Linux/Unix"},
  {command: "/sbin/sleep 20 ;", os: "Linux/Unix"},
  {command: "ping.exe -n 20 127.0.0.1 > nul & ::", os: "Windows"},
  {command: "timeout 20 & ::", os: "Windows"}
];

var alteredRequests = [];

function initialize(ctx) {
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
		
  var ps = ctx.getPathState();
  
  if (ps.isParametric()) {
    ctx.submitMultipleAlteredRequests(checkTiming, ["vega1", "vega2"], false);    
  }
}

function checkTiming(req, res, ctx) {
  
  if (res.fetchFail) {
    ctx.error(req, res, "During command injection checks");
    ctx.setModuleFailed();
    return;
  }
  
  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 2) return;
  var first = ctx.getSavedResponse(0).milliseconds;
  var second = ctx.getSavedResponse(1).milliseconds;
  average = first + second / 2;
  var firstRequest = alteredRequests.pop();
  ctx.submitAlteredRequest(process, firstRequest.commandString, false, 0);
}

function process(req2, res2, ctx2) {
  
	if (res2.fetchFail) {
    ctx2.error(req, res, "During command injection checks");
    ctx2.setModuleFailed();
    return;
  }
  
	var ps = ctx2.getPathState();
  var currentIndex = ctx2.getCurrentIndex();
  ctx2.addRequestResponse(req2, res2);
  ctx2.incrementResponseCount();

  if ((average < 20000) && (ctx2.getSavedResponse(currentIndex).milliseconds > 20000)) {
    detected = true;
  }

  if (detected && !alerted) {    
    alerted = true;
    var uri = String(req2.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");
    ctx2.alert("vinfo-shell-inject", ctx2.getSavedRequest(currentIndex), ctx2.getSavedResponse(currentIndex), {
      output: res2.bodyAsString,
      key: "vinfo-shell-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: alteredRequests[alteredRequests.length - 1].os + " Blind Timing Analysis Checks",
      param: ps.getFuzzableParameter().name
    }); 
  } else {
  	var nextRequest = alteredRequests.pop();
    if (nextRequest) {
      ctx2.submitAlteredRequest(process, nextRequest.commandString, false, currentIndex + 1);
    }
  }
}


