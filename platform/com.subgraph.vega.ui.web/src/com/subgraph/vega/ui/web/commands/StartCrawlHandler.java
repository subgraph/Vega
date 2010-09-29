package com.subgraph.vega.ui.web.commands;

import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.subgraph.vega.api.crawler.ICrawlerConfig;
import com.subgraph.vega.api.crawler.IWebCrawler;
import com.subgraph.vega.api.crawler.IWebCrawlerFactory;
import com.subgraph.vega.api.model.web.IWebEntity;
import com.subgraph.vega.api.model.web.IWebHost;
import com.subgraph.vega.api.model.web.IWebModel;
import com.subgraph.vega.api.model.web.IWebPath;
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
		final ICrawlerConfig crawlerConfig = factory.createBasicConfig(uri);
		configureCrawler(crawlerConfig);
		final IWebCrawler crawler = factory.create(crawlerConfig);
		crawler.start();
	}
	
	private void configureCrawler(ICrawlerConfig config) {
		final URI base = config.getBaseURI();
		final IWebModel model = Activator.getDefault().getModel();
		boolean secure = ("https".equalsIgnoreCase(base.getScheme()));
		final IWebHost host = model.addWebHost(base.getHost(), base.getPort(), secure);
		final IWebPath path = host.addPath(base.getPath());
		for(IWebPath wp: path.getUnvisitedPaths()) 
			config.addInitialURI(wp.toURI());	
	}
}
