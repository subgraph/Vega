package com.subgraph.vega.ui.console;

import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;

import com.subgraph.vega.api.console.IConsoleDisplay;
import com.subgraph.vega.ui.util.images.ImageCache;

public class ConsoleView extends ViewPart implements IConsoleDisplay {

	final private static String CONSOLE_ICON = "icons/console.png";
	final private static String CONSOLE_OUTPUT_ICON = "icons/console_output.png";
	final private static String CONSOLE_ERROR_ICON = "icons/console_error.png";
	
	final private static int ALERT_TIME = 4000;
	private final ImageCache imageCache = new ImageCache(Activator.PLUGIN_ID);

	private StyledText output;
	private MenuManager contextMenu;
	
	private long lastOutputTime = System.currentTimeMillis();
	private long lastErrorTime = System.currentTimeMillis();
	private boolean showingOutputIcon = false;
	private boolean showingErrorIcon = false;
	
	@Override
	public void createPartControl(Composite parent) {
		output = new StyledText(parent, SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);

		output.setFont(JFaceResources.getTextFont());
		
		/* create and set context menu */
		contextMenu = new MenuManager("#PopupMenu");
		fillContextMenu(contextMenu);
		output.setMenu(contextMenu.createContextMenu(output));
		// createActions();
		Activator.getDefault().getConsoleService().registerDisplay(this);
	}

	@Override
	public void dispose() {
		imageCache.dispose();
		contextMenu.dispose();
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		showingErrorIcon = false;
		showingOutputIcon = false;
		setTitleImage();
		output.setFocus();
	}

	@Override
	public void printOutput(final String message) {
		Display display = Display.getDefault();
		if (display.isDisposed()) {
			System.out.print(message);
			return;
		}
		
		display.asyncExec(new Runnable() {
			public void run() {
				if(output.isDisposed()) 
					return;
				output.append(message);
				output.setCaretOffset(output.getCharCount());
				output.showSelection();
				
				showOutputIcon();
			}
		});
	}
	
	@Override
	public void printError(final String message) {
		final Display display = Display.getDefault();
		if (display.isDisposed()) {
			System.err.print(message);
			return;
		}
		
		display.asyncExec(new Runnable() {
			public void run() {
				if(output.isDisposed()) 
					return;
				output.append(message);
				output.setStyleRange(new StyleRange(output.getCharCount()-message.length(), message.length(), display.getSystemColor(SWT.COLOR_RED), null));
				output.setCaretOffset(output.getCharCount());
				output.showSelection();
				
				showErrorIcon();
			}
		});
	}

	private void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new Action("Clear"){
			public void run() {
				output.setText("");
			}
		});
		
		/* add standard separator to handle additions */
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void setTitleImage() {
		if (showingErrorIcon) {
			setTitleImage(imageCache.get(CONSOLE_ERROR_ICON));
		} else if (showingOutputIcon) {
			setTitleImage(imageCache.get(CONSOLE_OUTPUT_ICON));
		} else {
			setTitleImage(imageCache.get(CONSOLE_ICON));
		}
	}

	private void animateTitleImage() {
		setTitleImage();
		
		/*
		 * if the Console doesnt have the focus, keep the title image static to show
		 * output or errors until the user gives focus to it, otherwise schedule
		 * a reset of the icon decoration for a few seconds after the last output
		 * or error happened
		 */
		if (!output.isFocusControl())
			return;
		final Display display = Display.getDefault();
		display.timerExec(ALERT_TIME/2, new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				if (lastErrorTime + ALERT_TIME <= now)
					showingErrorIcon = false;
				if (lastOutputTime + ALERT_TIME <= now)
					showingOutputIcon = false;
				setTitleImage();
				if (showingErrorIcon || showingOutputIcon)
					display.timerExec(ALERT_TIME/2, this);
			}
		});
	}
	
	public void showErrorIcon() {
		lastErrorTime = System.currentTimeMillis();
		if (!showingErrorIcon) {
			showingErrorIcon = true;
			animateTitleImage();
		}
	}

	public void showOutputIcon() {
		lastOutputTime = System.currentTimeMillis();
		if (!showingOutputIcon) {
			showingOutputIcon = true;
			animateTitleImage();
		}
	}
	/*
    protected void createActions() {
        IViewSite viewSite = getViewSite();
        IActionBars actionBars = viewSite.getActionBars();
//		ResourceBundle bundle = ConsoleResourceBundleMessages.getBundle();
//        FindReplaceAction fraction = new FindReplaceAction(bundle, "find_replace_action_", this); //$NON-NLS-1$
 //       actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(), TestHandler);
        actionBars.updateActionBars();

    }
    */
   
   
}
