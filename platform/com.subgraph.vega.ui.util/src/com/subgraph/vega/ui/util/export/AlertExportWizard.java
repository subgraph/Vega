package com.subgraph.vega.ui.util.export;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.subgraph.vega.api.model.alerts.IScanAlert;
import com.subgraph.vega.api.model.alerts.IScanInstance;
import com.subgraph.vega.export.AlertExporter;

public class AlertExportWizard extends Wizard {

	protected ExportWizardPageOne one;
	protected ExportWizardPageTwo two;
	protected ExportWizardPageThree three;
	private IScanInstance scanInstance = null;
	
	public AlertExportWizard(IScanInstance s) {
		super();
		scanInstance = s;
	}
	
	public AlertExportWizard() {
		super();
	}
	
	@Override
	public void addPages() {
		one = new ExportWizardPageOne();
		two = new ExportWizardPageTwo(scanInstance);
		three = new ExportWizardPageThree();
		addPage(one);
		addPage(two);
		addPage(three);
		
	}
	
	@Override
	public boolean performFinish() {
				
		List<IScanAlert> alerts;
		alerts = two.allAlertsFromTree();
		
		AlertExporter exporter = new AlertExporter();
		exporter.exportAlertsbyList(alerts);
		
		return true;
	}

}
