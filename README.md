# Two Pass Linker
A README File  

## What is this?
This is a program that links or resolves address and references.  
This is based on the lab specification for the Linker Lab for Operating Systems.

## How to Compile

### On Windows and NYU Compute Servers
To compile and run the program, load the file linker.java to your working directory. 
Next, using a terminal window with the javac and java commands, compile the java program by using the command "javac linker.java"
a) To run the program after compilation (using keyboard as standard input) use the command "java linker". 
b) To run the program after compilation (using a file as standard input) use the command "java jlinker < 'file' ".


### FreeStyle Compiling
Open the jj1922.java file and copy and paste the source code into wherever you want to compile. 
The class declaration (line 31) may need modifications for freestyle compiling. 
The program must be compiled into a java unit using the Java compiler using at a minimum Java 7.
a) To run the program (using keyboard as standard input), use the java command and call the class declartion.
b) To run the program (using a file as standard input) use the java command, call the class declartion, add the < operator, and the file.

### Notes about Compiling
Quotation marks (and appostrophes) above are for differantion purposes, DO NOT use them when running commands in the terminal.
All input must still follow the specifications indicated. 
It is recommended for the (input) file to be in the same directory as the java file.
If using an (input) file, that is NOT in the same directory as the java file, you must (at a minimum) pass the relative path to the file.

## Program Description
The program first reads all the input to a clean temporary file (with file name of temp_linker.txt). 
First Pass, loads the definitions, checks their validity, and the size of the entire operation.
Second Pass, loads the use list, and works to resolve the symbols to addresses, and solve addresses as needed.
Error messages are printed between passes, during the second pass, or at the end. 
The Symbol Table is printed between passes. The memory map is printed during the second pass.
The the end, the program closes and deletes the temporary file. 


## Important Considerations

### Input Requirements
In addition to the specifications listed, symbols may not have any whitespace characters. 
Addresses and instructions may not have any whitecharacters.

### Minimum System Requirements
This program REQUIRES read, write, and deletion access. (For the temporary file) Please verify that your system
allows for these permissions. (Crackle1 allows these permissions as tested)
System must have sufficent disk space to write approximately 1MB at most. (Will vary with input size and length)

## Other Related Repositories:
* [Scheduler](https://github.com/tojimjiang/systems-scheduler)  
* [Banker](https://github.com/tojimjiang/systems-banker)  
* [Demand Pager](https://github.com/tojimjiang/systems-pager)  