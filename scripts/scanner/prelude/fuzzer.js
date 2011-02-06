
function ParameterFuzzer(resource, generator)
{
	this.resource = resource;
	this.parameters = resource.getParameters;
}

ParameterFuzzer.prototype = {

createDefaultValues: 

	function(nameList) {
		var ps = this.parameters;
		return nameList.map(function(n) {
			var vs = ps.getValuesFor(n)
			return { name: n, value: (vs[0] || null) };
		});
	},

fuzzGetParameters:

	function(valueGenerator, responseProcessor) {
		for(ps in this.fuzzAllGenerator(valueGenerator)) {
			var 
				uri = this.resource.buildUriFromParameters(ps),
			  response = sendGet(uri);
			responseProcessor(response, resource, ps);
		}
	},

fuzzAllGenerator:

	function(valueGenerator) {
		var 
			nls = this.parameters.parameterNameLists,
		  gen = this.fuzzNameListGenerator;

		for(var i = 0; i < nls.length; i++) {
			for(ps in gen.call(this, valueGenerator, nls[i])) yield ps;
		}
	},

fuzzNameListGenerator:

	function(valueGenerator, nameList) {
		return this.fuzzParameterListGenerator(valueGenerator, this.createDefaultValues(nameList));
	},

fuzzParameterListGenerator:
 
	function(valueGenerator, params) {
		var gen = this.fuzzOneParameterGenerator;
		for(var i = 0; i < params.length; i++) {
			for(ps in gen.call(this, valueGenerator, params, i))
				yield ps;
		}
	},

fuzzOneParameterGenerator: 
	function(valueGenerator, params, idx) {
		var ps = cloneParams(params);
		for(v in valueGenerator()) {
			ps[idx].value = v;
			yield ps;
		}
	}
};

function cloneParams(params) {
		return params.map(function(p) { 
			return { name: p.name, value: p.value } 
		});
}

function printParameterList(params) {
	return params.map(function(p) { 
		return p.name +"="+ p.value;
	}).join(", ");
}

function fuzzGetParameters(resource, valueGenerator, responseCallback)
{
	var fuzzer = new ParameterFuzzer(resource);
	fuzzer.fuzzGetParameters(valueGenerator, responseCallback);
}

