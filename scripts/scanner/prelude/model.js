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
	this.alertWith(type, null, -1, vars);
};

Model.prototype.alertWith = function(type, key, response, vars) {
	var requestId = -1;
	try {
		this.alertModel.lock();
		if(key && this.alertModel.hasAlertKey(key))
			return;
		if(response) {
			requestId = this.requestLog.addRequestResponse(response.rawRequest, response.rawResponse, response.host);
		}
		var alert = this.alertModel.createAlert(type, key, requestId);
		if(!alert)
			throw new Error("Could not locate an alert template with name '"+ type +"'.");
		for(var name in vars) {
			var value = vars[name];
			if(value)
				alert.setProperty(name, value);
		}
		this.alertModel.addAlert(alert);
	} finally {
		this.alertModel.unlock();
	}
};
