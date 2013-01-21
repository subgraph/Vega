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
package com.subgraph.vega.internal.ui.http.requestlogviewer;

import java.util.Map;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.subgraph.vega.api.model.requests.IRequestLogRecord;

public class HttpViewLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
	private final Map<Integer, Color> colorMap = new TreeMap<Integer, Color>();
	
	@Override
	public void dispose() {
		for (Color color: colorMap.values()) {
			color.dispose();
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof IRequestLogRecord))
			return null;
		final IRequestLogRecord record = (IRequestLogRecord) element;
		switch(columnIndex) {
		case 0:
			return Long.toString(record.getRequestId());
		case 1:
			return record.getHttpHost().toURI();
		case 2:
			return record.getRequest().getRequestLine().getMethod();
		case 3:
			return record.getRequest().getRequestLine().getUri();
		case 4:
			return Integer.valueOf(record.getResponse().getStatusLine().getStatusCode()).toString();
		case 5:
			return getResponseLength(record.getResponse());
		case 6:
			return Long.toString(record.getRequestMilliseconds());
		case 7:
			final int count = record.getTagCount();
			if (count != 0) {
				return Integer.toString(count);
			} else {
				return "";
			}
		}
		return null;
	}
	
	public String getResponseLength(HttpResponse response) {
		final Header lengthHeader = response.getFirstHeader("Content-Length");
		if(lengthHeader != null)
			return lengthHeader.getValue();
		
		if(response.getEntity() == null)
			return "";
		
		return Long.toString(response.getEntity().getContentLength());
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		IRequestLogRecord record = (IRequestLogRecord) element;
		if (record.getTagCount() != 0) {
			return getColorInverse(record.getTag(0).getRowColor());
		}
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		IRequestLogRecord record = (IRequestLogRecord) element;
		if (record.getTagCount() != 0) {
			return getColor(record.getTag(0).getRowColor());
		}
		return null;
	}

	private Color getColor(int colorCode) {
		Color color = colorMap.get(colorCode);
		if (color == null) {
			color = new Color(Display.getCurrent(), (colorCode >> 16) & 0xff, (colorCode >> 8) & 0xff, colorCode & 0xff);
			colorMap.put(colorCode, color);
		}
		return color;
	}
	
	// REVISIT: this isn't very nice.
	private Color getColorInverse(int colorCode) {
		final int inverseR = 255 - ((colorCode >> 16) & 0xff);
		final int inverseG = 255 - ((colorCode >> 8) & 0xff);
		final int inverseB = 255 - (colorCode & 0xff);

		return getColor(inverseR << 16 | inverseG << 8 | inverseB);
	}
	
}
