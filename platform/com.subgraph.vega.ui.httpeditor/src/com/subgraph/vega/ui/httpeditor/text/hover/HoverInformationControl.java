package com.subgraph.vega.ui.httpeditor.text.hover;

import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.subgraph.vega.ui.hexeditor.HexEditControl;

public class HoverInformationControl extends AbstractInformationControl implements IInformationControlExtension2 {

	private Composite rootComposite;
	private Composite parent;
	private boolean haveInput;
	
	public HoverInformationControl(Shell parentShell,
			boolean isResizable) {
		super(parentShell, isResizable);
		create();
	}

	@Override
	protected void createContent(Composite parent) {
		this.parent = parent;
	}

	@Override
	public void setInput(Object input) {
		if(rootComposite != null)
			rootComposite.dispose();
		
		rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(new GridLayout());
		
		haveInput = false;
		if(input instanceof IHttpImage) {
			createImageControl((IHttpImage) input);
			
		} else if(input instanceof IBinaryEncodedData) {
			createBinaryDataControl((IBinaryEncodedData) input);
		}
		parent.layout();
	}

	private void createImageControl(IHttpImage image) {
		final Label label = new Label(rootComposite, SWT.NONE);
		label.setImage(image.getImageInstance());
		haveInput = true;
	}
	
	private void createBinaryDataControl(IBinaryEncodedData data) {
		Label label = new Label(rootComposite, SWT.NONE);
		label.setText(data.getDescription());
		HexEditControl hec = new HexEditControl(rootComposite);
		hec.setInput(data.getDecodedBytes());
		haveInput= true;		
	}

	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new HoverInformationControl(parent, true);
			}
		};
	}
	
	@Override
	public boolean hasContents() {
		return haveInput;
	}
}