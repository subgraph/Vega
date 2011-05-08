package com.subgraph.vega.ui.httpviewer.syntax;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;

import com.subgraph.vega.ui.httpviewer.Colors;
import com.subgraph.vega.ui.httpviewer.syntax.rules.EncodedCharacterRule;
import com.subgraph.vega.ui.httpviewer.syntax.rules.HttpVersionRule;
import com.subgraph.vega.ui.httpviewer.syntax.rules.RequestMethodRule;

public class RequestLineScanner extends AbstractScanner {

	public RequestLineScanner(Colors colors) {
		super(colors);
	}

	@Override
	protected void initializeRules(List<IRule> rules) {
		rules.add(new RequestMethodRule(createToken(Colors.REQUEST_VERB)));
		rules.add(new EncodedCharacterRule(createToken(Colors.ENCODED_CHAR)));
		rules.add(new HttpVersionRule(createToken(Colors.HTTP_VERSION)));
	}

}
