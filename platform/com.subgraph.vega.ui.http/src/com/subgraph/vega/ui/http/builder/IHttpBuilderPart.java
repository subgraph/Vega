package com.subgraph.vega.ui.http.builder;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IHttpBuilderPart {
	Composite createPartControl(Composite parent);
	Control getControl();
	void refresh();
	void processContents();
}
