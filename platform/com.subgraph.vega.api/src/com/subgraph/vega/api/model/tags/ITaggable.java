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
package com.subgraph.vega.api.model.tags;

import java.util.Collection;

/**
 * Interface for an object to which tags can be applied.
 */
public interface ITaggable {
	/**
	 * Get a collection of all tags applied to this object.
	 * @return Tags applied to this record.
	 */
	Collection<ITag> getAllTags();

	/**
	 * Get a count of tags applied to this object.
	 * @return Count of tags applied to this object.
	 */
	int getTagCount();

	/**
	 * Set the tags for this object. Clears any existing tags.
	 * @param tags Tags.
	 */
	void setTags(Collection<ITag> tags);
	
	/**
	 * Apply a tag to this object.
	 * @param tag Tag.
	 */
	void addTag(ITag tag);

	/**
	 * Remove a tag from this object.
	 * @param tag Tag.
	 */
	void removeTag(ITag tag);
}
