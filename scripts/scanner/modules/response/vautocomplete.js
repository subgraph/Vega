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
          // print("match password");
          found++;
        }
      }
    });
  }

  if (found) {
    var match;

    (found > 1) ? match = "instances" : match = "instance";
    ctx.alert("vautocomplete", request, response, {
      "output": found + " " + match + " discovered.",
      "resource": request.requestLine.uri,
      key: "vautocomplete" + request.requestLine.uri + found + " " + match
    });

  }
}
