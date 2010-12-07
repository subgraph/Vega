
function print(str) {
	if(workspace != null) {
		workspace.consoleWrite(str);
	} else {
		java.lang.System.out.println(str);
	}
};
