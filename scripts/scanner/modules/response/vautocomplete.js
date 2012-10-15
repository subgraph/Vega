var module = {
  name: "Form autocomplete",
  type: "response-processor"
};

function run(request, response, ctx) {
  var found = 0;

  if (response.document) {

    var form = jQuery("form", response.document);
    form.children().each(function() {
      if ((this.getAttribute("type") != null) && (this.getAttribute("type") == "password")) {
        if ((this.getAttribute("autocomplete") == null) || (this.getAttribute("autocomplete").toLowerCase() != "off")) {
          ctx.addStringHighlight("type=\"password\""); // FIXME: Unoptimal, Selecting the form as a string without cloning the whole DOM would be nice.
          found++;
        }
      }
    });
  }

  if (found) {
    var match;
    ctx.addStringHighlight(request.requestLine.uri);
    var sub = request.requestLine.uri;
    var index = sub.indexOf('?');

    if (index >= 0) {
      sub = sub.substring(0, index);
    }

    (found > 1) ? match = "instances" : match = "instance";
    ctx.alert("vautocomplete", request, response, {
      "resource": request.requestLine.uri,
      key: "vautocomplete:" + sub
    });

  }
}
