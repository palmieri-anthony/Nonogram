#!/usr/bin/python

import sys
import os
import subprocess
import glob
rootdir = os.path.dirname(os.path.realpath(__file__))

jardir = os.path.dirname(os.path.realpath(__file__)+"/jars")
listJar = [ f for f in os.listdir("./jars")  ]
print listJar
for subdir,dirs,files in os.walk(rootdir):
    for jar in listJar:
		print jar
		for file in glob.glob("*.txt"):
			for i in range(0,10):
				print file
				excepted = jar+file+".csv"
				print excepted
				subprocess.call(['java', '-Xmx2024M','-jar', "jars/"+jar, '-p', file, '-s', excepted])
