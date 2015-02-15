# Nonogram
======
Choco models to solve nonogram problem

###Requirements:
```
maven 3.x 
```
```
jdk 8.x 
```
```
python 2.7 To launch tests 
```
###Compile project

open terminal at the root of project and launch <br>
```
mvn clean install
```
###Execute project

open terminal in \<projectBase>/target and launch 
```
java -jar <Model to launch>.jar <path to nonogram instance> <path to outputFile>
```
###Scripts performances analysis:

The analysis part is located in Nonogram-DataSet folder.
In this folder, you can find a python script which launch performances test with differents nonogram instances with various size and difficulty.
You can launch these tests with the following command:
```
python test.py
```
