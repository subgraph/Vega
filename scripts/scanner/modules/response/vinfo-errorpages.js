var module = {
  name: "Error Page Detection",
  type: "response-processor"
};

var errorStrings = [
	{ 
		type: "java",
		regex: "<title>\s*Apache Tomcat.+Error Report\s*</title>",
		responseCode: [500]
	},
	{
		type: "java",
		regex: "java.lang.Exception",
	},
	{ 
		type: "cf",
		regex: "<title>\s*Error Occurred While Processing Request\s*</title>"
	},
	{
		type: "php",
		regex: "<b>Warning</b>: .+</b> on line <b>"
	},
	{ 
		type: "php",
		regex: "<b>Fatal error</b>: .+</b> on line <b>"
	},
	{
		type: "asp",
		regex: "<span><H1>Server Error in '/[/A-Za-z_0-9\-]*' Application.<hr width=100% size=1 color=silver></H1>"
	},
	{
	  type: "asp",
	  regex: "<title>401 - Unauthorized: Access is denied due to invalid credentials.</title>",
	  responseCode: [401]
	},
	{
	  type: "asp",
	  regex: "System.UnauthorizedAccessException: Access to the path",
	},
	{
		type: "django",
		regex: "t = loader.get_template.+# You need to create a \\d{3}.html template."
	},
	{
		type: "django",
		regex: "PythonHandler django.core.handlers.modpython"
	},
	{
		type: "ruby",
		regex: "<h1 class=\"error_title\">Ruby.+application could not be started</h1>"
	},
	{
		type: "http",
		regex: "<TITLE>500 Internal Server Error</TITLE>",
		responseCode: [500]
	},
	{ 
		type: "http",
		regex: "<TITLE>401 Authorization Required</TITLE>",
		responseCode: [401]
	},
	{
	  type: "http",
	  regex: "<h1>HTTP Status 401 - </h1>",
	  responseCode: [401]
	},
	{ 
		type: "http", 
		regex: "<TITLE>403 Forbidden</TITLE>",
		responseCode: [403]
	}
];

function run(request, response, ctx) {
	var uri = String(request.requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");
	var responseCodeMatch = false;
	for (var i = 0; i < errorStrings.length; i++) {
		if (!errorStrings[i].responseCode) {
			responseCodeMatch = true;
		} else {
			if (errorStrings[i].responseCode.indexOf(response.code) >= 0) {
				responseCodeMatch = true;
			}
		}
		
		var match = RegExp(errorStrings[i].regex, "im").exec(response.bodyAsString);
		if (match && responseCodeMatch) {		
			ctx.addRegexCaseInsensitiveHighlight(errorStrings[i].regex);
			ctx.alert("vinfo-errorpages-" + errorStrings[i].type, request, response, {
				output: match[0],
				resource: uripart,
				key: "vinfo-errorpages" + errorStrings[i].type + uripart + match[0]
			});
		}
	}
};
