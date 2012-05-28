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
package com.subgraph.vega.internal.ui.tags.taggableeditor;

import com.subgraph.vega.api.model.tags.ITag;
import com.subgraph.vega.api.model.tags.ITagModel;

/**
 * Decorator for ITag to manage manipulation to a tag without storing the changes to the database.
 */
public class TagModifier implements ITag {
	private ITag tagOrig; /** Tag stored in the workspace, or null if it isn't */
	private String name;
	private String description;
	private int nameColor;
	private int rowColor;
	private boolean isModified;
	
	public TagModifier(ITag tagOrig) {
		this.tagOrig = tagOrig;
		name = tagOrig.getName();
		description = tagOrig.getDescription();
		nameColor = tagOrig.getNameColor();
		rowColor = tagOrig.getRowColor();
		isModified = false;
	}
	
	public ITag getTagOrig() {
		return tagOrig;
	}

	public boolean isModified() {
		return isModified;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		isModified = true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		isModified = true;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setNameColor(int color) {
		this.nameColor = color;
		isModified = true;
	}

	@Override
	public int getNameColor() {
		return nameColor;
	}

	@Override
	public void setRowColor(int color) {
		this.rowColor = color;
		isModified = true;
	}

	@Override
	public int getRowColor() {
		return rowColor;
	}

	public void store(ITagModel tagModel) {
		if (isModified == true) {
			ITag tag = tagOrig;
			if (tag == null) {
				tag = tagModel.createTag();
			}

			tagOrig.setName(name);
			tagOrig.setDescription(description);
			tagOrig.setNameColor(nameColor);
			tagOrig.setRowColor(rowColor);

			if (tagOrig != tag) {
				tagModel.store(tag);
				tagOrig = tag;
			}
			isModified = false;
		}
	}

}
