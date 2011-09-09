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

/**
 * A tag is a unique single word identifier that can be applied to a record such as a web transaction or a scan to mark
 * it as significant and to simplify later searching.
 */
public interface ITag {
	/**
	 * Set the name identifier for the tag.
	 * @param name Tag name.
	 */
	void setName(String name);

	/**
	 * Get the name identifier for the tag.
	 * @return Tag name.
	 */
	String getName();
	
	/**
	 * Set the description for the tag. Can be null.
	 * @param description Tag description. Can be null.
	 */
	void setDescription(String description);

	/**
	 * Get the description for the tag. May be null.
	 * @return Tag description. May be null.
	 */
	String getDescription();

	/**
	 * Set the 24-bit sRGB color to be applied to the tag name in certain displays.
	 * @param color sRGB color for the tag name.
	 */
	void setNameColor(int color);

	/**
	 * Get the 24-bit sRGB color for the tag name.
	 * @return sRGB color for the tag name.
	 */
	int getNameColor();

	/**
	 * Ser the 24-bit sRGB color to be applied to the background of rows in a table the tag is applied to.
	 * @param color sRGB color for table rows the tag is applied to.
	 */
	void setRowColor(int color);

	/**
	 * Get the 24-bit sRGB color for tagged table rows.
	 * @return sRGB color for tagged table rows.
	 */
	int getRowColor();
}
