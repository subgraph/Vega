var module = {
  name : "HTTP Banner Module",
  type: "response-processor"
};

function run() {
  var banner = response.header("Server");
  var host = response.header("Host");
  
  if(!banner)
	  return;
 
  if(!model.get("server.banner")) {
    model.set("server.banner", banner);
    model.alert("banner", { "output": banner, "resource": host });
  }
}
