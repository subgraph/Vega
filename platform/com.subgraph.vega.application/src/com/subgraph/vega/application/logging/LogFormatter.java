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
package com.subgraph.vega.application.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	
	Date dat = new Date();
    private final static String format = "{0,time}";
    private MessageFormat formatter;
    private Object args[] = new Object[1];
    
	@Override
	public synchronized String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();
		dat.setTime(record.getMillis());
		args[0] = dat;
	
		StringBuffer text = new StringBuffer();
		if(formatter == null) {
			formatter = new MessageFormat(format);
		}
		formatter.format(args, text, null);
		sb.append(text);
		sb.append(" [");
		sb.append(record.getLevel().getLocalizedName());
		sb.append("] (");
		sb.append(record.getLoggerName());
		sb.append(") ");
		sb.append(formatMessage(record));
		sb.append("\n");
		
		if (record.getThrown() != null) {
		    try {
		        StringWriter sw = new StringWriter();
		        PrintWriter pw = new PrintWriter(sw);
		        record.getThrown().printStackTrace(pw);
		        pw.close();
			sb.append(sw.toString());
		    } catch (Exception ex) {
		    	sb.append("Exception caused by writing exception backtrace to message!  : "+ ex.getMessage());
		    }
		}
		return sb.toString();		
	}

}
