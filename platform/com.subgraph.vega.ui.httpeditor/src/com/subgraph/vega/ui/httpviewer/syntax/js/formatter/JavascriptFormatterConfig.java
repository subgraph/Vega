package com.subgraph.vega.ui.httpviewer.syntax.js.formatter;

public class JavascriptFormatterConfig {
	enum BraceStyle { COLLAPSE, EXPAND, END_EXPAND };
	BraceStyle braceStyle = BraceStyle.COLLAPSE;
	boolean preserveNewlines;
	boolean keepArrayIndentation;
	int indentCount = 4;
	int indentLevel = 0; // initial indentation
	int maxPreserveNewline;
	boolean bracesOnOwnLine;
	char indentChar;

}
