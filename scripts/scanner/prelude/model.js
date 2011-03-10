var workspace = null;
var wrappedModel = null;

__defineGetter__("model", function() {
    if(!workspace)
      return null;
    return (wrappedModel || (wrappedModel = new Model(workspace)));
});

function Model(scanModel) {
  this.workspace = workspace;
  this.alertModel = workspace.scanAlertModel;
	this.requestLog = workspace.requestLog;
}

Model.prototype.get = function(name) {
  return this.workspace.getProperty(name);
};

Model.prototype.set = function(name, value) {
  this.workspace.setProperty(name, value);
};

Model.prototype.alert = function(type, vars) {
	var requestId = -1;
	try {
		this.alertModel.lock();
		if(vars.key && this.alertModel.hasAlertKey(vars.key))
			return;
		if(vars.response) {
			requestId = this.requestLog.addRequestResponse(vars.response.rawRequest, vars.response.rawResponse, vars.response.host);
		}
		var alert = this.alertModel.createAlert(type, vars.key, requestId);
		if(!alert)
			throw new Error("Could not locate an alert template with name '"+ type +"'.");
		for(var name in vars) {
			if(name != "response" && name != "key") {
				var value = vars[name];
				if(value)
					alert.setProperty(name, value);
			}
		}
		this.alertModel.addAlert(alert);
	} finally {
		this.alertModel.unlock();
	}
};
