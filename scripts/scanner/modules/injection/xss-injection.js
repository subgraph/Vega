var module = {
  name: "XSS Injection checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  var ps = ctx.getPathState();


  ctx.submitRequest(createReq0(ctx, ps), process, 0);
  ctx.submitRequest(createReq1(ctx, ps), process, 1);
  ctx.submitRequest(createReq2(ctx, ps), process, 2);
  ctx.submitRequest(createReq3(ctx, ps), process, 3);
  ctx.submitRequest(createReq4(ctx, ps), process, 4);
  ctx.submitRequest(createReq5(ctx, ps), process, 5);
  ctx.submitRequest(createReq6(ctx, ps), process, 6);
  ctx.submitRequest(createReq7(ctx, ps), process, 7);
  ctx.submitRequest(createReq8(ctx, ps), process, 8);
  ctx.submitRequest(createReq9(ctx, ps), process, 9);
  ctx.submitRequest(createReq10(ctx, ps), process, 10);
  ctx.submitRequest(createReq11(ctx, ps), process, 11);
  ctx.submitRequest(createReq12(ctx, ps), process, 12);
  ctx.submitRequest(createReq13(ctx, ps), process, 13);	
  ctx.submitRequest(createReq14(ctx, ps), process, 14);
  ctx.submitRequest(createReq14(ctx, ps), process, 15); 
  fuzzCookies(ctx, ps);

}

function createReq0(ctx, ps) {
  var xid = ps.allocateXssId();
  var tag = ps.createXssTag(xid);
  var req = ps.createAlteredRequest(tag, true);
  req.addHeader("Referer", tag);
  ps.registerXssRequest(req, xid);
  return req;
}

function createReq1(ctx, ps) {
  var xid = ps.allocateXssId();
  var tag = ps.createXssTag(".htaccess.aspx", xid);
  var req = ps.createAlteredRequest(tag, true);
  ps.registerXssRequest(req, xid);
  return req;
}

function createReq2(ctx, ps) {
  var xid = ps.allocateXssId();
  var tag = ps.createXssTag("</textarea>", xid);
  var req = ps.createAlteredRequest(tag, true);
  ps.registerXssRequest(req, xid);
  return req;
}

function createReq3(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag("\" src=", xid);
	var req = ps.createAlteredRequest(tag, true);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq4(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag(" src=", xid);
	var req = ps.createAlteredRequest(tag, true);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq5(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag("\n ", xid);
	var req = ps.createAlteredRequest(tag, true);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq6(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag("*/ ", xid);
	var req = ps.createAlteredRequest(tag, true);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq7(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag("' ", xid);
	var req = ps.createAlteredRequest(tag, true);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq8(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssPattern("javascript:", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}


function createReq9(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssPattern("vbscript:", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq10(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssPattern("\" onMouseOver=", xid);
	ctx.debug(tag);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq11(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssPattern("\" style=", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}


function createReq12(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssPattern("' onMouseOver=", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq13(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssPattern("' style=", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}

function createReq14(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag("\" ", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}


function createReq15(ctx,ps) {
	var xid = ps.allocateXssId();
	var tag = ps.createXssTag("\' ", xid);
	var req = ps.createAlteredRequest(tag, false);
	ps.registerXssRequest(req, xid);
	return req;
}

function fuzzCookies(ctx,ps) {
	var headers = ctx.getOrigResponse().allHeaders;
	var i = 0;

  	for (i = 0; i < headers.length; i++) {
		if (headers[i].name.toLowerCase() == "set-cookie") {
			var xid = ps.allocateXssId();
 			var tag = ps.createXssTag(xid);
			var rawCookie = headers[i].value;
			var cookies = new Array();
			var nameValue = new Array();
			var req = ps.createAlteredRequest(tag, false);
			var x = 0;

			cookies = rawCookie.split(";");
			for (i = 0; i < cookies.length; i++) {
				if (cookies[i].indexOf("=") >= 0) {
					nameValue = cookies[i].split("=");
					nameValue[1] += " " + tag;
					cookie = nameValue[0] + "=" + nameValue[1];
					req.addHeader("Cookie", cookie);
					ctx.submitRequest(req, process);
				}
			}
		}

	}          
}
	
function process(req, res, ctx) {
  ctx.contentChecks(req, res);
}
 
