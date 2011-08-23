package com.subgraph.vega.api.model.tags;

import java.util.Collection;

public interface ITagModel {
	/**
	 * Get all tags managed by the model.
	 * @return Tags managed by the model.
	 */
	Collection<ITag> getAllTags();

	/**
	 * Create a new tag with no fields set.
	 * @return Tag.
	 */
	ITag createTag();
	
	/**
	 * Store a tag in the database. The caller is responsible for ensuring a tag with the same name does not already
	 * exist.
	 * @param tag Tag to store.
	 */
	void store(ITag tag);
}
