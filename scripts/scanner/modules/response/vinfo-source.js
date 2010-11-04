var module = {
  name : "Source Code Disclosure Module",
  type: "response-processor"
};


var xmlReject = /<?\s*xml/i;

function runTest(test) {
	var output = "";
	for(var i in test.regex) {
		var regex = test.regex[i];
		var res = regex.exec(response.bodyAsString);
		if(res) {
			print("Searching: "+ res[0]);
			if(!xmlReject.test(res[0])) {
				print("not found");
				output += res[0];
			} else {
				print("found");
			}
		}
	}
	if(output.length > 0)
		return output;
	else 
		return null;
}

function run() {
	var testData = [ 
	  {
		  type: "ASP or JSP",
		  regex: [
		    /<!--\s*%[\s\S]+%\s*(--)?>/g,
		    /<%(?:(?!%>)[\s\S])+%>/g
		  ]
	  },
	  {
		  type: "PHP",
		  regex: [
		    /<!--\s*\?[\s\S]+\?\s*(--)?>/g,
		    /<\?(?:(?!\?>)[\s\S])+\?>/g
		  ]
	  },
	  {
		  type: "JSP Tag",
		  regex: [
		    /<jsp:.+\s+(?:(?!\/>)[\s\S])+\/>/g,
		    /<jsp:.+>(?:(:!<\/jsp:)[\s\S])<\/jsp:[^>]*>/g
		  ]
	  }
	];

	var output = "";

	for(var idx in testData) {
		var result = runTest(testData[idx]);
		if(result) {
			output += ("Possible "+ testData[idx].type +" code: \n"+ result);
		}
	}

	if(output.length > 0) {
    model.alert("vinfo-source", {"output": output, "resource": httpRequest.requestLine.uri} );
	}
}
