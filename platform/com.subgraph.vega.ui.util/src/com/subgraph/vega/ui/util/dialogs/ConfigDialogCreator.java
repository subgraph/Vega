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
package com.subgraph.vega.ui.util.dialogs;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.internal.ui.util.PopupConfigDialog;
import com.subgraph.vega.internal.ui.util.TitleAreaConfigDialog;
import com.subgraph.vega.ui.util.Activator;
import com.subgraph.vega.ui.util.preferencepage.IPreferenceConstants;

public class ConfigDialogCreator {
	
	public static Window createDialog(ExecutionEvent event, IConfigDialogContent content) {
		final Shell parentShell = HandlerUtil.getActiveShell(event);
		return createDialog(parentShell, eventToPoint(event), content);
	}
	
	public static Window createDialog(ToolItem toolItem, IConfigDialogContent content) {
		final Shell parentShell = toolItem.getParent().getShell();
		return createDialog(parentShell, toolItemToPoint(toolItem), content);
	}
	
	public static Window createDialog(Shell parentShell, Point origin, IConfigDialogContent content) {
		boolean popup = Activator.getDefault().getPreferenceStore().getBoolean(IPreferenceConstants.P_CONFIG_POPUP);
		if(popup) {
			return new PopupConfigDialog(parentShell, origin, content);
		} else {
			return new TitleAreaConfigDialog(parentShell, content);
		}
	}

	private static Point eventToPoint(ExecutionEvent event) {
		if(event.getTrigger() instanceof Event) {
			final Event e = (Event) event.getTrigger();
			if(e.widget instanceof ToolItem) {
				return toolItemToPoint((ToolItem) e.widget);
			}
		}
		return new Point(100, 100);
	}
	
	private static Point toolItemToPoint(ToolItem toolItem) {
		final int x = toolItem.getBounds().x;
		final int y = toolItem.getBounds().y + toolItem.getBounds().height;
		return toolItem.getDisplay().map(toolItem.getParent(), null, x, y);
	}
}
