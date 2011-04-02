var module = {
  name: "HTTP Header Injection checks",
  category: "Injection Modules"
};

function initialize(ctx)
{
	ctx.submitMultipleAlteredRequests(process, ["bogus\nVega-Inject:bogus", "bogus\rVega-Inject:bogus"], true);
}

function process(req, res, ctx)
{
	if(res.getRawResponse().containsHeader("Vega-Inject")) {
		ctx.publishAlert("vinfo-header-inject", "Injected Vega-Inject header into response", request, response);
	}
}
