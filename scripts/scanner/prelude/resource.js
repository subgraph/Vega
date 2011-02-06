var path = null;
var wrappedPath = null;

__defineGetter__("resource", function() {
		if(!path)
			return null;
		return (wrappedPath || (wrappedPath = new Resource(path)));
});

function Resource(path) {
	this.path = path;
	this.getParameters = new Parameters(path.getParameters);
	this.postParameters = new Parameters(path.postParameters);
}

Resource.prototype.__defineGetter__("hasGetParameters", function() {
	return this.path.getParameters.hasParameters();
});

Resource.prototype.__defineGetter__("hasPostParameters", function() {
	return this.path.postParameters.hasParameters();
});


Resource.prototype.buildUriFromParameters = function(params) {
	var 
		query = params.map(function(p) { if(!p.value) return p.name; else return p.name +"="+ p.value; }).join("&"),
	  u = this.path.uri;
	return new java.net.URI(u.scheme, u.authority, u.path, query, null);
};
