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
    var e = this.rawResponse.entity;
    if(!e) {
      return null; /* or "" ? */
    }
    if(this.entityString) {
      return this.entityString;
    }
    this.entityString = EntityUtils.toString(e);
    return this.entityString;
    /* XXX exceptions */
    //return (this.entityString || (this.entityString = EntityUtils.toString(e)));
});

Response.prototype.__defineGetter__("allHeaders", function() {
	return this.rawResponse.allHeaders;
});

Response.prototype.headers = function(name) {
	return this.rawResponse.getHeaders(name);
};

Response.prototype.__defineGetter__("document", function()  {
	if(!this.httpResponse.getDocumentDOM()) {		
		return null;
	}
	if(!this.cachedDocument)
		this.cachedDocument = new Document(this.httpResponse.getDocumentDOM());
	return this.cachedDocument;
});

Response.prototype.__defineGetter__("code", function() {
    return this.rawResponse.statusLine.statusCode;
});




