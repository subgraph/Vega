package com.subgraph.vega.api.model.requests;

import java.util.List;

//import org.apache.http.HttpHost;
//import org.apache.http.HttpRequest;
//import org.apache.http.HttpResponse;

import com.subgraph.vega.api.http.requests.IHttpResponse;
import com.subgraph.vega.api.model.conditions.IHttpConditionSet;

public interface IRequestLog {
	long allocateRequestId();
	long addRequestResponse(IHttpResponse response);
	IRequestLogRecord lookupRecord(long requestId);
	List<IRequestLogRecord> getAllRecords();
	List<IRequestLogRecord> getRecordsByConditionSet(IHttpConditionSet filterCondition);

	void addUpdateListener(IRequestLogUpdateListener callback);
	void addUpdateListener(IRequestLogUpdateListener callback, IHttpConditionSet filterCondition);
	
	void removeUpdateListener(IRequestLogUpdateListener callback);

}
