
var module = {
	name : "DOM testing module",
	type : "dom-test",
	html : "jquery-test.html"
};


function q() {
	var r = [];
	for(var i = 0; i < arguments.length; i++) {
		r.push(doc.getElementById(arguments[i]));
	}
	return r;
}

function t(a,b,c) {
	var f = jQuery(b).get(), s = "";
	for(var i = 0; i < f.length; i++) {
		s += (s && ",") + '"' + f[i].id + '"';
	}
	same(f, q.apply(q,c), a + " ("+ b + ")");
}

function print(msg) {
    java.lang.System.out.println(msg);
}

function run() {
	print("Running dom test");
	
	this.doc = new HTMLDocument(testDom);
	jQuery.setDocument(doc);
	
	basic_tests();
	broken_tests();
	id_test();
	class_tests();
	name_tests();
	multiple_tests();
	child_and_adjacent_tests();
	attributes_tests();
	pseudo_child_tests();
	pseudo_misc_tests();
	pseudo_not_tests();
	pseudo_position_tests();
	pseudo_form_tests();
	trac_bugs();
	
	summary();

}

function basic_tests() {
	var sz = jQuery("*").size();
	ok(sz >= 30, "Select all, size is "+ sz);
	var all = jQuery("*"), good = true;
	for(var i = 0; i < all.length; i++)
		if(all[i].nodeType == 8)
			good = false;
	ok(good, "Select all elements, no comment nodes");
	
	t( "Element Selector", "p", ["firstp","ap","sndp","en","sap","first"] );
	t( "Element Selector", "body", ["body"] );
	t( "Element Selector", "html", ["html"] );
	t( "Parent Element", "div p", ["firstp","ap","sndp","en","sap","first"] );
	equals( jQuery("param", "#object1").length, 2, "Object/param as context" );
	same( jQuery("p", doc.getElementsByTagName("div")).get(), q("firstp","ap","sndp","en","sap","first"), "Finding elements with a context." );
	same( jQuery("p", "div").get(), q("firstp","ap","sndp","en","sap","first"), "Finding elements with a context." );
	same( jQuery("p", jQuery("div")).get(), q("firstp","ap","sndp","en","sap","first"), "Finding elements with a context." );
	same( jQuery("div").find("p").get(), q("firstp","ap","sndp","en","sap","first"), "Finding elements with a context." );

	same( jQuery("#form").find("select").get(), q("select1","select2","select3","select4","select5"), "Finding selects with a context." );
	
	ok( jQuery("#length").length, '<input name="length"> cannot be found under IE, see #945' );
	ok( jQuery("#lengthtest input").length, '<input name="length"> cannot be found under IE, see #945' );

	// Check for unique-ness and sort order
	same( jQuery("p, div p").get(), jQuery("p").get(), "Check for duplicates: p, div p" );

	t( "Checking sort order", "h2, h1", ["qunit-header", "qunit-banner", "qunit-userAgent"] );
	t( "Checking sort order", "h2:first, h1:first", ["qunit-header", "qunit-banner"] );
	t( "Checking sort order", "p, p a", ["firstp", "simon1", "ap", "google", "groups", "anchor1", "mark", "sndp", "en", "yahoo", "sap", "anchor2", "simon", "first"] );
}
function broken_tests() {
	function broken(name, selector) {
		try {
			jQuery(selector);
			ok( false, name + ": " + selector );
		} catch(e){
			ok(  typeof e === "string" && e.indexOf("Syntax error") >= 0,
				name + ": " + selector );
		}
	}
	
	broken( "Broken Selector", "[", [] );
	broken( "Broken Selector", "(", [] );
	broken( "Broken Selector", "{", [] );
	broken( "Broken Selector", "<", [] );
	broken( "Broken Selector", "()", [] );
	broken( "Broken Selector", "<>", [] );
	broken( "Broken Selector", "{}", [] );
	broken( "Doesn't exist", ":visble", [] );
}

function id_test() {
	t( "ID Selector", "#body", ["body"] );
	t( "ID Selector w/ Element", "body#body", ["body"] );
	t( "ID Selector w/ Element", "ul#first", [] );
	t( "ID selector with existing ID descendant", "#firstp #simon1", ["simon1"] );
	t( "ID selector with non-existant descendant", "#firstp #foobar", [] );
	 
	t( "Escaped ID", "#foo\\:bar", ["foo:bar"] );
	t( "Escaped ID", "#test\\.foo\\[5\\]bar", ["test.foo[5]bar"] );
	t( "Descendant escaped ID", "div #foo\\:bar", ["foo:bar"] );
	t( "Descendant escaped ID", "div #test\\.foo\\[5\\]bar", ["test.foo[5]bar"] );
	t( "Child escaped ID", "form > #foo\\:bar", ["foo:bar"] );
	t( "Child escaped ID", "form > #test\\.foo\\[5\\]bar", ["test.foo[5]bar"] );
	
	t( "ID Selector, child ID present", "#form > #radio1", ["radio1"] ); // bug #267
	t( "ID Selector, not an ancestor ID", "#form #first", [] );
	t( "ID Selector, not a child ID", "#form > #option1a", [] );
	
	t( "All Children of ID", "#foo > *", ["sndp", "en", "sap"] );
	t( "All Children of ID with no children", "#firstUL > *", [] );

	t( "ID Selector on Form with an input that has a name of 'id'", "#lengthtest", ["lengthtest"] );
	
	t( "ID selector with non-existant ancestor", "#asdfasdf #foobar", [] ); // bug #986

	same( jQuery("body").find("div#form").get(), [], "ID selector within the context of another element" );

	t( "Underscore ID", "#types_all", ["types_all"] );
	t( "Dash ID", "#fx-queue", ["fx-queue"] );

	t( "ID with weird characters in it", "#name\\+value", ["name+value"] );
}

function class_tests() {
	t( "Class Selector", ".blog", ["mark","simon"] );
	t( "Class Selector", ".GROUPS", ["groups"] );
	t( "Class Selector", ".blog.link", ["simon"] );
	t( "Class Selector w/ Element", "a.blog", ["mark","simon"] );
	t( "Parent Class Selector", "p .blog", ["mark","simon"] );

	same( jQuery(".blog", doc.getElementsByTagName("p")).get(), q("mark", "simon"), "Finding elements with a context." );
	same( jQuery(".blog", "p").get(), q("mark", "simon"), "Finding elements with a context." );
	same( jQuery(".blog", jQuery("p")).get(), q("mark", "simon"), "Finding elements with a context." );
	same( jQuery("p").find(".blog").get(), q("mark", "simon"), "Finding elements with a context." );

	t( "Escaped Class", ".foo\\:bar", ["foo:bar"] );
	t( "Escaped Class", ".test\\.foo\\[5\\]bar", ["test.foo[5]bar"] );
	t( "Descendant scaped Class", "div .foo\\:bar", ["foo:bar"] );
	t( "Descendant scaped Class", "div .test\\.foo\\[5\\]bar", ["test.foo[5]bar"] );
	t( "Child escaped Class", "form > .foo\\:bar", ["foo:bar"] );
	t( "Child escaped Class", "form > .test\\.foo\\[5\\]bar", ["test.foo[5]bar"] );

}

function name_tests() {
	t( "Name selector", "input[name=action]", ["text1"] );
	t( "Name selector with single quotes", "input[name='action']", ["text1"] );
	t( "Name selector with double quotes", 'input[name="action"]', ["text1"] );

	t( "Name selector non-input", "[name=test]", ["length", "fx-queue"] );
	t( "Name selector non-input", "[name=div]", ["fadein"] );
	t( "Name selector non-input", "*[name=iframe]", ["iframe"] );

	t( "Name selector for grouped input", "input[name='types[]']", ["types_all", "types_anime", "types_movie"] )

	same( jQuery("#form").find("input[name=action]").get(), q("text1"), "Name selector within the context of another element" );
	same( jQuery("#form").find("input[name='foo[bar]']").get(), q("hidden2"), "Name selector for grouped form element within the context of another element" );

}

function multiple_tests() {
	t( "Comma Support", "h2, p", ["qunit-banner","qunit-userAgent","firstp","ap","sndp","en","sap","first"]);
	t( "Comma Support", "h2 , p", ["qunit-banner","qunit-userAgent","firstp","ap","sndp","en","sap","first"]);
	t( "Comma Support", "h2 , p", ["qunit-banner","qunit-userAgent","firstp","ap","sndp","en","sap","first"]);
	t( "Comma Support", "h2,p", ["qunit-banner","qunit-userAgent","firstp","ap","sndp","en","sap","first"]);
}

function child_and_adjacent_tests() {
	t( "Child", "p > a", ["simon1","google","groups","mark","yahoo","simon"] );
	t( "Child", "p> a", ["simon1","google","groups","mark","yahoo","simon"] );
	t( "Child", "p >a", ["simon1","google","groups","mark","yahoo","simon"] );
	t( "Child", "p>a", ["simon1","google","groups","mark","yahoo","simon"] );
	t( "Child w/ Class", "p > a.blog", ["mark","simon"] );
	t( "All Children", "code > *", ["anchor1","anchor2"] );

	t( "All Grandchildren", "p > * > *", ["anchor1","anchor2"] );
	t( "Adjacent", "a + a", ["groups"] );
	t( "Adjacent", "a +a", ["groups"] );
	t( "Adjacent", "a+ a", ["groups"] );
	t( "Adjacent", "a+a", ["groups"] );
	t( "Adjacent", "p + p", ["ap","en","sap"] );
	t( "Adjacent", "p#firstp + p", ["ap"] );
	t( "Adjacent", "p[lang=en] + p", ["sap"] );
	t( "Adjacent", "a.GROUPS + code + a", ["mark"] );
	t( "Comma, Child, and Adjacent", "a + a, code > a", ["groups","anchor1","anchor2"] );
	t( "Element Preceded By", "p ~ div", ["foo", "moretests","tabindex-tests", "liveHandlerOrder", "siblingTest"] );
	t( "Element Preceded By", "#first ~ div", ["moretests","tabindex-tests", "liveHandlerOrder", "siblingTest"] );
	t( "Element Preceded By", "#groups ~ a", ["mark"] );
	t( "Element Preceded By", "#length ~ input", ["idTest"] );
	t( "Element Preceded By", "#siblingfirst ~ em", ["siblingnext"] );

	t( "Verify deep class selector", "div.blah > p > a", [] );

	t( "No element deep selector", "div.foo > span > a", [] );

	same( jQuery("> :first", doc.getElementById("nothiddendiv")).get(), q("nothiddendivchild"), "Verify child context positional selctor" );
	same( jQuery("> :eq(0)", doc.getElementById("nothiddendiv")).get(), q("nothiddendivchild"), "Verify child context positional selctor" );
	same( jQuery("> *:first", doc.getElementById("nothiddendiv")).get(), q("nothiddendivchild"), "Verify child context positional selctor" );

	t( "Non-existant ancestors", ".fototab > .thumbnails > a", [] );
}

function attributes_tests() {
	t( "Attribute Exists", "a[title]", ["google"] );
	t( "Attribute Exists", "*[title]", ["google"] );
	t( "Attribute Exists", "[title]", ["google"] );
	t( "Attribute Exists", "a[ title ]", ["google"] );
	
	t( "Attribute Equals", "a[rel='bookmark']", ["simon1"] );
	t( "Attribute Equals", 'a[rel="bookmark"]', ["simon1"] );
	t( "Attribute Equals", "a[rel=bookmark]", ["simon1"] );
	t( "Attribute Equals", "a[href='http://www.google.com/']", ["google"] );
	t( "Attribute Equals", "a[ rel = 'bookmark' ]", ["simon1"] );

	doc.getElementById("anchor2").href = "#2";
	t( "href Attribute", "p a[href^=#]", ["anchor2"] );
	t( "href Attribute", "p a[href*=#]", ["simon1", "anchor2"] );

	t( "for Attribute", "form label[for]", ["label-for"] );
	t( "for Attribute in form", "#form [for=action]", ["label-for"] );

	t( "Attribute containing []", "input[name^='foo[']", ["hidden2"] );
	t( "Attribute containing []", "input[name^='foo[bar]']", ["hidden2"] );
	t( "Attribute containing []", "input[name*='[bar]']", ["hidden2"] );
	t( "Attribute containing []", "input[name$='bar]']", ["hidden2"] );
	t( "Attribute containing []", "input[name$='[bar]']", ["hidden2"] );
	t( "Attribute containing []", "input[name$='foo[bar]']", ["hidden2"] );
	t( "Attribute containing []", "input[name*='foo[bar]']", ["hidden2"] );
	
	t( "Multiple Attribute Equals", "#form input[type='radio'], #form input[type='hidden']", ["radio1", "radio2", "hidden1"] );
	t( "Multiple Attribute Equals", "#form input[type='radio'], #form input[type=\"hidden\"]", ["radio1", "radio2", "hidden1"] );
	t( "Multiple Attribute Equals", "#form input[type='radio'], #form input[type=hidden]", ["radio1", "radio2", "hidden1"] );

	
	t( "Attribute Begins With", "a[href ^= 'http://www']", ["google","yahoo"] );
	t( "Attribute Ends With", "a[href $= 'org/']", ["mark"] );
	t( "Attribute Contains", "a[href *= 'google']", ["google","groups"] );
	t( "Attribute Is Not Equal", "#ap a[hreflang!='en']", ["google","groups","anchor1"] );

	t("Empty values", "#select1 option[value='']", ["option1a"]);
	t("Empty values", "#select1 option[value!='']", ["option1b","option1c","option1d"]);
	
	t("Select options via :selected", "#select1 option:selected", ["option1a"] );
	t("Select options via :selected", "#select2 option:selected", ["option2d"] );
	t("Select options via :selected", "#select3 option:selected", ["option3b", "option3c"] );
	
	t( "Grouped Form Elements", "input[name='foo[bar]']", ["hidden2"] );
}

function pseudo_child_tests() {
	t( "First Child", "p:first-child", ["firstp","sndp"] );
	t( "Last Child", "p:last-child", ["sap"] );
	t( "Only Child", "#main a:only-child", ["simon1","anchor1","yahoo","anchor2","liveLink1","liveLink2"] );
	t( "Empty", "ul:empty", ["firstUL"] );
	t( "Is A Parent", "p:parent", ["firstp","ap","sndp","en","sap","first"] );

	t( "First Child", "p:first-child", ["firstp","sndp"] );
	t( "Nth Child", "p:nth-child(1)", ["firstp","sndp"] );
	t( "Not Nth Child", "p:not(:nth-child(1))", ["ap","en","sap","first"] );
	
	t( "Last Child", "p:last-child", ["sap"] );
	t( "Last Child", "#main a:last-child", ["simon1","anchor1","mark","yahoo","anchor2","simon","liveLink1","liveLink2"] );
	
	t( "Nth-child", "#main form#form > *:nth-child(2)", ["text1"] );
	t( "Nth-child", "#main form#form > :nth-child(2)", ["text1"] );

	t( "Nth-child", "#form select:first option:nth-child(3)", ["option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(0n+3)", ["option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(1n+0)", ["option1a", "option1b", "option1c", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(1n)", ["option1a", "option1b", "option1c", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(n)", ["option1a", "option1b", "option1c", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(even)", ["option1b", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(odd)", ["option1a", "option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(2n)", ["option1b", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(2n+1)", ["option1a", "option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(3n)", ["option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(3n+1)", ["option1a", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(3n+2)", ["option1b"] );
	t( "Nth-child", "#form select:first option:nth-child(3n+3)", ["option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(3n-1)", ["option1b"] );
	t( "Nth-child", "#form select:first option:nth-child(3n-2)", ["option1a", "option1d"] );
	t( "Nth-child", "#form select:first option:nth-child(3n-3)", ["option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(3n+0)", ["option1c"] );
	t( "Nth-child", "#form select:first option:nth-child(-n+3)", ["option1a", "option1b", "option1c"] );

}

function pseudo_misc_tests() {
	t( "Headers", ":header", ["qunit-header", "qunit-banner", "qunit-userAgent"] );
	t( "Has Children - :has()", "p:has(a)", ["firstp","ap","en","sap"] );

	t( "Text Contains", "a:contains(Google)", ["google","groups"] );
	t( "Text Contains", "a:contains(Google Groups)", ["groups"] );

	t( "Text Contains", "a:contains(Google Groups (Link))", ["groups"] );
	t( "Text Contains", "a:contains((Link))", ["groups"] );

}

function pseudo_not_tests() {
	t( "Not", "a.blog:not(.link)", ["mark"] );

	t( "Not - multiple", "#form option:not(:contains(Nothing),#option1b,:selected)", ["option1c", "option1d", "option2b", "option2c", "option3d", "option3e", "option4e", "option5b", "option5c"] );
	t( "Not - recursive", "#form option:not(:not(:selected))[id^='option3']", [ "option3b", "option3c"] );

	t( ":not() failing interior", "p:not(.foo)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not() failing interior", "p:not(div.foo)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not() failing interior", "p:not(p.foo)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not() failing interior", "p:not(#blargh)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not() failing interior", "p:not(div#blargh)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not() failing interior", "p:not(p#blargh)", ["firstp","ap","sndp","en","sap","first"] );

	t( ":not Multiple", "p:not(a)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not Multiple", "p:not(a, b)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not Multiple", "p:not(a, b, div)", ["firstp","ap","sndp","en","sap","first"] );
	t( ":not Multiple", "p:not(p)", [] );
	t( ":not Multiple", "p:not(a,p)", [] );
	t( ":not Multiple", "p:not(p,a)", [] );
	t( ":not Multiple", "p:not(a,p,b)", [] );
	t( ":not Multiple", ":input:not(:image,:input,:submit)", [] );

	t( "No element not selector", ".container div:not(.excluded) div", [] );

	t( ":not() Existing attribute", "#form select:not([multiple])", ["select1", "select2", "select5"]);
	t( ":not() Equals attribute", "#form select:not([name=select1])", ["select2", "select3", "select4","select5"]);
	t( ":not() Equals quoted attribute", "#form select:not([name='select1'])", ["select2", "select3", "select4", "select5"]);

	t( ":not() Multiple Class", "#foo a:not(.blog)", ["yahoo","anchor2"] );
	t( ":not() Multiple Class", "#foo a:not(.link)", ["yahoo","anchor2"] );
	t( ":not() Multiple Class", "#foo a:not(.blog.link)", ["yahoo","anchor2"] );

}

function pseudo_position_tests() {
	t( "nth Element", "p:nth(1)", ["ap"] );
	t( "First Element", "p:first", ["firstp"] );
	t( "Last Element", "p:last", ["first"] );
	t( "Even Elements", "p:even", ["firstp","sndp","sap"] );
	t( "Odd Elements", "p:odd", ["ap","en","first"] );
	t( "Position Equals", "p:eq(1)", ["ap"] );
	t( "Position Greater Than", "p:gt(0)", ["ap","sndp","en","sap","first"] );
	t( "Position Less Than", "p:lt(3)", ["firstp","ap","sndp"] );

	t( "Check position filtering", "div#nothiddendiv:eq(0)", ["nothiddendiv"] );
	t( "Check position filtering", "div#nothiddendiv:last", ["nothiddendiv"] );
	t( "Check position filtering", "div#nothiddendiv:not(:gt(0))", ["nothiddendiv"] );
	t( "Check position filtering", "#foo > :not(:first)", ["en", "sap"] );
	t( "Check position filtering", "select > :not(:gt(2))", ["option1a", "option1b", "option1c"] );
	t( "Check position filtering", "select:lt(2) :not(:first)", ["option1b", "option1c", "option1d", "option2a", "option2b", "option2c", "option2d"] );
	t( "Check position filtering", "div.nothiddendiv:eq(0)", ["nothiddendiv"] );
	t( "Check position filtering", "div.nothiddendiv:last", ["nothiddendiv"] );
	t( "Check position filtering", "div.nothiddendiv:not(:lt(0))", ["nothiddendiv"] );

	t( "Check element position", "div div:eq(0)", ["nothiddendivchild"] );
	t( "Check element position", "div div:eq(5)", ["t2037"] );
	t( "Check element position", "div div:eq(28)", ["hide"] );
	t( "Check element position", "div div:first", ["nothiddendivchild"] );
	t( "Check element position", "div > div:first", ["nothiddendivchild"] );
	t( "Check element position", "#dl div:first div:first", ["foo"] );
	t( "Check element position", "#dl div:first > div:first", ["foo"] );
	t( "Check element position", "div#nothiddendiv:first > div:first", ["nothiddendivchild"] );

}

function pseudo_form_tests() {
	t( "Form element :input", "#form :input", ["text1", "text2", "radio1", "radio2", "check1", "check2", "hidden1", "hidden2", "name", "search", "button", "area1", "select1", "select2", "select3", "select4", "select5"] );
	t( "Form element :radio", "#form :radio", ["radio1", "radio2"] );
	t( "Form element :checkbox", "#form :checkbox", ["check1", "check2"] );
	t( "Form element :text", "#form :text:not(#search)", ["text1", "text2", "hidden2", "name"] );
	t( "Form element :radio:checked", "#form :radio:checked", ["radio2"] );
	t( "Form element :checkbox:checked", "#form :checkbox:checked", ["check1"] );
	t( "Form element :radio:checked, :checkbox:checked", "#form :radio:checked, #form :checkbox:checked", ["radio2", "check1"] );

	t( "Selected Option Element", "#form option:selected", ["option1a","option2d","option3b","option3c","option4b","option4c","option4d","option5a"] );
}

function trac_bugs() {
	same( jQuery("div").has("em").get(), q("main", "siblingTest"), "Trac ticket #20 has()");
	same( jQuery("#nonnodes").text(), "hi there ", "Trac ticket #23 text()");
}
