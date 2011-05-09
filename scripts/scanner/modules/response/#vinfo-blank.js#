var module = {
    name : "Empty Reponse Body Module",
    type: "response-processor"
};

function run()
{

  if (response.bodyAsString == "")
  {

    var i = 0;
    var found = 0;

    for(i=0; i < response.allHeaders.length; i++)
    {
     if (response.allHeaders[i].name.toLowerCase() == "location")
       found = 1;
    }
    if (!found)
    {
      if ((response.code != "401") && (response.code != "304")) {
        model.alert("vinfo-blank", {"output": "n/a", "resource": httpRequest.requestLine.uri, response: response } );
      }
    }
  }
}
