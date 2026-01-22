#!/bin/bash

# Change directory to the script's folder
cd "$(dirname "$0")"

# Run the JAR
java -jar Purple.jar

# Keep the terminal open if run from a GUI
read -p "Press Enter to exit..."
