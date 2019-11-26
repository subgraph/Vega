# Python Interface for Vega

This code adds a rudimentary Python interface to Vega using [py4j](https://github.com/bartdag/py4j). With this, it is possible to automatically configure and run scans as well as getting the results of Vega.
An installation guidline for Ubuntu 16.04 is given [here](installation.md). You may also have a look at the installation instructions for Vega given [here](https://github.com/subgraph/Vega/wiki/Building-Vega).

An example of how to use the Python interface is presented in the following (and can also be found in [example.py](example.py)).
Values that can be set for Vega can be found in [MyScanExecutor](platform/com.subgraph.vega.ui.scanner/src/com/subgraph/vega/ui/scanner/MyScanExecutor.java) and [MyAlertExporter](platform/com.subgraph.vega.export/src/com/subgraph/vega/export/AlertExporter.java).


```
import subprocess as sp
import time
import shlex
import shutil
import os

from py4j.java_gateway import JavaGateway
from py4j.java_collections import ListConverter


target = "192.168.0.2"
result_path = "./results"
authentication = "admin:admin"
vega_path = "/path/to/vega/Vega"
#seconds to wait for Vega to start
wait_time = 10


# checking if xvfb exists
if(shutil.which("xvfb-run") == None):
	raise MissingDependency('xvfb cannot be found. Please install xvfb to use the python interface of Vega.')

# run vega
cmd = "xvfb-run -a " + vega_path
#process gets a new group ID so it can be stopped (including all additional created processes) later
vega_process = sp.Popen(shlex.split(cmd), preexec_fn=os.setsid)
print("Started Vega from %s" % str(vega_path))
time.sleep(wait_time)

# init vega classes
gateway = JavaGateway()
scanex = gateway.entry_point.getMyScanExecutor()
alertExporter = gateway.entry_point.getAlertExporter()

# set values
scanex.setTarget(target)
alertExporter.setPath(result_path)

scanex.runScan()

alertExporter.exportAlertsOfLastScan()

try:
	os.killpg(os.getpgif(process.pid), signatl.SIGTERM)
	process.wait()
except Exception:
	pass
```
