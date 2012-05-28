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
package com.subgraph.vega.ui.tagsl.taggablepopup;

import com.subgraph.vega.internal.ui.tags.taggableeditor.TagModifier;

public interface ITagModifierValidator {
	/**
	 * Validate changes made to a tag modifier.
	 * @param modifier Tag modifier.
	 * @return String describing the problem if the tag does not pass, otherwise null.
	 */
	String validate(TagModifier modifier);
}
