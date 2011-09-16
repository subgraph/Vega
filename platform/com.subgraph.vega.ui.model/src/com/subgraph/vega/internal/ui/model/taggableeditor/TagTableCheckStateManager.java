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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;

public class TagTableCheckStateManager implements ICheckStateProvider, ICheckStateListener {
	private List<TagModifier> tagList = new ArrayList<TagModifier>(); /** List of selected TagModifiers */

	@Override
	public void checkStateChanged(CheckStateChangedEvent event) {
		TagModifier tagModifier = (TagModifier) event.getElement();
		if (event.getChecked() != false) {
			tagList.add(tagModifier);
		} else {
			tagList.remove(tagModifier);
		}
	}

	@Override
	public boolean isChecked(Object element) {
		return (tagList.indexOf(element) != -1);
	}

	@Override
	public boolean isGrayed(Object element) {
		return false;
	}
	
	public void addChecked(TagModifier tagModifier) {
		tagList.add(tagModifier);
	}

	public List<TagModifier> getCheckedList() {
		return tagList;
	}
	
}
