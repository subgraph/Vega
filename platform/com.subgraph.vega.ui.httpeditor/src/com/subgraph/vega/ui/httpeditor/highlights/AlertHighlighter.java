package com.subgraph.vega.ui.httpeditor.highlights;


import java.util.Collection;

import org.eclipse.jface.text.source.projection.ProjectionViewer;

import com.subgraph.vega.api.model.alerts.IScanAlertHighlight;
import com.subgraph.vega.ui.httpeditor.Colors;

public class AlertHighlighter {
	private final static String ANNOTATION_HIGHLIGHT = "httpeditor.highlight";
	
	private final MatchHighlighter highlighter;
	
	public AlertHighlighter(ProjectionViewer viewer, Colors colors) {
		highlighter = new MatchHighlighter(viewer, colors.get(Colors.ALERT_HIGHLIGHT), 
				ANNOTATION_HIGHLIGHT, false);
	}

	public void clearHighlights() {
		highlighter.clearMatches();
	}

	public void addAlertHighlights(Collection<IScanAlertHighlight> highlights) {
		for(IScanAlertHighlight h: highlights) {
			highlighter.searchMatches(h.getMatchString(), h.isRegularExpression());
		}
	}
	
	public void displayHighlights() {
		highlighter.displayFirstMatch();
	}
}
