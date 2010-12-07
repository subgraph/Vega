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
}

Model.prototype.get = function(name) {
  return this.workspace.getProperty(name);
};

Model.prototype.set = function(name, value) {
  this.workspace.setProperty(name, value);
};

Model.prototype.alert = function(type, vars) {
  var alert = this.alertModel.createAlert(type);
  if(!alert)
    throw new Error("Could not locate an alert template with name '"+ type +"'.");
  for(var name in vars) {
    var value = vars[name];
    if(value)
      alert.setProperty(name, value);
  }
  this.alertModel.addAlert(alert);
};

