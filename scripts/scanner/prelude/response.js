importPackage(org.apache.http.util)

var httpResponse = null;
var wrappedResponse = null;

__defineGetter__("response", function() {
    if(!httpResponse)
      return null;
    return (wrappedResponse || (wrappedResponse = new Response(httpResponse)));
});

function Response(httpResponse) {
  this.httpResponse = httpResponse;
  this.entityString = null;
}

Response.prototype.header = function(name) {
  var hdr = this.httpResponse.getFirstHeader(name);
  return (hdr && hdr.value);
}

Response.prototype.consumeContent = function() {
  if(this.httpResponse.entity)
    this.httpResponse.entity.consumeContent();
}

Response.prototype.__defineGetter__("bodyAsString", function() {
    var e = this.httpResponse.entity;
    if(!e) {
      print("e is null :(");
      return null; /* or "" ? */
    }
    if(this.entityString) {
      print("returning cached :"+ this.entityString);
      return this.entityString;
    }
    this.entityString = EntityUtils.toString(e);
    print("returning calculated: "+ this.entityString);
    return this.entityString;
    /* XXX exceptions */
    //return (this.entityString || (this.entityString = EntityUtils.toString(e)));
});

Response.prototype.__defineGetter__("code", function() {
    return this.httpResponse.statusLine.statusCode;
});




