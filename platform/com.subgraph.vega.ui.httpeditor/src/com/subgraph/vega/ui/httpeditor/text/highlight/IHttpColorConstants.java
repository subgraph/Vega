package com.subgraph.vega.ui.httpeditor.text.highlight;

import org.eclipse.swt.graphics.RGB;

public interface IHttpColorConstants {
	RGB HTTP_DEFAULT = new RGB(0,0,0);
	RGB HTTP_HEADER_NAME = new RGB(128,0,0);
	RGB HTTP_HEADER_VALUE = new RGB(200,20,200);
	RGB HTTP_REQUEST_METHOD = new RGB(29,0,200);
	RGB HTTP_VERSION = new RGB(49, 200, 111);
	RGB HTTP_STRING = new RGB(0x64, 0x3C, 0xFF);
	RGB HTTP_DATE = new RGB(0xA9, 0xA1, 0x6E);
	RGB HTTP_INTEGER = new RGB(0x63, 0x91, 0x5E);
	RGB ENCODED_CHARACTER = new RGB(0xE8, 0x17, 0x74);
	RGB IMAGE_TAG = new RGB(70, 70, 70);
}