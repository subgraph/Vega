package com.subgraph.vega.ui.scanner.alerts;

import java.util.Date;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.subgraph.vega.api.model.alerts.IScanAlertRepository;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertScanNode;
import com.subgraph.vega.ui.scanner.alerts.tree.AlertSeverityNode;

public class ScanAlertSorter extends ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		final int cat1 = category(e1);
		final int cat2 = category(e2);
		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		if ((e1 instanceof AlertScanNode) && (e2 instanceof AlertScanNode)) {
			return compareAlertNodes((AlertScanNode) e1, (AlertScanNode) e2);
		} else if ((e1 instanceof AlertSeverityNode)
				&& (e2 instanceof AlertSeverityNode)) {
			final AlertSeverityNode asn1 = (AlertSeverityNode) e1;
			final AlertSeverityNode asn2 = (AlertSeverityNode) e2;
			return asn2.getSeverityIndex() - asn1.getSeverityIndex();

		} else {
			return super.compare(viewer, e1, e2);
		}
	}

	@Override
	public int category(Object element) {
		if (element instanceof AlertScanNode) {
			return (((AlertScanNode) element).getScanId() == IScanAlertRepository.PROXY_ALERT_ORIGIN_SCAN_ID) ? (0)
					: (1);
		}
		return 0;
	}

	private int compareAlertNodes(AlertScanNode n1, AlertScanNode n2) {
		if ((n1.getScanInstance() == null) || (n2.getScanInstance() == null)) {
			return (int) (n1.getScanId() - n2.getScanId());
		} else {
			final Date d1 = n1.getScanInstance().getStartTime();
			final Date d2 = n2.getScanInstance().getStartTime();
			if (d1 == null || d2 == null) {
				return 0;
			}
			return (d1.getTime() < d2.getTime()) ? (1) : (-1);
		}
	}

}
