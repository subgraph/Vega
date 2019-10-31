package com.subgraph.vega.application;

import com.subgraph.vega.ui.scanner.*;
import com.subgraph.vega.export.*;

public class EntryPoint {

	private MyScanExecutor myScanEx;
	private Application myApplication;
	private AlertExporter myAlertExporter;

	public EntryPoint() {
		myScanEx = new MyScanExecutor();
		myApplication = null;
		myAlertExporter = new AlertExporter();
	}
	
	public EntryPoint(Application application) {
		myApplication = application;
		myScanEx = new MyScanExecutor();
		myAlertExporter = new AlertExporter();
	}

	public MyScanExecutor getMyScanExecutor(){
		return myScanEx;
	}
	
	public Application getApplication() {
		return myApplication;
	}
	
	public AlertExporter getAlertExporter() {
		return myAlertExporter;
	}
	

}


