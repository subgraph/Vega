var module = {
  name: "Error Page Detection",
  type: "response-processor"
};

function run(request, response, ctx) {
  var htmlres;
  var phpres;
  var cfres;
  var rubyres;
  var djangores;
  var javares;
  var x = new Array();
  var i;


  var html = ["400", "401", "402", "500", "501"];

  var php = ["unexpected T_STRING", "unexpected T-STRING", "Parse Error", "Fatal error", "Call to undefined function", "Notice: Undefined index", "T_VARIABLE", "T_DOLLAR_OPEN_CURLY_BRACES", "T_CURLY_OPEN", "unexpected $end, expecting", "syntax error, unexpected", "No row with the given identifier", "open_basedir restriction in effect", "Cannot execute a blank command in", "Fatal error</b>:  preg_replace", "thrown in <b>", "Stack trace:", ];

  var cf = ["Invalid list index", "The string is not closed", "an exception occured", "There is no column", "Session is Invalid", "Cannot invoke method", "Image type must be RGB, ARGB", "A License exception has occured", '<li>Search the <a href="http://www.macromedia.com/support/coldfusion/"', ];

  var ruby = ["Ruby on Rails application could not be started</h1>", ];

  var django = ["you have <code>DEBUG = True</code>", "<div id=\"pastebinTraceback\" class=\"pastebin\">", "PythonHandler django.core.handlers.modpython", "t = loader.get_template(template_name) # You need to create a 404.html template.", "<h2>Traceback <span>(innermost last)</span></h2>"];

  var java = ["[java.lang.", "class java.lang.", "java.lang.NullPointerException", "java.rmi.ServerException", "at org.apache.", "full exception chain stacktrace"];


  for (i = 0; i <= html.length - 1; i += 1) {
    x = response.code.toString();
    x = x.indexOf(html[i]);
    if (x >= 0) {
      htmlres = 1;
    }
  }

  for (i = 0; i <= php.length - 1; i += 1) {
    x = response.bodyAsString.indexOf(php[i]);
    if (x >= 0) {
      phpres = 1;
    }
  }

  for (i = 0; i <= cf.length - 1; i += 1) {
    x = response.bodyAsString.indexOf(cf[i]);
    if (x >= 0) {
      cfres = 1;
    }
  }
  for (i = 0; i <= ruby.length - 1; i += 1) {
    x = response.bodyAsString.indexOf(ruby[i]);
    if (x >= 0) {
      rubyres = 1;
    }
  }
  for (i = 0; i <= django.length - 1; i += 1) {
    x = response.bodyAsString.indexOf(django[i]);
    if (x >= 0) {
      djangores = 1;
    }
  }
  for (i = 0; i <= java.length - 1; i += 1) {
    x = response.bodyAsString.indexOf(java[i]);
    if (x >= 0) {
      javares = 1;
    }
  }

  if (htmlres) {
    ctx.alert("vinfo-errorpages-html", request, response, {
      output: response.rawResponse.getStatusLine().toString(),
      resource: request.requestLine.uri,
      key: "vinfo-errorpages-html" + request.requestLine.uri
    });
  }
  if (phpres) {
    ctx.alert("vinfo-errorpages-php", request, response, {
      "output": response.bodyAsString,
      "resource": request.requestLine.uri,
      key: "vinfo-errorpages-php" + request.requestLine.uri
    });
  }
  if (cfres) {
    ctx.alert("vinfo-errorpages-cf", request, response, {
      "output": response.bodyAsString,
      "resource": request.requestLine.uri,
      key: "vinfo-errorpages-cf" + request.requestLine.uri
    });
  }
  if (rubyres) {
    ctx.alert("vinfo-errorpages-ruby", request, response, {
      "output": response.bodyAsString,
      "resource": request.requestLine.uri,
      key: "vinfo-errorpages-ruby" + request.requestLine.uri
    });
  }
  if (djangores) {
    ctx.alert("vinfo-errorpages-django", request, response, {
      "output": response.bodyAsString,
      "resource": request.requestLine.uri,
      key: "vinfo-errorpages-django" + request.requestLine.uri,
    });
  }
  if (javares) {
    ctx.alert("vinfo-errorpages-java", request, response, {
      "output": response.bodyAsString,
      "resource": request.requestLine.uri,
      key: "vinfo-errorpages-java" + request.requestLine.uri,
    });
  }
}
