var module = {
	name: "Unsafe Or Unrecognized Character Set",
	type: "response-processor"
};

var textSubtypeRegex = /^text\/\w+/;
var charsetHeaderRegex = /^text\/\w+;.+charset\s*=['"]?([^'";]+)['"]?/i;
var charsetBodyRegex = /<meta.+charset\s*=['"]?([^'"]+)['"]?.*>/im;

var safeCharsets = ["utf-8",
                    "iso-8859-1",
                    "iso-8859-2",
                    "iso8859-1",
                    "iso8859-2",
                    "iso8859-15",
                    "iso8859-16",
                    "iso-8859-15",
                    "iso-8859-16",
                    "windows-1252",
                    "windows-1250",
                    "us-ascii",
                    "koi8-r"];

function run(request, response, ctx) {
	var uri = String(request.requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");
	
	if (response.bodyAsString.length > 0) {
		if (response.hasHeader("Content-Type")) {
			var hdr = response.getFirstHeader("Content-Type");
	
			var matchHeader = charsetHeaderRegex.exec(hdr.value);
			if (matchHeader) {
				if (safeCharsets.indexOf(matchHeader[1].toLowerCase()) == -1) {
					unsafeHeader = true;
					ctx.addStringHighlight(matchHeader[1]);
					ctx.alert("vinfo-unsafe-charset-header", request, response, {
						output: hdr.name + ": " + hdr.value,
						resource: uripart,
						key: "vinfo-unsafe-charset-header:" + uripart
					});
				}
			}
	
			var matchBody = charsetBodyRegex.exec(response.bodyAsString);
			if (matchBody) {
				if (safeCharsets.indexOf(matchBody[1].toLowerCase()) == -1) {
					unsafeBody = false;
					ctx.addStringHighlight(matchBody[1]);
					ctx.alert("vinfo-unsafe-charset-body", request, response, {
						output: matchBody[0],
						resource: uripart,
						key: "vinfo-unsafe-charset-body:" + uripart
					});				
				}
			}
		}
	}
}
		