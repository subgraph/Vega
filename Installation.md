# Installation

This installation guide has been tested on Ubuntu 16.04.6 LTS.

## Installing Requirements

It is necessary to use Python 3. This installation guide has been tested with Python 3.5.2 and pip 8.1.1.

* Install libwebkit ([necessary for Vega](https://github.com/subgraph/Vega/wiki/Troubleshooting))
  ```
  sudo apt install libwebkitgtk-1.0
  ```
* Install Java 8
  ```
  sudo apt install openjdk-8-jre-headless
  ```
* Choose the right java version (Java 8)
  ```
  sudo update-alternatives --config java
  ```
* Install ant
  ```
  sudo apt install ant
  ```
* Install xvfb (necessary to run Vega without GUI)
  ```
  sudo apt install xvfb
  ```
* Install py4j
  ```  
  pip3 install py4j
  ```

## Building Vega

* Clone this repository
  ```  
  git clone https://github.com/anneborcherding/Vega.git
  ```
  ```
  cd Vega
  ```
* Build Vega using ant
  ```
  ant
  ```
* The resulting zip-file can be found in `build/stage/I.VegaBuild/`
* Copy the zip-file to a location of your choice und unzip it. We will call this folder `path/to/vega`.

## Running Vega

* If you wish to run Vega using the GUI, run `./Vega` in `path/to/vega`.
* If you wish to run Vega using the Python API, adapt `example.py` to your needs, copy it to `path/to/vega` and run it
  ```
  python3 example.py
  ```

# Trouble Shooting

* Make sure that you have installed all the requirements
* Permission denied exception by xvfb-run: Make sure to use the right path for Vega. It needs to point to the executable.