var module = {
  name: "XML Injection checks",
  category: "Injection Modules"
};

function initialize(ctx)
{
	ctx.submitMultipleAlteredRequests(process, ["vega>'>\"><vega></vega>", "vega>'>\"></vega><vega>"]);
}

function process(req, res, ctx)
{
	if(ctx.hasModuleFailed())
		return;

	if(res.isFetchFail()) {
		ctx.setModuleFailed();
		return;
	}
	ctx.addRequestResponse(req, res);
	if(ctx.incrementResponseCount() < 2)
		return;
	if(!ctx.isFingerprintMatch(0, 1)) {
		ctx.publishAlert("xml-inject", "responses for <vega></vega> and </vega><vega> look different", ctx.getSavedRequest(0), ctx.getSavedResponse(0));
		ctx.responseChecks(1);
	}

}
