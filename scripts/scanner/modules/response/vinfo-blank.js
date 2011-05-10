var module = {
    name : "Empty Reponse Body Module",
    type: "response-processor"
};

function run(request, response, ctx)
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
        ctx.alert("vinfo-blank",request, response, {
        	output: "n/a", 
        	resource: request.requestLine.uri, 
        	response: response,
        	key: "vinfo-blank" + request.requestLine.uri
        	} );
      }
    }
  }
}
