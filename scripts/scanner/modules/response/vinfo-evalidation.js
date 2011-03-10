var module = {
	name: "__EVENTVALIDATION detector",
	type: "response-processor"
};

function run() {
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
		model.alert("vinfo-validation", {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri, response: response } );
	}
}
