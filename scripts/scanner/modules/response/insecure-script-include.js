var module = {
  name: "Insecure Script Include",
  type: "response-processor"
};

function run(request, response, ctx) {
  var found = 0;

  if (response.document) {
    var script = jQuery("script", response.document);
    script.each(function() {
      if (this.getAttribute("src") != null) {
        var scriptsrc = this.getAttribute("src").toLowerCase();
        if (scriptsrc.indexOf("http://") == 0 || (scriptsrc.indexOf("https://") == 0)) {

          var scripthost = parseUri(scriptsrc).host.toLowerCase();

          if ((ctx.isValidInternetDomainName(scripthost)) && (ctx.isValidInternetDomainName(response.host.hostName))) {

            var local = ctx.internetDomainName(response.host.hostName.toLowerCase()).topPrivateDomain().name(); 
            var remote = ctx.internetDomainName(scripthost).topPrivateDomain().name();
          
              if (local != remote) {

                var uristr = String(request.requestLine.uri);
                var uripart = uristr.replace(/\?.*/, "");

                ctx.addStringHighlight(scriptsrc);

                ctx.alert("xs-script-include", request, response, {
                          "resource": uripart,
                          "key": "xs-script-include:" + uripart + ":" + scriptsrc,
    		       	  "output": "Local domain: "+response.host.hostName+"\n"+"Script source: "+scriptsrc
                          });
              }
            } else if (scripthost.toLowerCase() != response.host.hostName.toLowerCase()) {
                var uristr = String(request.requestLine.uri);
                var uripart = uristr.replace(/\?.*/, "");

                ctx.addStringHighlight(scriptsrc);

                ctx.alert("xs-script-include", request, response, {
                          "resource": uripart,
                          "key": "xs-script-include:" + uripart + ":" + scriptsrc,
                          "output": "Local domain: "+response.host.hostName+"\n"+"Script source: "+scriptsrc
                          });
            }
        }
        if ((response.host.schemeName == "https") && (scriptsrc.indexOf("http://") == 0)) {
         var uristr = String(request.requestLine.uri);
         var uripart = uristr.replace(/\?.*/, "");

         ctx.addStringHighlight(scriptsrc);

         ctx.alert("insecure-script-include", request, response, {
                   "resource": uripart,
                   "key": "insecure-script-include:" + uripart + ":" + scriptsrc,
		   "output": "Script source: "+scriptsrc
                  });
       }

      }
    });
  }
}

