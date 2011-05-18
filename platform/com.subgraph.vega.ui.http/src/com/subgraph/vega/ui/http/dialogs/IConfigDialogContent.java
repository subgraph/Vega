package com.subgraph.vega.ui.http.dialogs;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface IConfigDialogContent {
	Composite createContents(Composite parent);
	String getTitle();
	String getMessage();
	Control getFocusControl();
	Point getSize();
	void onClose();
	void onOk();
}
