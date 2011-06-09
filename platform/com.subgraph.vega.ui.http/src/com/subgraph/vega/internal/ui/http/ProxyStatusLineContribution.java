package com.subgraph.vega.internal.ui.http;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.subgraph.vega.ui.http.Activator;
import com.subgraph.vega.ui.http.intercept.InterceptView;
import com.subgraph.vega.ui.http.intercept.queue.InterceptQueueView;

public class ProxyStatusLineContribution extends ContributionItem {
	private final static int BLINK_INTERVAL = 500;
	private final Logger logger = Logger.getLogger("proxy");
	private CLabel label;
	private Image proxyAlert = Activator.getImageDescriptor("icons/proxy_alert.png").createImage();
	private Image proxyRunning = Activator.getImageDescriptor("icons/proxy_running.png").createImage();
	private Image proxyStopped = Activator.getImageDescriptor("icons/proxy_stopped.png").createImage();
	private final Timer alertBlinkTimer = new Timer();
	private TimerTask alertBlinkTask;
	private boolean alertEnabled;
	private String runningMessage;
	private int viewCnt = 0; /** Count of currently visible views that display interceptor information */
	private int queueCnt = 0;
	private Date lastViewChange = new Date();

	public ProxyStatusLineContribution() {
		super();
		createListeners();
	}

	@Override
	public void fill(Composite parent) {		
		new Label(parent, SWT.SEPARATOR);
		label = new CLabel(parent, SWT.SHADOW_NONE);
		setProxyStopped();
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 220;
		label.setLayoutData(layoutData);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				synchronized(alertBlinkTimer) {
					if(queueCnt != 0) {
						handleAlertClick();
					}
				}
			}
		});
	}

	private void createListeners() {
		IWindowListener listener = new IWindowListener() {
			@Override
			public void windowActivated(IWorkbenchWindow window) {
			}

			@Override
			public void windowDeactivated(IWorkbenchWindow window) {
			}

			@Override
			public void windowClosed(IWorkbenchWindow window) {
			}

			@Override
			public void windowOpened(IWorkbenchWindow window) {
				createPageListener(window);
			}
		};
		IWorkbench wb = PlatformUI.getWorkbench();
		wb.addWindowListener(listener);
	}

	private void createPageListener(IWorkbenchWindow window) {
		IPageListener listener = new IPageListener() {
			@Override
			public void pageActivated(IWorkbenchPage page) {
			}

			@Override
			public void pageClosed(IWorkbenchPage page) {
			}

			@Override
			public void pageOpened(IWorkbenchPage page) {
				createPartListener(page);
			}
		};
		window.addPageListener(listener);		
		IWorkbenchPage workbenchPages[] = window.getPages();
		for (int idx = 0; idx < workbenchPages.length; idx++) {
			createPartListener(workbenchPages[idx]);
			viewCnt += countPageViewsVisible(workbenchPages[idx]);
		}
	}
	
	private int countPageViewsVisible(IWorkbenchPage page) {
		int cnt = 0;
		IViewReference references[] = page.getViewReferences();
		for (int idx = 0; idx < references.length; idx++) {
			IViewPart part = references[idx].getView(false);
			if (part instanceof InterceptView || part instanceof InterceptQueueView) {
				if (page.isPartVisible(part) == true) {
					cnt++;
				}
			}
		}
		return cnt;
	}
	
	private void createPartListener(IWorkbenchPage page) {
		IPartListener2 listener = new IPartListener2() {
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}

			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				final IWorkbenchPart part = partRef.getPart(false);
				if (part instanceof InterceptView || part instanceof InterceptQueueView) {
					synchronized (alertBlinkTimer) {
						lastViewChange = new Date();
						viewCnt--;
						if (queueCnt != 0 && viewCnt == 0 && !alertEnabled) {
							startAlert();
						}
					}
				}
			}

			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				final IWorkbenchPart part = partRef.getPart(false);
				if (part instanceof InterceptView || part instanceof InterceptQueueView) {
					synchronized (alertBlinkTimer) {
						lastViewChange = new Date();
						viewCnt++;
						// we don't cancel the alert here in case there are other callbacks still to be invoked
					}
				}
			}

			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
			}
		};		
		page.addPartListener(listener);
	}

	public void setProxyRunning(int port) {
		runningMessage = "Proxy running on port " + port;
		setLabelImageAndText(proxyRunning, runningMessage);
	}

	public void setProxyPending(int size) {
		setInterceptionQueueSize(size);
	}
	
	public void setProxyStopped() {
		stopAlert();
		setLabelImageAndText(proxyStopped, "Proxy is not running");
	}
	
	private void setInterceptionQueueSize(int size) {
		synchronized(alertBlinkTimer) {
			queueCnt = size;
			if (size > 0) {
				if (!alertEnabled && viewCnt == 0) {
					startAlert();
				}
				final String msg = "("+ size + ") messages intercepted";
				setLabelImageAndText(proxyAlert, msg);
			} else {
				stopAlert();
				setLabelImageAndText(proxyRunning, runningMessage);
			}
		}
	}

	/** Must be invoked in a synchronized block */
	private void startAlert() {
		if(alertBlinkTask == null) {
			alertBlinkTask = createAlertTask();
			alertBlinkTimer.scheduleAtFixedRate(alertBlinkTask, BLINK_INTERVAL, BLINK_INTERVAL);
			alertEnabled = true;
		}
	}

	/** Must be invoked in a synchronized block */
	private void stopAlert() {
		alertEnabled = false;
		if(alertBlinkTask != null) {
			alertBlinkTask.cancel();
			alertBlinkTask = null;
		}
	}
	
	private TimerTask createAlertTask() {
		return new TimerTask() {
			private boolean state = true;

			@Override
			public void run() {
				synchronized(alertBlinkTimer) {
					if (viewCnt != 0) {
						Date currentTime = new Date();
						if (currentTime.getTime() - lastViewChange.getTime() >= BLINK_INTERVAL) {
							stopAlert();
							if (!state) {
								setLabelImage(proxyAlert);
							}
							return;
						}
					}
				}
				state = !state;
				if(state) {
					setLabelImage(proxyAlert);
				} else {
					setLabelImage(proxyStopped);
				}
			}
		};
	}

	private void setLabelImage(Image image) {
		setLabelImageAndText(image, null);
	}
	
//	private void setLabelText(String text) {
//		setLabelImageAndText(null, text);
//	}

	private void setLabelImageAndText(final Image image, final String text) {
		if(label == null || label.isDisposed()) {
			return;
		}

		label.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if(image != null) {
					label.setImage(image);
				}
				if(text != null) {
					label.setText(text);
				}
				label.pack(true);
				label.getParent().layout();
			}
		});
	}

	private void handleAlertClick() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			window.getActivePage().showView(InterceptView.VIEW_ID);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to open interception view", e);
		}		
	}
}
