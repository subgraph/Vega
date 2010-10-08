var scanModel = null;
var wrappedModel = null;

__defineGetter__("model", function() {
    if(!scanModel)
      return null;
    return (wrappedModel || (wrappedModel = new Model(scanModel)));
});

function Model(scanModel) {
  this.scanModel = scanModel;
}

Model.prototype.get = function(name) {
  return this.scanModel.getProperty(name);
}

Model.prototype.set = function(name, value) {
  this.scanModel.setProperty(name, value);
}

Model.prototype.alert = function(type, vars) {
  var alert = this.scanModel.createAlert(type);
  if(!alert)
    throw new Error("Could not locate an alert template with name '"+ type +"'.");
  for(var name in vars) {
    var value = vars[name];
    if(value)
      alert.setProperty(name, value);
  }
  this.scanModel.addAlert(alert);
}

