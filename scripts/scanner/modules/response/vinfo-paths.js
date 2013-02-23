var module = {
  name: "Path Disclosure",
  type: "response-processor"
};

function run(request, response, ctx) {
  var pathlinux = new Array("/bin/", "/boot/", "/cdrom/", "/dev/", "/etc/", "/home/", "/initrd/", "/lib/", "/media/", "/mnt/", "/opt/", "/proc/", "/root/", "/sbin/", "/sys/", "/srv/", "/tmp/", "/usr/", "/var/", "/htdocs/", "/apache/");

  var pathwindows = new Array("C:\\", "D:\\", "E:\\", "Z:\\", "C:\\windows", "C:\\winnt\\", "C:\\win32\\", "C:\\win\\system\\", "C:\\windows\\system\\", "C:\\win32\\system\\", "C:\\winnt\\system\\", "C:\\Program Files\\", "C:\\Documents and Settings\\");

  var paths = [];
  var i = 0;
  var body = response.bodyAsString;
  var matches = [];
  var output = [];

  /* Perhaps we can set an server.os field in the model */
  /* For now, check them all */

  paths = paths.concat(pathlinux).concat(pathwindows);

  // first do a more efficient indexOf linear search //
  for (i = 0; i < paths.length; i++) {
    if (body.indexOf(paths[i]) >= 0) {
      matches.push(paths[i]);
    }
  }

  // if there was a match, use regexp // 
  if (matches.length) {
    for (i = 0; i < matches.length; i++) {
      var s = matches[i].replace(/\\/g, "\\\\");
      var r = "(" + s + "[A-Z\\._\\\-\\\\/\+~]*)";
      var regexp = new RegExp(r, "ig");
      var res = regexp.exec(body);
      var j = 0;
      if (res) {
        for (j = 0; j < res.length; j++) {
          if (output.indexOf(res[j]) < 0) {
            ctx.addStringHighlight(res[j]);
            output.push(res[j]);
          }
        }
      }
    }
  }



  if (matches.length) {
 
    var uri = String(request.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");

    var key = output.join(" ");

    ctx.alert("vinfo-paths", request, response, {
      "output": output.join(" "),
      "resource": uripart,
      key: "vinfo-paths" + key
    });
  }
}
