var module = {
  name : "XSS",
  type: "per-resource"
};

var tag = 'VEGAVEGAVEGA';

function generateValues() {
	var vs = [
		'<ScripT>'+tag+'();</ScripT>'
	];
	for(var i = 0; i < vs.length; i++) yield vs[i];
}

function processResponses(response, resource, params)
{
	var doc = response.document;
	if(doc && $("script:contains('"+ tag +"')", doc).length > 0) {
		print("Found XSS in "+ resource.path.uri +" with request: "+ resource.buildUriFromParameters(params));
		print("Output: ["+ response.bodyAsString +"]");
		
	}
}

function run() {
	if(resource && resource.hasGetParameters) 
		fuzzGetParameters(resource, generateValues, processResponses);
}
