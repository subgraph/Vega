package com.subgraph.vega.ui.httpviewer.syntax;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;

import com.subgraph.vega.ui.httpviewer.Colors;
import com.subgraph.vega.ui.httpviewer.syntax.rules.HttpVersionRule;
import com.subgraph.vega.ui.httpviewer.syntax.rules.IntegerRule;

public class ResponseLineScanner extends AbstractScanner {

	public ResponseLineScanner(Colors colors) {
		super(colors);
	}

	@Override
	protected void initializeRules(List<IRule> rules) {
		rules.add(new HttpVersionRule(createToken(Colors.HTTP_VERSION)));
		rules.add(new IntegerRule(createToken(Colors.INTEGER)));
	}

}
