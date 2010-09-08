package com.subgraph.vega.ui.tree.web;

import java.util.ArrayList;
import java.util.List;

import com.subgraph.vega.api.model.web.IWebGetTarget;
import com.subgraph.vega.api.model.web.IWebPath;
import com.subgraph.vega.ui.tree.ITreeAdapter;

public class WebPathTreeStrategy implements ITreeAdapter<IWebPath> {

	@Override
	public Object[] getChildren(IWebPath item) {
		List<Object> children = new ArrayList<Object>();
		for(IWebPath cp : item.getChildPaths()) {
			Object node = collapsedNode(item, cp);
			if(node != null)
				children.add(node);
			for(IWebGetTarget t: cp.getTargets())
				children.add(t);
		}
		
		return children.toArray();
	}
	
	private Object collapsedNode(IWebPath root, IWebPath p) {
		if(p.getChildPaths().size() == 1 && p.getTargets().isEmpty())
			return collapsedNode(root, p.getChildPaths().iterator().next());
		else if(p.getChildPaths().isEmpty() && !p.getTargets().isEmpty()) {
			IWebPath pp = p.getParentPath();
			if(pp == null || pp == root)
				return null;
			else
				return pp;
		} else {
			return p;
		}
	}
	
	@Override
	public int getChildrenCount(IWebPath item) {
		return getChildren(item).length;
	}

	@Override
	public Object getParent(IWebPath item) {
		if(item.getParentPath() == null)
			return item.getHost();
		else if(item.getParentPath().getChildPaths().size() == 1)
			return getParent(item.getParentPath());
		else
			return item.getParentPath();
	}

	@Override
	public String getLabel(IWebPath item) {
		if(item.getParentPath() == null)
			return "/";
		else
			return prevLabel(item) + "/" + item.getPath();
	}
	
	private String prevLabel(IWebPath item) {
		IWebPath pp = item.getParentPath();
		if(pp.getChildPaths().size() == 1 && pp.getParentPath() != null)
			return getLabel(pp);
		else
			return "";
	}

	
}
