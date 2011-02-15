package com.subgraph.vega.api.model.web.forms;

import java.net.URI;
import java.util.Collection;

public interface IWebForm {
	enum FormMethod { METHOD_GET, METHOD_POST };
	enum EncType { FORM_URLENCODED, FORM_DATA };
	
	EncType getEncodingType();
	FormMethod getMethod();
	URI getAction();
	Collection<IWebFormField> getFields();
}
