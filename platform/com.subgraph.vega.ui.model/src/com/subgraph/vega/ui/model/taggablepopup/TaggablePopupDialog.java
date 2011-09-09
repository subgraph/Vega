/*******************************************************************************
 * Copyright (c) 2011 Subgraph.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Subgraph - initial API and implementation
 ******************************************************************************/
package com.subgraph.vega.ui.model.taggablepopup;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.api.model.tags.ITag;
import com.subgraph.vega.api.model.tags.ITaggable;

public class TaggablePopupDialog extends PopupDialog {
	private Composite parentComposite;
	private ITaggable taggable;
	private Point origin;
	
	public TaggablePopupDialog(Shell parent, ITaggable taggable, Point origin) {
		super(parent, HOVER_SHELLSTYLE, false, false, false, false, false, null, null);
		this.taggable = taggable;
		this.origin = origin;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));

		for (ITag tag: taggable.getAllTags()) {
			createTagLabel(parentComposite, tag);
		}

		return parentComposite;
	}
	
	@Override
	protected boolean hasInfoArea() {
		return false;
	}

	@Override
	protected boolean hasTitleArea() {
		return false;
	}

	private void createTagLabel(Composite parent, ITag tag) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(tag.getName());
		label.setForeground(tagColorToColor(label.getDisplay(), tag.getNameColor()));
	}

	private Color tagColorToColor(Device device, int color) {
		return new Color(device, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
	}

}
