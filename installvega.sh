#!/bin/bash

defaultText="\e[39m"
blueText="\e[34m"
yellowText="\e[33m"
redText="\e[31m"
greenText="\e[32m"

sourceInput="deb http://deb.debian.org/debian oldstable main contrib non-free"
sourceCheck="http://deb.debian.org/debian"

clear
echo -e "${yellowText}Vega for Kali Linux (2020) (64-bit) Installer by Indica"

# Download Vega
echo -e "${blueText}Downloading Vega . . .${defaultText}"
curl -o ./vega.zip "https://support.subgraph.com/downloads/VegaBuild-linux.gtk.x86_64.zip"
echo -e "${greenText}Successfully downloaded vega to ./vega.zip"

# Extract Vega
echo -e "${blueText}Extracting zip . . .${defaultText}"
unzip vega.zip 
echo -e "${greenText}Successfully extracted the software"

# Switch JDK Version
echo -e "${blueText}Switching Java JDK Version . . .${defaultText}"
echo "2" | sudo update-alternatives --config java

# Update Sources 
echo -e "\n${blueText}Updating Sources . . .${defaultText}"
if sudo grep -q "${sourceCheck}" "/etc/apt/sources.list"; then
	# source already exists
	echo -e "${greenText}Source is already inserted!"
else
	# source doesnt exist
	echo -e "${yellowText}Source does not exist, adding it now . . .${defaultText}"
	echo "$sourceInput" | sudo tee -a /etc/apt/sources.list
	echo -e "${blueText}Updating apt database . . .${defaultText}"
	sudo apt-get update
fi

# Install libwebkitgtk-1.0
echo -e "${blueText}Installing libwebkitgtk-1.0 . . .${defaultText}"
sudo apt-get install libwebkitgtk-1.0

# Move Vega to Applications Directory
echo -e "${blueText}Moving Vega to Applications . . .${defaultText}"
sudo mv vega /usr/share/

# Create Accessable Launcher
echo -e "${blueText}Creating Desktop Icon . . .${defaultText}"
echo "[Desktop Entry]" | sudo tee -a /usr/share/applications/Vega.desktop
echo "Name=Vega" | sudo tee -a /usr/share/applications/Vega.desktop
echo "Exec=/usr/share/vega/Vega" | sudo tee -a /usr/share/applications/Vega.desktop
echo "Icon=/usr/share/vega/Vega.png" | sudo tee -a /usr/share/applications/Vega.desktop
echo "Terminal=false" | sudo tee -a /usr/share/applications/Vega.desktop
echo "Type=Application" | sudo tee -a /usr/share/applications/Vega.desktop
echo "Categories=03-webapp-analysis;03-05-web-vulnerability-scanners;" | sudo tee -a /usr/share/applications/Vega.desktop

# Finish
echo -e "${greenText}Vega should now be installed!\nLaunch via Applications/Web Application Analysis/Vega"












