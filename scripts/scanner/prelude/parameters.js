
function Parameters(parameters)
{
	this.parameters = parameters;
}

Parameters.prototype.__defineGetter__("hasParameters", function() {
	return this.parameters.hasParameters();
});

Parameters.prototype.__defineGetter__("parameterLists", function() {
	var 
		lists = this.parameters.getParameterLists(),
		that = this,
		pair_creator = function(elem) {
			return {
				name : elem.name,
				value: elem.value
			};
		};

	return this.i2a(lists, function(list_elem) {
		return that.i2a(list_elem, pair_creator);
	});
});

Parameters.prototype.__defineGetter__("parameterNameLists", function() {
	var
		lists = this.parameters.getParameterNameLists(),
		that = this;

	return this.i2a(lists, function(elem) {
		return that.i2a(elem);
	});
});

Parameters.prototype.__defineGetter__("parameterNames", function() {
		return this.i2a(this.parameters.getParameterNames());
});

Parameters.prototype.getValuesFor = function(parameter_name) {
	return this.i2a(this.parameters.getValuesForParameter(parameter_name));
}

Parameters.prototype.i2a = function(iterable, f)
{
	var
		it = iterable.iterator(),
		a = [],
		elem = null;

	if(!f) 
		f = function(x) { return x; }

	while(it.hasNext()) {
		elem = it.next();
		a.push( f(elem) );
	}
	return a;
}

