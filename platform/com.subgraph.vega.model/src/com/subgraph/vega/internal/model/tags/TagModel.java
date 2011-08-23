package com.subgraph.vega.internal.model.tags;

import java.util.Collection;

import com.db4o.ObjectContainer;
import com.subgraph.vega.api.model.tags.ITag;
import com.subgraph.vega.api.model.tags.ITagModel;

public class TagModel implements ITagModel {
	private ObjectContainer database;

	public TagModel(ObjectContainer database) {
		this.database = database;
	}

	@Override
	public Collection<ITag> getAllTags() {
		return database.query(ITag.class);
	}

	@Override
	public ITag createTag() {
		return new Tag();
	}

	@Override
	public void store(ITag tag) {
		database.store(tag);
	}

}
