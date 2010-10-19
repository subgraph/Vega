
function print(str) {
	if(scanModel != null) {
		scanModel.consoleWrite(str);
	} else {
		java.lang.System.out.println(str);
	}
};
