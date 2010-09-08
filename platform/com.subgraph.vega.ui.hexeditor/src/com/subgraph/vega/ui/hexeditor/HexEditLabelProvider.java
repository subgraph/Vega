package com.subgraph.vega.ui.hexeditor;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;

public class HexEditLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider  {

	private final FontRegistry fonts = new FontRegistry();
	
	HexEditLabelProvider() {
		addFont("hex-element", "Courier", 12, SWT.NORMAL);
		addFont("hex-ascii", "Courier", 12, SWT.NORMAL);
	}
	
	private void addFont(String tag, String name, int size, int flags) {
		fonts.put(tag, new FontData[] {
			new FontData(name, size, flags)	
		});
	}
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(!(element instanceof HexEditModelItem))
			return null;
		final HexEditModelItem item = (HexEditModelItem) element;
		if(columnIndex == 0)
			return String.format("%04X", item.getOffset());
		else if(columnIndex > 0 && columnIndex <= 16) {
			int value = item.getByteAt(columnIndex - 1);
			if(value == -1)
				return "";
			else
				return String.format("%02X", value);
		}
		else if(columnIndex == 17)
			return renderAscii(item.getData());
		else
			return "???";
	}
	
	private String renderAscii(byte[] rowData) {
		final StringBuilder buffer = new StringBuilder();
		for(int i = 0; i < HexEditModel.ROW_LENGTH; i++) {
			if(i >= rowData.length)
				buffer.append(" ");
			else
				buffer.append(renderByte(rowData[i]));
		}
		return buffer.toString();
	}
	
	private String renderByte(byte b) {
		if(isPrintable(b))
			return Character.toString((char)b);
		else 
			return ".";
	}
	
	private boolean isPrintable(byte b) {
		return (b >= 32 && b <= 126);
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		if(columnIndex == 17)
			return fonts.get("hex-ascii");
		else
			return fonts.get("hex-element");
	}

	
}