var module = {
	name: "Cookie Scope Detection",
	type: "response-processor"
};

var ipRegex = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;

function run(request, response, ctx) {
	var uri = String(request.requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");
	var host = response.host;
	var cookies = response.cookies;
	for (var i = 0; i < cookies.length; i++) {
		var tooLiberal = false;
		if (cookies[i].domain) {
			var domain = String(cookies[i].domain);
			if (!ipRegex.test(response.host)) {
				if (domain !== host.hostName) {
					var hostArray = String(host.hostName).split(".");
					var domainArray = domain.split(".");
					// Domain: .bar.example.com vs. Host: foo.bar.example.com = too liberal
					if (domainArray.length < hostArray.length) {
						tooLiberal = true;			
					// Domain: .example.com vs. Host: www.example.com = too liberal
					} else if (domainArray.length === hostArray.length && domainArray.shift() === "") {
						tooLiberal = true;
					} else {
						tooLiberal = false;
					}
				}
			}
		}
		if (tooLiberal) {
                        var uripart = uri.replace(/\?.*/, "");
			ctx.addRegexCaseInsensitiveHighlight("Domain=[\"']*"+String(cookies[i].domain)+"[\"']*");
			ctx.alert("vinfo-cookie-scope", request, response, {
				"output": cookies[i].getHeader(),
				key: "vinfo-cookie-scope" +  uri.host + cookies[i].getName() + cookies[i].getDomain() + cookies[i].getPath(),
				resource: uripart
			});
		}
		
	}
}

