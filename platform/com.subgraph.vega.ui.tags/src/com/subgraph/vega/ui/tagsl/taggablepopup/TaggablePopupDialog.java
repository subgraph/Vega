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
package com.subgraph.vega.ui.tagsl.taggablepopup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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
	private List<Label> tagLabelList;
	
	public TaggablePopupDialog(Shell parent, ITaggable taggable, Point origin) {
		super(parent, HOVER_SHELLSTYLE, false, false, false, false, false, null, null);
		this.taggable = taggable;
		this.origin = origin;
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));

		Label header = new Label(parentComposite, SWT.NONE);
		header.setText("Tags:");
		FontData[] fd = header.getFont().getFontData();
		fd[0].setStyle(SWT.BOLD);
		final Font newFont = new Font(parentComposite.getDisplay(), fd[0]);
		header.setFont(newFont);
		header.addDisposeListener(new DisposeListener() {
		    public void widgetDisposed(DisposeEvent e) {
		    	newFont.dispose();
		    }
		});

		final Collection<ITag> tagList = taggable.getAllTags();
		tagLabelList = new ArrayList<Label>(tagList.size());
		for (ITag tag: tagList) {
			tagLabelList.add(createTagLabel(parentComposite, tag));
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

	@Override
	protected Point getInitialLocation(Point initialSize) {
		final int diff = origin.x - initialSize.x;
		final int x = (diff > 0) ? (diff) : (origin.x); 
		return new Point(x, origin.y);
	}
	
	@Override
	protected Color getBackground() {
		return parentComposite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected List getForegroundColorExclusions() {
		final List superExclusions = super.getForegroundColorExclusions();
		final List exclusions = new ArrayList(superExclusions.size() + tagLabelList.size());
		exclusions.addAll(superExclusions);
		exclusions.addAll(tagLabelList);
		return exclusions;
	}
	
	private Label createTagLabel(Composite parent, ITag tag) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(tag.getName());
		final Color color = tagColorToColor(label.getDisplay(), tag.getNameColor());
		label.setForeground(color);
		label.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				color.dispose();
			}
		});
		return label;
	}

	private Color tagColorToColor(Device device, int color) {
		return new Color(device, (color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
	}

}
