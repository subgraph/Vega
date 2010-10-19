
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