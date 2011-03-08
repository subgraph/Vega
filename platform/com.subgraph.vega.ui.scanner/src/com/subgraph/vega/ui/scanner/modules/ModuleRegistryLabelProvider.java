package com.subgraph.vega.ui.scanner.modules;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.subgraph.vega.api.scanner.modules.IScannerModule;
import com.subgraph.vega.api.scanner.modules.ModuleScriptType;
import com.subgraph.vega.ui.scanner.Activator;
import com.subgraph.vega.ui.util.ImageCache;

public class ModuleRegistryLabelProvider extends LabelProvider {

	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	public String getText(Object element) {
		if (element instanceof IScannerModule) {
			return ((IScannerModule) element).getModuleName();
		} else if (element instanceof ModuleScriptType) {
			switch((ModuleScriptType)element) {
				case RESPONSE_PROCESSOR:
					return "Respone Processing Modules";
				case PER_SERVER:
					return "Host Modules";
				case PER_DIRECTORY:
					return "Directory Modules";
				case PER_MOUNTPOINT:
					return "Mointpoint Modules";
				case PER_RESOURCE:
					return "Resource Modules";
			}
		}
		return element.toString();
	}
	/*
	public Image getImage(Object element) {
		return imageCache.get("icons/alert_high.png");
	}*/
}
