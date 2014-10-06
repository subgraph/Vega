var module = {
  name: "Cleartext Password Over HTTP",
  type: "response-processor"
};

function run(request, response, ctx) {

  if (response.document) {
    var form = jQuery("form", response.document);
    
    form.each(function() {
      if (((this.getAttribute("action") == null) && (response.host.schemeName != "https")) || 
          ((this.getAttribute("action") != null) && (this.getAttribute("action").toLowerCase().indexOf("https") != 0) && (response.host.schemeName != "https")) ||
          ((this.getAttribute("action") != null) && (this.getAttribute("action").toLowerCase().indexOf("http:") >= 0) && (response.host.schemeName == "https"))) {
        $(this).find("input").each(function() {
	  if ((this.getAttribute("type") != null) && (this.getAttribute("type") == "password")) {
            var parent = $(this).closest("form");
            var parentName = "";
            var uristr = String(request.requestLine.uri);
	    var uripart = uristr.replace(/\?.*/, "");
            if ($(parent).get(0).getAttribute("name") != null) {
              parentName = $(parent).get(0).getAttribute("name");
              var escaped = parentName.replace(/([\\\(\[\{\^\$\|\(\]\}\?\*\+\.\/])/g, "\\$1");
              ctx.addRegexCaseInsensitiveHighlight("name=[\"']*"+escaped+"[\"']*");
            }
            if ($(parent).get(0).getAttribute("action") != null) {
              formAction = $(parent).get(0).getAttribute("action");
              var escaped = formAction.replace(/([\\\(\[\{\^\$\|\(\]\}\?\*\+\.\/])/g, "\\$1");
              ctx.addRegexCaseInsensitiveHighlight("action=([\"'])*"+escaped+"[\"']*");
            }
	    ctx.alert("vauthhttp", request, response, {
      	              "resource": uripart,
                      "key": "vauthhttp:" + uripart + ":" + parentName
                      });
          }              
        });
       }
    });
  }
}
