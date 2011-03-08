importPackage(org.apache.http.util);

var httpResponse = null;
var wrappedResponse = null;

__defineGetter__("response", function() {
     if(!httpResponse) {
    	 print("no http response");
      return null;
	}
    return (wrappedResponse || (wrappedResponse = new Response(httpResponse)));
});

function Response(httpResponse) {
  this.httpResponse = httpResponse;
  this.rawResponse = httpResponse.rawResponse;
	this.rawRequest = httpResponse.originalRequest;
	this.host = httpResponse.host;
  this.entityString = null;
  this.cachedDocument = null;
}

Response.prototype.header = function(name) {
  var hdr = this.rawResponse.getFirstHeader(name);
  return (hdr && hdr.value);
};

Response.prototype.consumeContent = function() {
  if(this.rawResponse.entity)
    this.rawResponse.entity.consumeContent();
};

Response.prototype.__defineGetter__("bodyAsString", function() {
	return this.entityString || (this.entityString = this.httpResponse.getBodyAsString());
});

Response.prototype.__defineGetter__("allHeaders", function() {
	return this.rawResponse.allHeaders;
});

Response.prototype.headers = function(name) {
	return this.rawResponse.getHeaders(name);
};

Response.prototype.__defineGetter__("document", function()  {
	if(this.cachedDocument)
		return this.cachedDocument;
	
	var html = this.httpResponse.getParsedHTML();
	if(!html || !html.getDOMDocument())
		return null;
	
	return (this.cachedDocument = new HTMLDocument(html.getDOMDocument()));
	
});

Response.prototype.__defineGetter__("code", function() {
    return this.rawResponse.statusLine.statusCode;
});




