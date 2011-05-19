package com.subgraph.vega.ui.http.builder;

import org.eclipse.swt.widgets.Control;

public interface IHttpBuilderPart {
	Control getControl();
	void setEditable(boolean editable);
	void refresh();
	void processContents();
}
