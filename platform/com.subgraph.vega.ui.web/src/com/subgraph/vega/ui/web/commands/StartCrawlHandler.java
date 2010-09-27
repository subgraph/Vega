package com.subgraph.vega.ui.web.commands;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.ui.web.Activator;

public class StartCrawlHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if(ss.size() == 1 && (ss.getFirstElement() instanceof IWebEntity))
				crawlFrom((IWebEntity) ss.getFirstElement());
		}
		return null;
	}
	
	
	private void crawlFrom(IWebEntity item) {
		final IWebCrawlerFactory factory = Activator.getDefault().getWebCrawlerFactory();
		final URI uri = item.toURI();
		final IWebCrawler crawler = factory.create(uri);
		crawler.start();
	}
}
