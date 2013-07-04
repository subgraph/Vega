package com.subgraph.vega.impl.scanner.urls;

import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;

public class SQLErrorMessageDetector {
	private final static String SQL_INJECTION_ALERT = "vinfo-sql-error";
	private final static List<String> ERROR_STRINGS = Arrays.asList(
			
			"Incorrect syntax near",                             // MS-SQL Server
			"Unclosed quotation mark",					         // MS-SQL Server
			"Dynamic SQL Error",						         // MS-SQL Server
			"SqlClient.SqlException: Syntax error",				 // ASP.Net / MS SQL Server
			"[Microsoft][ODBC SQL Server Driver]",               // MS Generic ODBC Error
			"Microsoft OLE DB Provider for ODBC Drivers</font>", // MS OLE 
			"Microsoft OLE DB Provider for ODBC Drivers</FONT>", // MS OLE

			"Syntax error in string in query expression",        // MS Access
			
			"<b>Warning</b>:  MySQL: ",                          // MySQL
			"You have an error in your SQL syntax",              // MySQL
			"supplied argument is not a valid MySQL",            // MySQL
			
			"PostgreSQL query failed:",                          // PostgreSQL
			"unterminated quoted string at or near",             // PostgreSQL
			"syntax error at or near",                           // PostgreSQL
			"invalid input syntax for integer:",		         // PostgreSQL
			"Query failed: ERROR: syntax error",		         // PostgreSQL
			
			"Unexpected end of command in statement",	         // Java
			"java.sql.SQLException:",					         // Java
			
			"quoted string not properly terminated",	         // Oracle
			"SQL command not properly ended",                    // Oracle
			"unable to perform query",
			
			"[DM_QUERY_E_SYNTAX]",						         // DQL
			
			"[Macromedia][SQLServer JDBC Driver]",				 // CF
			
			"DB2 SQL Error:"     								 // DB2
			
			
			);
	
	private final ResponseAnalyzer responseAnalyzer; 
	
	public SQLErrorMessageDetector(ResponseAnalyzer responseAnalyzer) {
		this.responseAnalyzer = responseAnalyzer;
	}
	
	public void detectErrorMessages(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		final String body = response.getBodyAsString();
		if(body == null || body.isEmpty()) {
			return;
		}
		for(String errorString: ERROR_STRINGS) {
			if(body.contains(errorString)) {
				processDetectedErrorMessage(ctx, request, response, errorString);
			}
		}
	}
	
	private void processDetectedErrorMessage(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response, String errorString) {
		ctx.addStringHighlight(errorString);
		responseAnalyzer.alert(ctx, SQL_INJECTION_ALERT, "SQL Error Message Detected", request, response, null);
	}
}
