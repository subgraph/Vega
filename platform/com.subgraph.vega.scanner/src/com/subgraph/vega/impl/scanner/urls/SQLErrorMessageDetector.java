package com.subgraph.vega.impl.scanner.urls;

import org.apache.http.client.methods.HttpUriRequest;

import com.google.common.collect.ImmutableMap;
import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.scanner.IInjectionModuleContext;

public class SQLErrorMessageDetector {
	
	private static enum databaseErrorTypes {SQLSERVER, ASP, MS, ACCESS, MYSQL, POSTGRES, JAVA, ORACLE, DQL, CF, DB2}
	
	private final static ImmutableMap<databaseErrorTypes, String> DATABASE_ALERTS = ImmutableMap.<databaseErrorTypes, String>builder().put(
																													databaseErrorTypes.SQLSERVER, "vinfo-sqlserver-error").put(
																													databaseErrorTypes.ASP, "vinfo-sql-error").put(
																													databaseErrorTypes.MS, "vinfo-sql-error").put(
																													databaseErrorTypes.ACCESS, "vinfo-sql-error").put(
																													databaseErrorTypes.MYSQL, "vinfo-mysql-error").put(
																													databaseErrorTypes.POSTGRES, "vinfo-sql-error").put(
																													databaseErrorTypes.JAVA, "vinfo-sql-error").put(
																													databaseErrorTypes.ORACLE, "vinfo-sql-error").put(
																													databaseErrorTypes.DQL, "vinfo-sql-error").put(
																													databaseErrorTypes.CF, "vinfo-sql-error").put(
																													databaseErrorTypes.DB2, "vinfo-sql-error").build();
																													
	
	private final static ImmutableMap<String, databaseErrorTypes> ERROR_STRINGS = ImmutableMap.<String, databaseErrorTypes>builder().put(
																													"Incorrect syntax near",  databaseErrorTypes.SQLSERVER).put(
																								   					"Unclosed quotation mark", databaseErrorTypes.SQLSERVER).put(
																								   					"Dynamic SQL Error", databaseErrorTypes.SQLSERVER).put(			
																								   							
																								   					"SqlClient.SqlException: Syntax error", databaseErrorTypes.ASP).put(
																								   							
																								   				    "[Microsoft][ODBC SQL Server Driver]", databaseErrorTypes.MS).put(
																								   				    "Microsoft OLE DB Provider for ODBC Drivers</font>", databaseErrorTypes.MS).put(
																								   				    "Microsoft OLE DB Provider for ODBC Drivers</FONT>", databaseErrorTypes.MS).put(
																								   
																								   				    "Syntax error in string in query expression",  databaseErrorTypes.ACCESS).put(
																								   
																								   				    "<b>Warning</b>:  MySQL: ", databaseErrorTypes.MYSQL).put(
																								   				    "You have an error in your SQL syntax", databaseErrorTypes.MYSQL).put(
																								   				    "supplied argument is not a valid MySQL", databaseErrorTypes.MYSQL).put(
																								   
																								   				    "PostgreSQL query failed:", databaseErrorTypes.POSTGRES).put(
																								   				    "unterminated quoted string at or near", databaseErrorTypes.POSTGRES).put(
																								   				    "syntax error at or near", databaseErrorTypes.POSTGRES).put(
																								   				    "invalid input syntax for integer:",	databaseErrorTypes.POSTGRES).put(
																								   				    "Query failed: ERROR: syntax error",	databaseErrorTypes.POSTGRES).put(
																								  
																								   				    "Unexpected end of command in statement", databaseErrorTypes.JAVA).put(
																								   				    "java.sql.SQLException:", databaseErrorTypes.JAVA).put(
																								   				    "quoted string not properly terminated",	databaseErrorTypes.ORACLE).put(
																								   				    "SQL command not properly ended", databaseErrorTypes.ORACLE).put(
																								   				    "unable to perform query", databaseErrorTypes.ORACLE).put(
			
																								   				    "[DM_QUERY_E_SYNTAX]", databaseErrorTypes.DQL).put(
																								   
																								   				    "[Macromedia][SQLServer JDBC Driver]", databaseErrorTypes.CF).put(
																								   				    "[Macromedia][MySQL JDBC Driver]", databaseErrorTypes.CF).put(
			
																								   				    "DB2 SQL Error:", databaseErrorTypes.DB2).build();
			
	private final ResponseAnalyzer responseAnalyzer; 
	
	public SQLErrorMessageDetector(ResponseAnalyzer responseAnalyzer) {
		this.responseAnalyzer = responseAnalyzer;
	}
	
	public void detectErrorMessages(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response) {
		final String body = response.getBodyAsString();
		if(body == null || body.isEmpty()) {
			return;
		}
		for(String errorString: ERROR_STRINGS.keySet()) {
			if(body.contains(errorString)) {
				processDetectedErrorMessage(ctx, request, response, errorString, ERROR_STRINGS.get(errorString));
			}
		}
	}
	
	private void processDetectedErrorMessage(IInjectionModuleContext ctx, HttpUriRequest request, IHttpResponse response, String errorString, databaseErrorTypes errorType) {
		ctx.addStringHighlight(errorString);
		responseAnalyzer.alert(ctx, DATABASE_ALERTS.get(errorType), "SQL Error Message Detected", request, response, null);
	}
}
