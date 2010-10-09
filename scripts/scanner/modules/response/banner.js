var module = {
  name : "HTTP Banner Module",
  type: "response-processor"
};

function run() {
  var banner = response.header("Server");
  var uri=new URI(this.httpRequest.getRequestLine().getUri());
  var param_prefix = uri.getScheme() + "." + uri.getHost()  + "." +  uri.getPort();
  var host = uri.getHost();

  if(!banner)
	  return;
 
  if(!model.get(param_prefix+ ".server.banner")) {
    model.set(param_prefix +".server.banner", banner);
    model.alert("banner", { "output": banner, "resource": host });
  }
}
