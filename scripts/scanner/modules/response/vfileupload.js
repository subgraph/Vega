var module = {
  name : "Form File Upload",
  type: "response-processor"
};

function run() {

  var found = 0;

  if (response.document){

      var form = jQuery("form",response.document);
      
      form.children().each(function (){

        if ((this.getAttribute("type") != null) && (this.getAttribute("type") == "file"))
        {
          found++;
        }
      });

  }
  
  if (found)
  {
   var match;

   (found > 1) ? match = "instances" : match = "instance";
   model.alert("vfileupload", {"output": found + " " + match + " discovered.", "resource": httpRequest.requestLine.uri});

  }
}
