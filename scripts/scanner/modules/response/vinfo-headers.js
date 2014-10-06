var module = {
  name: "HTTP Header Checks",
  type: "response-processor"
};

var headers = ["X-XSS-Protection", "Access-Control-Allow-Origin"]; // May be an issue when these headers are present

function run(request, response, ctx) {
	var uri = String(request.requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");
	
	for (var i = 0; i < headers.length; i++) {
		if (response.hasHeader(headers[i])) {
			var hdr = response.getFirstHeader(headers[i]);
			if (hdr.name == "X-XSS-Protection" && hdr.value == "0") {
				ctx.addStringHighlight(hdr);
				ctx.alert("vinfo-xss-filter-disabled", request, response, {
          				output: hdr,
					resource: uripart,
          				key: "vinfo-xss-filter-disabled" + uripart
        });
			}
			
			if (hdr.name == "Access-Control-Allow-Origin" && hdr.value == "*") {
				ctx.addStringHighlight(hdr);
				ctx.alert("vinfo-insecure-cors-ac", request, response, {
					output: hdr,
					resource: uripart,
					key: "vinfo-insecure-cors-ac" + uripart
				});
			}
		}
	}
}
