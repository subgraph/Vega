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
package com.subgraph.vega.internal.ui.model.taggableeditor;

import com.subgraph.vega.api.model.tags.ITag;
import com.subgraph.vega.api.model.tags.ITagModel;

public class TagModifier {
	private ITag tagOrig; /** Tag stored in the workspace, or null if it isn't */
	private ITag tagMod; /** Modifiable tag */
	private boolean isModified;
	
	public TagModifier(ITag tagOrig, ITag tagMod) {
		this.tagOrig = tagOrig;
		this.tagMod = tagMod;
	}

	public ITag getTagOrig() {
		return tagOrig;
	}
	
	public ITag getTagMod() {
		return tagMod;
	}

	public void setModified() {
		isModified = true;
	}

	public boolean isModified() {
		return isModified;
	}
	
	public void store(ITagModel tagModel) {
		if (tagOrig != null) {
			tagOrig.setName(tagMod.getName());
			tagOrig.setDescription(tagMod.getDescription());
			tagOrig.setNameColor(tagMod.getNameColor());
			tagOrig.setRowColor(tagMod.getRowColor());
		} else {
			tagModel.store(tagMod);
			tagOrig = tagMod;
		}
	}

}
