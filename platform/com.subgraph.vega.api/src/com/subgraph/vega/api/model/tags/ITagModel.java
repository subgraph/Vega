package com.subgraph.vega.api.model.tags;

import java.util.Collection;

public interface ITagModel {
	/**
	 * Get all tags managed by the model.
	 * @return Tags managed by the model.
	 */
	Collection<ITag> getAllTags();

	/**
	 * Create a new tag with no fields set. The tag is not stored to the workspace.
	 * @return Tag.
	 */
	ITag createTag();
	
	/**
	 * Create a copy of a tag. The copy is not stored to the workspace.
	 * @return Copy of tag.
	 */
	ITag createTag(ITag tag);
	
	/**
	 * Store a tag in the workspace. The caller is responsible for ensuring a tag with the same name does not already
	 * exist.
	 * @param tag Tag to store.
	 */
	void store(ITag tag);

	/**
	 * Determine whether a tag exists in the workspace.
	 * @param tag Tag.
	 * @return Boolean indicating whether the tag exists in the workspace.
	 */
	boolean isTagStored(ITag tag);

	/**
	 * Lookup a tag in the workspace by name. The lookup is case-insensitive.
	 * @param name Tag, or null if none exists with the given name.
	 */
	ITag getTagByName(String name);
}
