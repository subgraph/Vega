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
package com.subgraph.vega.ui.httpeditor.js.formatter;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.ui.httpeditor.js.formatter.FormatterState.Mode;
import com.subgraph.vega.ui.httpeditor.js.formatter.JavascriptFormatterConfig.BraceStyle;

/*
 * Adapted from http://jsbeautifier.org/
 */
public class JavascriptFormatter {
	private final static String[] PUNCTUATION = { "++", "--", "+=", "-=", "*=", "/=", "%=", "===", "==", "!==", "!=", ">>>=", ">>>", ">>=", "<<=", ">=", "<=", ">>", "<<",
		">", "<", "&&", "&=", "||", "!=", "|", "!!", "!", ",", "::", "^=", "^", ":", "=", "+", "-", "*", "/", "%", "&"};
	private final static String[] LINE_STARTERS = { "continue", "try", "throw", "return", "var", "if", "switch", "case", "default", "for", "while", "break", "function" };
	
	private final static int PREFIX_NONE = 0;
	private final static int PREFIX_NEWLINE = 1;
	private final static int PREFIX_SPACE = 2;
	
	private final static int CHAR_EOF = -1;
	
	private enum TokenType { TK_START_EXPR, TK_END_EXPR, TK_START_BLOCK, TK_END_BLOCK, TK_WORD,
		TK_SEMICOLON, TK_STRING, TK_EQUALS, TK_OPERATOR, TK_BLOCK_COMMENT, TK_INLINE_COMMENT, 
		TK_COMMENT, TK_EOF, TK_UNKNOWN
	}
	static class Token {
		final TokenType type;
		final String text;
		Token(TokenType type, int c) {
			this.type = type;
			this.text = String.valueOf((char)c);
		}
		Token(TokenType type, String text) {
			this.type = type;
			this.text = text;
		}
	}
	
	private StringBuilder output;
	
	private String lastWord = "";
	private String secondLastText = "";
	private String lastText = "";
	private TokenType lastTokenType = TokenType.TK_START_EXPR;
	private String input;
	private int pos;
	private int newlineCount = 0;
	private boolean wantedNewline;
	private boolean justAddedNewline;
	private boolean doBlockJustClosed;
	private String indentString; 
	private FormatterState state = new FormatterState(Mode.BLOCK);
	private List<FormatterState> stateStack = new ArrayList<FormatterState>();
	private JavascriptFormatterConfig config = new JavascriptFormatterConfig();
	public String format(String input) {
		output = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < config.indentCount; i++) {
			sb.append(' ');
		}
		indentString = sb.toString();
		reset(input);
		Token token = nextToken();
		while(token.type != TokenType.TK_EOF) {
			switch(token.type) {
			case TK_START_EXPR:
				handleStartExpr(token.text);
				break;
			case TK_END_EXPR:
				handleEndExpr(token.text);
				break;
			case TK_START_BLOCK:
				handleStartBlock(token.text);
				break;
			case TK_END_BLOCK:
				handleEndBlock(token.text);
				break;
			case TK_WORD:
				handleWord(token.text);
				break;
			case TK_SEMICOLON:
				handleSemicolon(token.text);
				break;
			case TK_STRING:
				handleString(token.text);
				break;
			case TK_EQUALS:
				handleEquals(token.text);
				break;
			case TK_OPERATOR:
				handleOperator(token.text);
				break;
			case TK_BLOCK_COMMENT:
				handleBlockComment(token.text);
				break;
			case TK_INLINE_COMMENT:
				handleInlineComment(token.text);
				break;
			case TK_COMMENT:
				handleComment(token.text);
				break;
			case TK_UNKNOWN:
				handleUnknown(token.text);
				break;
			case TK_EOF:
				break;
			default:
				break;
			}
			pushToken(token);
			token = nextToken();
		}
		
		return output.toString();
	}
	
	private void pushToken(Token token) {
		secondLastText = lastText;
		lastText = token.text;
		lastTokenType = token.type;
	}
	private void reset(String input) {
		pos = 0;
		this.input = input;
	}
	
	private Token nextToken() {
		newlineCount = 0;
		if(pos >= input.length())
			return new Token(TokenType.TK_EOF, "");
		wantedNewline = false;
		int c = input.charAt(pos);
		pos += 1;
		
		boolean keepWhitespace = (config.keepArrayIndentation && state.mode.isArray);
		if(keepWhitespace) {
			int whitespaceCount = 0;
			while(Character.isWhitespace(c)) {
				if(c == '\n') {
					trimOutput();
					output.append("\n");
					justAddedNewline = true;
					whitespaceCount = 0;
				} else if(c == '\t') {
					whitespaceCount += 4;
				} else if(c == '\r') {
					// do nothing
				} else {
					whitespaceCount += 1;
				}
				if(pos >= input.length()) {
					return new Token(TokenType.TK_EOF, "");
				}
				c = input.charAt(pos);
				pos += 1;
			}
			if(state.indentationBaseline == -1) {
				state.indentationBaseline = whitespaceCount;
			}
			if(justAddedNewline) {
				for(int i = 0; i < state.indentationLevel + 1; i++) {
					output.append(indentString);
				}
				if(state.indentationBaseline != -1) {
					for(int i = 0; i < whitespaceCount - state.indentationBaseline; i++) {
						output.append(' ');
					}
				}
			}
		} else {
			while(Character.isWhitespace(c)) {
				if(c == '\n') {
					if(config.maxPreserveNewline == 0 || config.maxPreserveNewline > newlineCount)
						newlineCount += 1;
				}
				if(pos >= input.length())
					return new Token(TokenType.TK_EOF, "");
				c = input.charAt(pos);
				pos += 1;
			}
			
			if(config.preserveNewlines && newlineCount > 1) {
				for(int i = 0; i < newlineCount; i++) {
					printNewline(i == 0);
					justAddedNewline = true;
				}
			}
			wantedNewline = newlineCount > 0;
		}
	
		if(Character.isJavaIdentifierStart(c)) {
			StringBuilder text = new StringBuilder();
			text.append((char)c);

			if(pos < input.length()) {
				while(Character.isJavaIdentifierPart(input.charAt(pos))) {
					text.append(input.charAt(pos));
					pos += 1;
					if(pos == input.length())
						break;
				}
			}
			if(!atEnd() && text.toString().matches("^[0-9]+[Ee]$") && (peek() == '-' || peek() == '+')) {
				int sign = next();
				Token t = nextToken();
				text.append((char) sign);
				text.append(t.text);
				return new Token(TokenType.TK_WORD, text.toString());
			}

			if(text.toString().equals("in")) {
				return new Token(TokenType.TK_WORD, text.toString());
			}
			
			if(wantedNewline && notLastToken(TokenType.TK_OPERATOR, TokenType.TK_EQUALS)
					&& !state.ifLine && (config.preserveNewlines || !"var".equals(lastText))) {
				printNewline();
			}
			return new Token(TokenType.TK_WORD, text.toString());				
		}
		
		switch(c) {
		case '(':
		case '[':
			return new Token(TokenType.TK_START_EXPR, c);
		case ')':
		case ']':
			return new Token(TokenType.TK_END_EXPR, c);
		case '{':
			return new Token(TokenType.TK_START_BLOCK, c);
		case '}':
			return new Token(TokenType.TK_END_BLOCK, c);
		case ';':
			return new Token(TokenType.TK_SEMICOLON, c);
		case '/':
			Token t =  maybeCommentToken();
			if(t != null)
				return t;
		
		}

		boolean isRegex = 
			c == '/' 
			&& ((lastTokenType == TokenType.TK_WORD && ("return".equals(lastText) || "do".equals(lastText))
					|| (lastTokenType == TokenType.TK_COMMENT || lastTokenType == TokenType.TK_START_EXPR 
						|| lastTokenType == TokenType.TK_START_BLOCK || lastTokenType == TokenType.TK_END_BLOCK ||
						lastTokenType == TokenType.TK_OPERATOR || lastTokenType == TokenType.TK_EQUALS || lastTokenType == TokenType.TK_EOF ||
						lastTokenType == TokenType.TK_SEMICOLON)));
		if(c == '"' || c == '\'' || isRegex) {
			int sep = c;
			boolean esc = false;
			StringBuilder sb = new StringBuilder();
			sb.append((char) c);
			if(pos < input.length()) {
				if(sep == '/') {
					boolean inCharClass = false;
					while(esc || inCharClass || input.charAt(pos) != sep) {
						sb.append(input.charAt(pos));
						if(!esc) {
							esc = input.charAt(pos) == '\\';
							if(input.charAt(pos) == '[') {
								inCharClass = true;
							} else if(input.charAt(pos) == ']') {
								inCharClass = false;
							}
						} else {
							esc = false;
						}
						pos += 1;
						if(pos >= input.length()) {
							return new Token(TokenType.TK_STRING, sb.toString());
						}
					}
				} else {
					while(esc || input.charAt(pos) != sep) {
						sb.append(input.charAt(pos));
						if(!esc) {
							esc = input.charAt(pos) == '\\';
						} else {
							esc = false;
						}
						pos += 1;
						if(pos >= input.length()) {
							return new Token(TokenType.TK_STRING, sb.toString());
						}
					}
				}
			}
			pos += 1;
			sb.append((char) sep);
			
			if(sep == '/') {
				while(pos < input.length() && Character.isJavaIdentifierPart(input.charAt(pos))) {
					sb.append(input.charAt(pos));
					pos += 1;
				}
			}
			return new Token(TokenType.TK_STRING, sb.toString());
		}
		
		if(c == '#') {
			if(output.length() == 0 && peek() == '!') {
				StringBuilder sb = new StringBuilder();
				sb.append((char) c);
				while(!atEnd() && c != '\n') {
					c = next();
					sb.append((char) c);
				}
				output.append(sb.toString().trim());
				output.append('\n');
				printNewline();
				return nextToken();
			}
			
			if(!atEnd() && Character.isDigit(peek())) {
				StringBuilder sb = new StringBuilder();
				sb.append('#');
				do {
					c = next();
					sb.append((char) c);
				} while(pos < input.length() && c != '#' && c != '=');
				if(c == '#') {
					//
				} else if(peek() == '[' && input.charAt(pos + 1) == ']') {
					sb.append("[]");
					pos += 2;
				} else if(peek() == '{' && input.charAt(pos + 1) == '}') {
					sb.append("{}");
					pos += 2;
				}
				return new Token(TokenType.TK_WORD, sb.toString());
			}
		}
		if(c == '<' && peekStr(3).equals("!--")) {
			pos += 3;
			state.inHtmlComment = true;
			return new Token(TokenType.TK_COMMENT, "<!--");
		}
		if(c == '-' && state.inHtmlComment && peekStr(2).equals("->")) {
			state.inHtmlComment = false;
			pos += 2;
			if(wantedNewline)
				printNewline();
			return new Token(TokenType.TK_COMMENT, "-->");
		}
		
		for(String p: PUNCTUATION) {
			int plen = p.length();
			if(c == p.charAt(0)) {
				if(plen == 1 || p.substring(1).equals(peekStr(plen - 1))) {
					pos += plen - 1;
					if(c == '=')
						return new Token(TokenType.TK_EQUALS, p);
					else
						return new Token(TokenType.TK_OPERATOR, p);
				}
			}
		}	
		return new Token(TokenType.TK_UNKNOWN, c);
	}
	
	private boolean atEnd() {
		return pos >= input.length();
	}
	
	private int next() {
		if(atEnd())
			return CHAR_EOF;
		
		pos += 1;
		return input.charAt(pos - 1);
	}
	
	private String peekStr(int length) {
		if(pos + length > input.length()) {
			return "";
		}
		return input.substring(pos, pos + length);
	}
	private int peek() {
		if(atEnd()) 
			return CHAR_EOF;
		return input.charAt(pos);
	}
	
	private Token maybeCommentToken() {
		if(peek() != '*' && peek() != '/')
			return null;
		
		TokenType type = TokenType.TK_INLINE_COMMENT;
		StringBuilder sb = new StringBuilder();
		int cc = next();
		sb.append('/');
		
		if(cc == '*') {
			while(!(cc == '*' && peek() == '/') && (cc != CHAR_EOF)) {
				sb.append((char) cc);
				if(cc == '\n' || cc == '\r')
					type = TokenType.TK_BLOCK_COMMENT;
				cc = next();
			}
			if(cc == '*') {
				next();
				sb.append("*/");
			}
			return new Token(type, sb.toString());
		} else {
			while(cc != '\n' && cc != '\r' && cc != CHAR_EOF) {
				sb.append((char) cc);
				cc = next();
			}
			return new Token(TokenType.TK_COMMENT, sb.toString());
		}
	}

	private void handleStartExpr(String text) {
		if(text.equals("[")) {
			if(lastTokenType == TokenType.TK_WORD || ")".equals(lastText)) {
				for(String ls: LINE_STARTERS) {
					if(ls.equals(text))
						printSingleSpace();
					setMode(Mode.PAREN_EXPR);
					printToken(text);
					return;
				}
			}
			if(state.mode == Mode.ARRAY_EXPR || state.mode == Mode.ARRAY_INDENTED_EXPR) {
				if("]".equals(secondLastText) && ",".equals(lastText)) {
					if(state.mode == Mode.ARRAY_EXPR) {
						state.mode = Mode.ARRAY_INDENTED_EXPR;
						if(!config.keepArrayIndentation) {
							indent();
						}
					}
				
					setMode(Mode.ARRAY_EXPR);
					if(!config.keepArrayIndentation) {
						printNewline();
					}
				} else if ("[".equals(lastText)) {
					if(state.mode == Mode.ARRAY_EXPR) {
						state.mode = Mode.ARRAY_INDENTED_EXPR;
						if(!config.keepArrayIndentation) {
							indent();
						}
					}
					setMode(Mode.ARRAY_EXPR);
					if(!config.keepArrayIndentation) {
						printNewline();
					}
				} else {
					setMode(Mode.ARRAY_EXPR);
				}
			
			} else {
				setMode(Mode.ARRAY_EXPR);
			}
		} else {
			setMode(Mode.ARRAY_EXPR);
		}
		if(";".equals(lastText) || lastTokenType == TokenType.TK_START_BLOCK) {
			printNewline();
		} else if(lastTokenType == TokenType.TK_END_EXPR || lastTokenType == TokenType.TK_START_EXPR || lastTokenType == TokenType.TK_END_BLOCK || ".".equals(lastText)) {
			// do nothing
		} else if(lastTokenType != TokenType.TK_WORD && lastTokenType != TokenType.TK_OPERATOR) {
			printSingleSpace();
		} else if("function".equals(lastWord) || "typeof".equals(lastWord)) {
			printSingleSpace();
		} else if("catch".equals(lastText)){
			printSingleSpace();
		} else {
			for(String ls: LINE_STARTERS) {
				if(ls.equals(lastText))
					printSingleSpace();
			}
		}
		printToken(text);
	}
	
	private void handleEndExpr(String text) {
		if("]".equals(text)) {
			if(config.keepArrayIndentation) {
				if("}".equals(lastText)) {
					removeIndent();
					printToken(text);
					restoreMode();
				}
			} else {
				if(state.mode == Mode.ARRAY_INDENTED_EXPR) {
					if("]".equals(lastText)) {
						restoreMode();
						printNewline();
						printToken(text);
					}
				}
			}
		} 
		restoreMode();
		printToken(text);
	}
	
	private void handleStartBlock(String text) {
		if(isLastWord("do")) {
			setMode(Mode.DO_BLOCK);
		} else {
			setMode(Mode.BLOCK);
		}
		
		if(config.braceStyle == BraceStyle.EXPAND) {
			if(notLastToken(TokenType.TK_OPERATOR)) {
				if(isLastText("return", "=")) {
					printSingleSpace();
				} else {
					printNewline(true);
				}
			}
			printToken(text);
			indent();
		} else {
			if(notLastToken(TokenType.TK_OPERATOR, TokenType.TK_START_EXPR)) {
				if(isLastToken(TokenType.TK_START_BLOCK)) {
					printNewline();
				} else {
					printSingleSpace();
				}
			} else {
				if(state.previousMode.isArray && isLastText(",")) {
					if(isSecondLastText("}")) {
						printSingleSpace();
					} else {
						printNewline();
					}
				}
			}
			indent();
			printToken(text);			
		}
	}
	
	private void handleEndBlock(String text) {
		restoreMode();
		if(config.braceStyle == BraceStyle.EXPAND) {
			if(notLastText("{")) {
				printNewline();
			}
			printToken(text);
		} else {
			if(isLastToken(TokenType.TK_START_BLOCK)) {
				if(justAddedNewline) {
					removeIndent();
				} else {
					trimOutput();
				}
			} else {
				if(state.mode.isArray && config.keepArrayIndentation) {
					config.keepArrayIndentation = false;
					printNewline();
					config.keepArrayIndentation = true;
				} else {
					printNewline();
				}
				
			}
			printToken(text);
		}
	}
	
	private void handleWord(String text) {
		if(doBlockJustClosed) {
			printSingleSpace();
			printToken(text);
			printSingleSpace();
			doBlockJustClosed = false;
			return;
		}
		if("function".equals(text)) {
			if(state.varLine) {
				state.varLineReindented = true;
			}
			if((justAddedNewline || isLastText(";")) && notLastText("{")) {
				newlineCount = (justAddedNewline) ? (newlineCount) : 0;
				if(!config.preserveNewlines) {
					newlineCount = 1;
				}
				for(int i = 0; i < (2 - newlineCount); i++) {
					printNewline(false);
				}
				
			}
		}
		
		if("case".equals(text) || "default".equals(text)) {
			if(isLastText(":")) {
				removeIndent();
			} else {
				state.indentationLevel -= 1;
				printNewline();
				state.indentationLevel += 1;
			}
			printToken(text);
			state.inCase = true;
			return;
		}
		int prefix = PREFIX_NONE;
		String ltext = text.toLowerCase();
		if(isLastToken(TokenType.TK_END_BLOCK)) {
			if(!("else".equals(ltext) || "catch".equals(ltext) || "finally".equals(ltext))) {
				prefix = PREFIX_NEWLINE;
			} else {
				if(config.braceStyle == BraceStyle.EXPAND || config.braceStyle == BraceStyle.END_EXPAND) {
					prefix = PREFIX_NEWLINE;
				} else {
					prefix = PREFIX_SPACE;
					printSingleSpace();
				}
			}
		} else if (isLastToken(TokenType.TK_SEMICOLON) && (state.mode == Mode.BLOCK || state.mode == Mode.DO_BLOCK)) {
			prefix = PREFIX_NEWLINE;
		} else if(isLastToken(TokenType.TK_SEMICOLON) && state.mode.isExpression) {
			prefix = PREFIX_SPACE;
		} else if(isLastToken(TokenType.TK_STRING)) {
			prefix = PREFIX_NEWLINE;
		} else if(isLastToken(TokenType.TK_WORD)) {
			if(isLastText("else")) {
				trimOutput(true);
			}
			prefix = PREFIX_SPACE;
		} else if(isLastToken(TokenType.TK_START_BLOCK)) {
			prefix = PREFIX_NEWLINE;
		} else if(isLastToken(TokenType.TK_END_EXPR)) {
			printSingleSpace();
			prefix = PREFIX_NEWLINE;
		}
		
		if(containsString(LINE_STARTERS, text) && notLastText(")")) {
			if(isLastText("else")) {
				prefix = PREFIX_SPACE;
			} else {
				prefix = PREFIX_NEWLINE;
			}
		}
		
		if(state.ifLine && isLastToken(TokenType.TK_END_EXPR)) {
			state.ifLine = false;
		}
		
		if(containsString(new String[] { "else", "catch", "finally"}, ltext)) {
			if(notLastToken(TokenType.TK_END_BLOCK) || config.braceStyle == BraceStyle.EXPAND || config.braceStyle == BraceStyle.END_EXPAND) {
				printNewline();
			} else {
				trimOutput(true);
				printSingleSpace();
			}
		} else if(prefix == PREFIX_NEWLINE) {
			if((isLastToken(TokenType.TK_START_EXPR) || isLastText("=", ",")) && "function".equals(text)) {
				// do nothing
			} else if ("function".equals(text) && isLastText("new")) {
				printSingleSpace();
			} else if(isLastText("return", "throw")) {
				printSingleSpace();
			} else if(notLastToken(TokenType.TK_END_EXPR)) {
				if((notLastToken(TokenType.TK_START_EXPR) || !"var".equals(text)) && notLastText(":")) {
					if("if".equals(text) && isLastWord("else") && notLastText("{")) {
						printSingleSpace();
					} else {
						state.varLine = false;
						state.varLineReindented = false;
						printNewline();
					}
				}
			
			} else if(containsString(LINE_STARTERS, text) && notLastText(")")) {
				state.varLine = false;
				state.varLineReindented = false;
				printNewline();
			}
			
		} else if(state.mode.isArray && isLastText(",") && isSecondLastText(")")) {
			printNewline();
		} else if(prefix == PREFIX_SPACE) {
			printSingleSpace();
		}
		
		printToken(text);
		lastWord = text;
		
		if("var".equals(text)) {
			state.varLine = true;
			state.varLineReindented = false;
			state.varLineTainted = false;
		} else if("if".equals(text)) {
			state.ifLine = true;
		} else if("else".equals(text)) {
			state.ifLine = false;
		}
	}
	
	private void handleSemicolon(String text) {
		printToken(text);
		state.varLine = false;
		state.varLineReindented = false;
		if(state.mode == Mode.OBJECT) {
			state.mode = Mode.BLOCK;
		}
	}
	
	private void handleString(String text) {
		if(isLastToken(TokenType.TK_START_BLOCK, TokenType.TK_END_BLOCK, TokenType.TK_SEMICOLON)) {
			printNewline();
		} else if(isLastToken(TokenType.TK_WORD)) {
			printSingleSpace();
		}
		printToken(text);
	}
	
	private void handleEquals(String text) {
		if(state.varLine) {
			state.varLineTainted = true;
		}
		printSingleSpace();
		printToken(text);
		printSingleSpace();
	}
	
	private void handleOperator(String text) {
		boolean spaceBefore = true;
		boolean spaceAfter = true;
		if(state.varLine && ",".equals(text) && state.mode.isExpression) {
			state.varLineTainted = false;
		}
		if(state.varLine) {
			if(",".equals(text)) {
				if(state.varLineTainted) {
					printToken(text);
					state.varLineReindented = true;
					state.varLineTainted = false;
					printNewline();
					return;
				} else {
					state.varLineTainted = false;
				}
			}
		}
		
		if(isLastText("return", "throw")) {
			printSingleSpace();
			printToken(text);
			return;
		}
		
		if(":".equals(text) && state.inCase) {
			printToken(text);
			printNewline();
			state.inCase = false;
			return;
		}
		
		if("::".equals(text)) {
			printToken(text);
			return;
		}
		
		if(",".equals(text)) {
			if(state.varLine) {
				if(state.varLineTainted) {
					printToken(text);
					printNewline();
					state.varLineTainted = false;
				} else {
					printToken(text);
					printSingleSpace();
				}
			} else if(isLastToken(TokenType.TK_END_BLOCK) && state.mode != Mode.PAREN_EXPR) {
				printToken(text);
				if(state.mode == Mode.OBJECT && isLastText("}")) {
					printNewline();
				} else {
					printSingleSpace();
				}
			} else {
				if(state.mode == Mode.OBJECT) {
					printToken(text);
					printNewline();
				} else {
					printToken(text);
					printSingleSpace();
				}
			}
			return;
		} else if (containsString(new String[] {"--", "++", "!"}, text)
				|| ( containsString(new String[] { "-", "+" }, text) 
						&& isLastToken(TokenType.TK_START_BLOCK, TokenType.TK_START_EXPR, TokenType.TK_EQUALS, TokenType.TK_OPERATOR) 
						|| isLastText(LINE_STARTERS))) {
			spaceBefore = false;
			spaceAfter = false;
			if(isLastText(";") && state.mode.isExpression) {
				spaceBefore = true;
			}
			if(isLastToken(TokenType.TK_WORD) && isLastText(LINE_STARTERS)) {
				spaceBefore = true;
			}
			if(state.mode == Mode.BLOCK && isLastText("{", ";")) {
				printNewline();
			}
		} else if(".".equals(text)) {
			spaceBefore = false;
		} else if(":".equals(text)) {
			if(state.ternaryDepth == 0) {
				state.mode = Mode.OBJECT;
				spaceBefore = false;
			} else {
				state.ternaryDepth -= 1;
			}
		} else if("?".equals(text)) {
			state.ternaryDepth += 1;
		}
		if(spaceBefore) {
			printSingleSpace();
		}
		printToken(text);
		if(spaceAfter) {
			printSingleSpace();
		}
		
	}
	
	private void handleBlockComment(String text) {
		final String[] lines = text.split("\\r?\\n");
		if(text.matches("^/\\*\\*")) {
			for(int i = 0; i < lines.length; i++) {
				printNewline();
				if(i > 0)
					output.append(" ");
				output.append(lines[i].trim());
			}
		} else {
			if(lines.length > 1) {
				printNewline();
				trimOutput();
			} else {
				printSingleSpace();
			}
			for(int i = 0; i < lines.length; i++) {
				output.append(lines[i]);
				output.append("\n");
			}
		}
		printNewline();
	}
	
	private void handleInlineComment(String text) {
		printSingleSpace();
		printToken(text);
		if(state.mode.isExpression) {
			printSingleSpace();
		} else {
			printNewline();
		}
		
	}
	
	private void handleComment(String text) {
		if(wantedNewline) {
			printNewline();
		} else {
			printSingleSpace();
		}
		printToken(text);
		printNewline();
	}
	
	private void handleUnknown(String text) {
		if(isLastText("return", "throw")) {
			printSingleSpace();
		}
		printToken(text);
	}
	
	private void trimOutput() {
		trimOutput(false);
	}
	
	private void trimOutput(boolean eatNewlines) {
		while(endsWith(' ') || endsWithIndent() || (eatNewlines && (endsWith('\n') || endsWith('\r')))) {
			dropLast();
		}
	}
	
	private void printNewline() {
		printNewline(true);
	}
	private void printNewline(boolean ignoreRepeated) {
		state.eatNextSpace = false;
		if(config.keepArrayIndentation && state.mode.isArray) {
			return;
		}
		trimOutput();
		if(outputEmpty())
			return;
		if(lastOutputChar() != '\n' || !ignoreRepeated) {
			justAddedNewline = true;
			output.append("\n");
		}
		for(int i = 0; i < state.indentationLevel; i++) {
			output.append(indentString);
		}
		if(state.varLine && state.varLineReindented) {
			if(config.indentChar == ' ') {
				output.append("    ");
			} else {
				output.append(indentString);
			}
		}
		
		
	}
	private void printSingleSpace() {
		if(state.eatNextSpace) {
			state.eatNextSpace = false;
			return;
		}
		char last = ' ';
		if(output.length() > 0) {
			last = output.charAt(output.length() - 1);
		}
		if(last != ' ' && last != '\n' && !(indentString.length() == 1 && indentString.charAt(0) == last)) {
			output.append(' ');
		}
	}
	
	private void printToken(String text) {
		justAddedNewline = false;
		state.eatNextSpace = false;
		output.append(text);
	}
	
	private void indent() {
		state.indentationLevel += 1;
	}
	
	private void removeIndent() {
		if(output.length() > 0 && matchIndentString(lastOutputChar())) {
			dropLast();
		}
	}
	
	private void setMode(FormatterState.Mode mode) {
		if(state != null) {
			stateStack.add(state);
		}
		state = new FormatterState(config, mode, state);
	}
	
	private void restoreMode() {
		doBlockJustClosed = (state.mode == Mode.DO_BLOCK);
		if(stateStack.size() > 0) {
			state = stateStack.remove(stateStack.size() - 1);
		} 
	}
	
	private boolean endsWith(char c) {
		if(outputEmpty())
			return false;
		return lastOutputChar() == c;
	}
	private boolean endsWithIndent() {
		if(outputEmpty())
			return false;
		return matchIndentString(lastOutputChar());
	}

	private boolean matchIndentString(char c) {
		return indentString.length() == 1 && indentString.charAt(0) == c;
	}
	
	private char lastOutputChar() {
		return output.charAt(output.length() - 1);
	}
	
	private boolean outputEmpty() {
		return output.length() == 0;
	}
	
	private void dropLast() {
		if(!outputEmpty()) {
			output.deleteCharAt(output.length() - 1);
		}		
	}

	private boolean containsString(String[] haystack, String needle) {
		for(String s: haystack)
			if(s.equals(needle))
				return true;
		return false;
	}
	private boolean notLastToken(TokenType ... types) {
		for(TokenType tt: types) {
			if(lastTokenType == tt)
				return false;
		}
		return true;
	}
	
	private boolean isLastToken(TokenType ...types) {
		for(TokenType tt: types) {
			if(lastTokenType == tt)
				return true;
		}
		return false;
	}
	
	private boolean isLastText(String ... strings) {
		for(String s: strings) {
			if(s.equals(lastText)) 
				return true;
		}
		return false;
	}

	private boolean notLastText(String ...strings) {
		for(String s: strings) {
			if(s.equals(lastText))
				return false;
		}
		return true;
	}
	private boolean isSecondLastText(String text) {
		return text.equals(secondLastText);
	}
	
	private boolean isLastWord(String text) {
		return text.equals(lastWord);
	}
	
}
