var module = {
  name : "E-Mail Finder Module",
  type: "response-processor"
};

function run() {
	var atDomainRegex = /@(?:[^\s.]{1,64}\.)+\S{2,6}/,
		mailRegex = /\w[^\s@]*@(?:[^\s.]{1,64}\.)+\S{2,6}/g,
		strictMailRegex = /[\w!#$%&'*+-\/=?^`{|}~.]+@(?:(([a-z0-9]{1}[a-z0-9\-]{0,62}[a-z0-9]{1})|[a-z])\.)+(?:aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])/i,
		body = response.bodyAsString,
		emails = [],
		r, sr;
	
	if(!atDomainRegex.test(body))
		return;
	
	while(r = mailRegex.exec(body)) {
		sr = strictMailRegex.exec(r[0]);
		if(sr && emails.indexOf(sr[0]) == -1) {
			emails.push(sr[0]);
		}
	}
	
	if (emails.length) {
	    model.alert("vinfo-emails", {"output": emails.join(" "), "resource": httpRequest.requestLine.uri} );
	}
}
