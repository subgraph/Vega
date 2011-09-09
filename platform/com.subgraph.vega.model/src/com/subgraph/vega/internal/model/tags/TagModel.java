package com.subgraph.vega.internal.model.tags;

import java.util.Collection;
import java.util.List;

import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
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
	public ITag createTag(ITag tag) {
		return new Tag(tag);
	}

	@Override
	public void store(ITag tag) {
		database.store(tag);
	}

	@Override
	public boolean isTagStored(ITag tag) {
		return database.ext().isStored(tag);
	}

	@Override
	public ITag getTagByName(final String name) {
		final List<ITag> results = database.query(new Predicate<ITag>() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean match(ITag tag) {
				return name.equals(tag.getName());
			}
		});
		if (results.size() == 0) {
			return null;
		}
		return results.get(0);
	}
	
}
