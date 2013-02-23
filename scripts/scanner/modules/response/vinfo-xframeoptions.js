var module = {
  name: "X-Frame Options Header Not Set",
  type: "response-processor",
  defaultDisabled: true
};

var valuesRegex = /(DENY|SAMEORIGIN)|(ALLOW-FROM\s*:\s*.+)/i;
function run(request, response, ctx) {
	var uri = String(request.requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");	
	var alert = false;
	
	if (response.bodyAsString.length > 0) {
		if (response.mostlyAscii) {
			if (response.hasHeader("X-Frame-Options")) {
				var hdr = response.getFirstHeader("X-Frame-Options");
				if (!valuesRegex.test(hdr.value)) {
					alert = true;
				}
			} else {
				alert = true;
			}
		}
	}
	
	if (alert) {
		ctx.alert("vinfo-xframeoptions", request, response, {
      output: request.requestLine.uri,
			resource: uripart,
      key: "vinfo-xframe-options" + uripart
		});
	}
}
