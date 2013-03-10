var module = {
  name: "File Upload Detection",
  type: "response-processor"
};

function run(request, response, ctx) {
  var found = 0;

  if (response.document) {
    var input = jQuery("input:file", response.document);
    input.each(function() {
    	found++;
    });
  }

  if (found) {
    var match;
    var sub = request.requestLine.uri;
    var index = sub.indexOf('?');

    if (index >= 0) {
      sub = sub.substring(0, index);
    }

    ctx.addRegexCaseInsensitiveHighlight("type=[\"']*file[\"']*");

    (found > 1) ? match = "instances" : match = "instance";

    ctx.alert("vfileupload", request, response, {
              "resource": sub,
              "key": "vfileupload" + sub
    });

  }
}
