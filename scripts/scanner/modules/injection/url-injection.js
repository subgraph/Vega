var module = {
  name: "URL Injection checks",
  category: "Injection Modules"
};

function initialize(ctx)
{
	ctx.submitMultipleAlteredRequests(process, ["http://vega.invalid/;?", "//vega.invalid/;?",  "vega://invalid/;?"]);
}

function process(req, res, ctx)
{
	var loc = headerValue(res, "Location");
	if(matchesInjectedUrls(loc)) {
		ctx.publishAlert("vinfo-url-inject", "Injected URL in Location header", request, response);

	}
	var refresh = headerValue(res, "Refresh");
	if(testRefresh(refresh)) {
		ctx.publishAlert("vinfo-url-inject", "Injected URL in Refresh header", request, response);
	}
	ctx.contentChecks(req, res);
}

function testRefresh(refresh) 
{
	if(!refresh)
		return false;
	var ps = refresh.split("=");
	if(ps.length < 2)
		return false;
	var val = ps[1];
	var c = val[0];
	var semiSafe = false;
	if(c == '"' || c == '\'') {
		val = val.substr(1);
		semiSafe = true;
	}
	return (matchesInjectedUrls(val) || match(val, "vega://") || (semiSafe && (val.indexOf(";") != -1)));

}

function headerValue(res, name)
{
	var hdr = res.getRawResponse().getFirstHeader(name);
	if(hdr)
		return hdr.value
	else
		return null;
}

function matchesInjectedUrls(val)
{
	
	return (val && match(val, "http://vega.invalid") && match(val, "//vega.invalid/"));
}

function match(val, str)
{
	return val.indexOf(str) == 0;
}
