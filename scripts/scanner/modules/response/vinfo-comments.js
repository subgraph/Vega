var module = {
	name : "HTML Interesting Comments Detection",
	type: "response-processor",
	disabled: true
};

var res;
var x = new Array();
var output = "";

function run() {
	var comments = ["user", "pass", "bug", "fix", "hack", "caution", "account", "bypass", "login", "todo", "warning", "note", "admin", "backdoor", "config"];
	for (i=0;i<=comments.length-1;i+=1) {
		var current = new RegExp("(<!--(?:(?!-->)[\\s\\S])*" + comments[i] + "[\\s\\S]*?-->)", "ig");
		x = current.exec(response.bodyAsString);
		if (x) {
			output += x[1];
			output += "\n";
			res = 1;
		}
	}
	if(res) {
		model.alert("vinfo-comments", {"output": output, "resource": httpRequest.requestLine.uri} );
	}
}
