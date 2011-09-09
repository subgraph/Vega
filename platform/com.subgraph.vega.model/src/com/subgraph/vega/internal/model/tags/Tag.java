package com.subgraph.vega.internal.model.tags;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;
import com.subgraph.vega.api.model.tags.ITag;

public class Tag implements ITag, Activatable {
	private transient Activator activator;
	private String name;
	private String description;
	private int nameColor;
	private int rowColor;

	public Tag() {
		rowColor = 0xffffff;
	}

	public Tag(ITag tag) {
		name = tag.getName();
		description = tag.getDescription();
		nameColor = tag.getNameColor();
		rowColor = tag.getRowColor();
	}

	@Override
	public void activate(ActivationPurpose activationPurpose) {
		if (activator != null) {
			activator.activate(activationPurpose);
		}				
	}

	@Override
	public void bind(Activator activator) {
		if (this.activator == activator) {
			return;
		}
		if (activator != null && this.activator != null) {
			throw new IllegalStateException("Object can only be bound to one activator");
		}
		this.activator = activator;			
	}

	@Override
	public void setName(String name) {
		activate(ActivationPurpose.READ);
		this.name = name;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getName() {
		activate(ActivationPurpose.READ);
		return name;
	}

	@Override
	public void setDescription(String description) {
		activate(ActivationPurpose.READ);
		this.description = description;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public String getDescription() {
		activate(ActivationPurpose.READ);
		return description;
	}

	@Override
	public void setNameColor(int color) {
		activate(ActivationPurpose.READ);
		nameColor = color;
		activate(ActivationPurpose.WRITE);
	}

	@Override
	public int getNameColor() {
		activate(ActivationPurpose.READ);
		return nameColor;
	}

	@Override
	public void setRowColor(int color) {
		activate(ActivationPurpose.READ);
		rowColor = color;		
		activate(ActivationPurpose.WRITE);
	}
	
	@Override
	public int getRowColor() {
		activate(ActivationPurpose.READ);
		return rowColor;
	}

}
