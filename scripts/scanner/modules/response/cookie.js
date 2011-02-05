importPackage(java.net);

var module = {
  name : "Cookie security module",
  type: "response-processor"
};

function run() {
  var serverHeaders=new Array();
  serverHeaders= response.headers("Set-cookie");
  var uri=new URI(this.httpRequest.getRequestLine().getUri());
  print("Testing cookie security on ressource: " + this.httpRequest.getRequestLine().getUri());
  if(!serverHeaders)
	  return;
  
  for(x in serverHeaders){
	  print(serverHeaders[x].getValue());
	  var cookie=new Array();
	  cookie=serverHeaders[x].getValue().split("; ");
	  var httponly=false;
	  var secure=false;
	  var cookieproperly=new Array();
	  cookieproperly=cookie[0].split("=");
	  var name=cookieproperly[0];
	  var value=cookieproperly[1];
	  print("name : "+ name);
	  print("value: "+ value);

	  for(y in cookie){
		  print(cookie[y]);
		  if(cookie[y].match(".*httponly.*")){
			  httponly=true;
		  }
		  if(cookie[y].match(".*secure.*")){
			  secure=true;
		  }
	  }
	  var param_prefix = uri.getScheme() + "." + uri.getHost  + "." +  uri.getPort  + name;
	  if(httponly!=true){
		if(!model.get( param_prefix + ".cookie.httponly")){
			model.alert("cookie-httponly", { "output": serverHeaders[x], "resource": this.httpRequest.getRequestLine().getUri() });
		  	model.set(param_prefix+".cookie.httponly", "false");
	  	}
	  }
	  
	  if(uri.getScheme().match("https") && secure!=true){
		if(!model.get(param_prefix + ".cookie.secure.")){
			model.alert("cookie-secure", { "output": serverHeaders[x], "resource": this.httpRequest.getRequestLine().getUri() });		  
			model.set(param_prefix + ".cookie.secure.", "false");
		}
	  }

  }
  
}
