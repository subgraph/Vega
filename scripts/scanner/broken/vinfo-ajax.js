var module = {
    name : "AJAX Detector",
    type: "response-processor"
};

function run()
{
  if (response.document)
  {

    var resp = response.bodyAsString;
    var regexp = /<script\b[^>]*?>([\s\S]*?)<\/script>/gim;
    var output = null;
    while(res = regexp.exec(resp))
    {
       matchr = /(eval\(|microsoft\.xmlhttp|activexobject|msxml2\.xmlhttp|xmlhttprequest)/gim;
       if (res[1].match(matchr))
       {
         output = res[1];
       }
    }

  }
  if (output) {
        if (output.length > 200)
        {
          output = output.substr(0,199) + "...";
        }

        model.alert("vinfo-ajax", {"output": output, "resource": httpRequest.requestLine.uri, response: response } );
  }

}
