var module = {
	name: "Language detection module",
	type: "response-processor"
};

function run() {
	var url = this.httpRequest.getRequestLine().getUri();
	var fr = 0;
	var tr = 0;
	var es = 0;
	var de = 0;
	var esp = 0;
	var lang = "";

	var fra = [ "oui", "vraiment", "beacoup", "cher", "nouvelle", "voiture", "aide", "montant", "homme", "femme", "enfant", "maison", "publics", "pouvoir", "personne", "madame", "monsieur", "politique", "gouvernement", "etudiant", "avoir", "alors", "pour", "musique", "bien", "comme", "voir", "savoir", "puis", "moi", "toi", "pouvoir", "eux", "vois", "vous", "avec", "falloir", "enfin", "petit", "vouloir", "venir", "prendre", "tout", "passer", "mettre", "devoir", "comprendre", "maintenant", "un", "deux", "trois", "quatre", "cinq", "six", "huit", "neuf", "dix", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche", "contre", "janvier", "fevrier", "mars", "avril", "juin", "juillet", "aout", "septembre", "octobre", "novembre", "decembre" ];

	var tra = [ 'ancak', 'burada', 'duyuru', 'evet', 'fakat', 'gibi', 'haber', 'kadar', 'karar', 'kaynak', 'olarak', 'sayfa', 'siteye', 'sorumlu', 'tamam', 'yasak', 'zorunlu' ];

	var esa = [ 'ante', 'bajo', 'cabe', 'con' , 'contra', 'desde', 'entre', 'hacia', 'hasta', 'para', 'por' , 'segun', 'sobre', 'tras', "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve", "diez", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo", "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre", ];

	var dea = [ "das", "und", "sein", "haben", "ich", "werden", "von", "nicht", "dass", "auch", "auf", "sich", "dann", "nach", "geben", "kommen", "eigentlich", "sehen", "lassen", "unter", "denn", "zwei", "wissen", "immer", "gehen", "wollen", "aber", "sagen", "nacht", "mein", "zuppe", "augen", "zind", "damit" "eins", "zwei", "drei", "vier", "fünf", "sechs", "sieben", "acht", "neun", "zehn", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag", "Januar", "Februar", "März", "Mai", "Juni", "Juli", "August", "September", "Oktober", "Dezember",];

	var espa = [ "kaj", "estis", "kiam", "povas", "lingvo", "tamen", "havas", "tiam", "tion", "kiun", "sian", "devas", "poste", "homoj", "respondis", "faris", "vortoj", "trovis", "infano", "internacia", "kapon" ];

	for (i=0;i<=fra.length-1;i+=1) {
		var x = new RegExp(fra[i]+" ", "i");
		if (x.exec(response.bodyAsString)) {
			fr += 1;
		}
	}
	for (i=0;i<=tra.length-1;i+=1) {
		var x = new RegExp(tra[i]+" ", "i");
		if (x.exec(response.bodyAsString)) {
			tr += 1;
		}
	}
	for (i=0;i<=esa.length-1;i+=1) {
		var x = new RegExp(esa[i]+" ", "i");
		if (x.exec(response.bodyAsString)) {
			es += 1;
		}
	}
	for (i=0;i<=dea.length-1;i+=1) {
		var x = new RegExp(dea[i]+" ", "i");
		if (x.exec(response.bodyAsString)) {
			de += 1;
		}
	}
	for (i=0;i<=espa.length-1;i+=1) {
		var x = new RegExp(espa[i]+" ", "i");
		if (x.exec(response.bodyAsString)) {
			esp += 1;
		}
	}

	if (fr >= 2) {
		lang += "French ";
	}
	if (tr >= 2) {
		lang += "Turkish ";
	}
	if (es >= 2) {
		lang += "Spanish ";
	}
	if (de >= 2) {
		lang += "German ";
	}
	if (esp >= 2) {
		lang += "Esperanto ";
	}
	if (lang!="") {
		model.set(url, lang);
		model.alert("vinfo-lang", {"output": lang, "resource": httpRequest.requestLine.uri} );
	}
}
