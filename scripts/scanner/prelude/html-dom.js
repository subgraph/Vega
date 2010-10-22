
var fakeElement = {
	insertBefore : function() {},
	removeChild : function() {},
	appendChild : function() {},
	getElementsByTagName: function() { return []; }
};

var window = { 
		document: {
			documentElement: fakeElement,
			createElement: function() { return fakeElement; },
			createComment: function() { },
			getElementById : function() { },
			getElementsByTagName: function() { return []; }
		}
};
var navigator = { userAgent: "" };

var jQuery = function(selector, context) {
	if(context)
		window.document = context;
	return window.jQuery(selector, context);
};

var $ = function(selector, context) {
	return this.jQuery(selector, context);
};