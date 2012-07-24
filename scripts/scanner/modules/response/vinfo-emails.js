var module = {
  name: "E-Mail Finder Module",
  type: "response-processor",
  defaultDisabled: true
};

function run(request, response, ctx) {
  var atDomainRegex = /@(?:[^\s.]{1,64}\.)+\S{2,6}/,
      mailRegex = /\w[^\s@]*@(?:[^\s.]{1,64}\.)+\S{2,6}/g,
      strictMailRegex = /[\w!#$%&'*+-\/=?^`{|}~.]+@(?:(([a-z0-9]{1}[a-z0-9\-]{0,62}[a-z0-9]{1})|[a-z])\.)+(?:aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])/i,
      body = response.bodyAsString,
      emails = [],
      r, sr, i, found;

  if (!atDomainRegex.test(body)) return;

  while (r = mailRegex.exec(body)) {
    sr = strictMailRegex.exec(r[0]);
    if (sr && emails.indexOf(sr[0]) == -1) {
      found = 0;
      for (i = 0; i < emails.length; i++) {
        ctx.addStringHighlight(emails[i]);
        if (emails[i] == sr[0].toLowerCase()) {
          found = 1;
        }
      }
      if (!found) {
        emails.push(sr[0].toLowerCase());
      }
    }
  }
  if (emails.length) {
    var key = emails.sort().join(" ");
    var uristr = String(request.requestLine.uri);
    var uripart = uristr.replace(/\?.*/, "");
    ctx.alert("vinfo-emails", request, response, {
      "output": emails.join(" "),
      "resource": uripart,
      key: "vinfo-emails" + uripart + key
    });
  }
}
