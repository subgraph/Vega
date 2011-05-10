var module = {
	name: "__EVENTVALIDATION detector",
	type: "response-processor"
};

function run(request, response, ctx) {
	var z;
	var x;
	var c;
	var v;
	var form = jQuery("form",response.document);

	form.children().each(function (){
		if ((this.getAttribute("name") != null) && (this.getAttribute("name") == "__VIEWSTATE")) {
			z = 1;
		}
	});
	form.children().each(function (){
		if ((this.getAttribute("name") != null) && (this.getAttribute("name") == "__EVENTVALIDATION")) {
			x = 1;
		}
	});

	form.children().each(function (){
		if ((this.getAttribute("id") != null) && (this.getAttribute("id") == "__VIEWSTATE")) {
			c = 1;
		}
	});
	form.children().each(function (){
		if ((this.getAttribute("id") != null) && (this.getAttribute("id") == "__EVENTVALIDATION")) {
			v = 1;
		}
	});


	if (z&&x|c&&v) {
		ctx.alert("vinfo-validation",request, response, {
			"output": response.bodyAsString, 
			"resource": request.requestLine.uri,
			key: "vinfo-validation" + request.requestLine.uri,
			response: response 
		} );
	}
}
