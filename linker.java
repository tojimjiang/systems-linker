/* 
 * Operating Systems Two Pass Linker
 *
 * Description: This linker reads from standard input that complies with the specification. There are 6 errors
 * specified in the specification. The standard input is read in, and written into a file. Later, this file is used as
 * the two pass linker. The first pass read the description list, and the size of modules. The second pass builds the
 * use list, and works to resolve addresses given.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Error Types
 * (*)(1) SYM MULTI DEFINED - If a symbol is multiply defined, print an error message and use the value given in the last definition.
 * (*)(2) SYM NOT DEF - If a symbol is used but not defined, print an error message and use the value 111.
 * (*)(3) SYM NOT USE - If a symbol is defined but not used, print a warning message and continue.
 * (*)(4) ABSOLUTE EXCEEDS SIZE - If an absolute address exceeds the size of the machine, print an error message and use the largest legal value.
 * (*)(5) MULTI SYM FOR INSTRUCTION - If multiple symbols are listed as used in the same instruction, print an error message and ignore all but the last usage given.
 * (*)(6) DEF EXCEEDS MODULE - If an address appearing in a definition exceeds the size of the module, print an error message and treat the address given as the last word in the module.
 */

public class linker {

    public static void main(String[] args) throws FileNotFoundException, IOException{
        // ++++++++++++++++++++++++++++++++++++
        // ++++++++++ INPUT HANDLING ++++++++++
        // Read the input though scanner, and write to a temporary file. This does no linking. Only input formatting.


        // ----------------------------------
        // ----- Reading Standard Input -----

        // Create Scanner and Temp File and Writer
        Scanner input = new Scanner(System.in);
        File fileName = new File("temp_linker.txt");
        BufferedWriter fwriter = new BufferedWriter(new FileWriter(fileName));

        // Module Count & Process
        int fModules = input.nextInt();
        fwriter.write(String.valueOf(fModules));
        fwriter.newLine();

        // Processing
        for (int i = 0; i < fModules; i++) {


            // Def List Count & Write
            int fDefinition = input.nextInt();
            fwriter.write(String.valueOf(fDefinition));
            fwriter.write(" ");
            // Add all definition symbol/value pairs
            for (int j = 0; j < fDefinition; j++) {
                String dSymbol = input.next();
                fwriter.write(dSymbol);
                fwriter.write(" ");

                int dValue = input.nextInt();
                fwriter.write(String.valueOf(dValue));
                fwriter.write(" ");
            }
            fwriter.newLine();

            // Use List Count & Write
            int fUse = input.nextInt();
            fwriter.write(String.valueOf(fUse));
            fwriter.write(" ");
            // Add all use symbol/instruction groups
            for (int j = 0; j < fUse; j++) {
                String uSymbol = input.next();
                fwriter.write(uSymbol);
                fwriter.write(" ");

                int dIns = input.nextInt();
                while (dIns != -1) {
                    fwriter.write(String.valueOf(dIns));
                    fwriter.write(" ");
                    dIns = input.nextInt();
                }
                fwriter.write(String.valueOf(dIns));
                fwriter.write(" ");
            }
            fwriter.newLine();

            // Address Count & Write
            int fAddress = input.nextInt();
            fwriter.write(String.valueOf(fAddress));
            fwriter.write(" ");

            for (int j = 0; j < fAddress; j++) {
                int dValue = input.nextInt();
                fwriter.write(String.valueOf(dValue));
                fwriter.write(" ");
            }
            fwriter.newLine();
        }
        // Close File
        fwriter.close();

        ArrayList<String> symbols = new ArrayList<String>(); // List of symbols
        ArrayList<Integer> values = new ArrayList<Integer>(); // List of values for the symbols
        ArrayList<Boolean> errorMDL = new ArrayList<Boolean>(); // Check if symbol is multi defined <error> <M>ulti<D>efined<L>ist
        ArrayList<Boolean> errorXMSL = new ArrayList<Boolean>(); // Track if a symbol size exceeds its module size <error> e<X>ceed<M>odule<S>ize<L>ist
        ArrayList<Boolean> notUsedSym = new ArrayList<Boolean>(); // Track if symbol was NOT used at all in this linker TRUE means NOT used; FALSE means used.
        ArrayList<Integer> moduleDef = new ArrayList<Integer>(); // Track module that a symbol was defined in for warning message

        // Read the "clean" file that we generated.
        Scanner reader = new Scanner(fileName);

        // +++++++++++++++++++++++++++++++++++++
        // ++++++++++ TWO PASS LINKER ++++++++++

        int machineSize = 300;

        // --------------------------------
        // ----- First Pass of Linker -----

        int nModules = Integer.parseInt(reader.next());
        int increment = 0;

        // Go though Modules
        for (int i = 0; i < nModules; i++){

            // Definition List
            int nDefinition = Integer.parseInt(reader.next());
            for (int j = 0; j < nDefinition; j++){
                String symbol = reader.next();
                int value = reader.nextInt() + increment;
                // Add the symbol to the lists
                if (symbols.contains(symbol)){
                    // Multi-defined Case
                    errorMDL.set(j,true); // Note it in errors
                    values.set(j,value); // Update value of symbol to most current one (thus will be last one)
                }
                //  When it is NOT multi-defined
                else{
                    symbols.add(symbol);
                    values.add(value);
                    errorMDL.add(false);
                    errorXMSL.add(false);
                    notUsedSym.add(true);
                    moduleDef.add(i);
                }
            }

            // Use List -- Not Used in Pass 1
            int nUse = Integer.parseInt(reader.next());
            String dummy = "";
            for (int j = 0; j < nUse; j++){
                dummy = "";
                while(!dummy.equals("-1")) {
                    dummy = reader.next();
                }
            }

            // Number of Addresses
            int nAddresses = Integer.parseInt(reader.next());

            // Check if any symbols would exceed module size
            for (int j = 0; j < nDefinition; j++){
                // Starting Index is last element of arraylist, then work backwards
                int startIndex = values.size() - 1;
                // The last address of the module will be  at increment + addresses - 1
                if (values.get(startIndex - j) > increment + nAddresses - 1){
                    values.set(startIndex - j, increment + nAddresses - 1);
                    errorXMSL.set(startIndex - j, true);
                }
            }
            // Go though the addresses
            for (int j = 0; j < nAddresses; j++){
                dummy = reader.next(); // We don't interpret in first pass
            }

            // Increase increment after each set of addresses
            increment = nAddresses + increment;
        }

        // ------------------------------------
        // ----- Interlude Between Passes -----

        // Close file at end of first pass (allows for reset for second pass)
        reader.close();

        // Print Symbol Table
        System.out.println("Symbol Table");
        for (int i = 0; i < symbols.size(); i++){
            // Print Symbol with Value
            System.out.printf("%s = %2d", symbols.get(i), values.get(i));
            // Multi Defined Error (Error 1)
            if(errorMDL.get(i)){
                System.out.print("    Error: This symbol is multiply defined; last value used. (Error 1)");
            }

            // Definition Exceeds Module Size (Error 6)
            if(errorXMSL.get(i)){
                System.out.print("    Error: This symbol exceeds module size. Last word of module was used. (Error 6)");
            }
            // Line break after completed print and errors.
            System.out.print("\n");
        }

        // Print Other Messages
        System.out.println("");
        System.out.println("Memory Map");

        // Reopen file
        reader = new Scanner(fileName);


        // ---------------------------------
        // ----- Second Pass of Linker -----

        nModules = Integer.parseInt(reader.next());
        increment = 0;
        int pos = 0; // position to appear in the memory map

        // Go though Modules
        for (int i = 0; i < nModules; i++){
            String dummy = "";

            // Definition List -- Not Used in Pass 2
            int nDefinition = Integer.parseInt(reader.next());
            for (int j = 0; j < nDefinition; j++){
                dummy = reader.next();
                dummy = reader.next();
            }

            // Use List
            int nUse = Integer.parseInt(reader.next());
            // Store which instructions use which symbols (array size of 300 to allow for max size of system) and any missing symbols
            String[] symList = new String[machineSize];
            boolean[] multiSym = new boolean[machineSize];
            ArrayList<String> missingSym = new ArrayList<String>();
            // Init boolean array to "FALSE" and symbol to " ".
            for (int j = 0; j < machineSize; j++){
                symList[j] = " ";
                multiSym[j] = false;
            }

            // Go though use list
            for (int j = 0; j < nUse; j++){
                // Get a symbol then get the list after it.
                String currSym = reader.next();
                int currNum = reader.nextInt();
                if (symbols.contains(currSym) == false) {
                    // Error (Missing Symbol)
                    // Create the symbol with value of 111.
                    missingSym.add(currSym);
                    symbols.add(currSym);
                    values.add(111);
                    errorMDL.add(false);
                    errorXMSL.add(false);
                    notUsedSym.add(true);
                    moduleDef.add(i); // Not truly defined, but used to maintain indexing parity with other lists.
                }

                // After first case, keep grabbing the used indexes from reader, until we get -1, indicating the termination of a list.
                // Once then, we can move on in for loop (which tracks number of symbols), and grab the next symbol.
                // Grab first (in case it is a symbol followed by -1, or no "real" use list case)
                while (currNum != -1) {
                    // Check if instruction has been saved in another symbol, if so make a note of it.
                    if (!symList[currNum].equals(" ")) {
                        multiSym[currNum] = true;
                    }
                    // Update instruction to use symbol.
                    symList[currNum] = currSym;
                    currNum = reader.nextInt();
                }
                // Only go to next for loop execution once we reach a -1.
            }

            // Addresses and Module
            int nAddress = Integer.parseInt(reader.next());
            // Temporary Elements
            int readAddress = 0;
            int trueAddress = 0;
            int firstDigit = 0;
            int middle3 = 0;
            int opDigit = 0;
            int fixedAddress = 0;
            boolean exceedMax = false;

            for (int j = 0; j < nAddress; j++){
                int currentOffset = increment + j;
                readAddress = reader.nextInt();
                trueAddress = readAddress / 10; // Use integer division to get first 4 digits
                firstDigit = readAddress / 10000; // Use integer division to get first digit
                opDigit = readAddress % 10; // Use modulo to get last digit
                exceedMax = false;
                switch (opDigit) {
                    case 1: // Immediate (do nothing)
                        fixedAddress = trueAddress;
                        System.out.printf("%2d:  %4d", currentOffset, fixedAddress);
                        break;
                    case 2: // Absolute (make sure size fits)
                        fixedAddress = trueAddress;
                        middle3 = trueAddress % 1000; // Modulo get the last 3 from trueAddress
                        if (middle3 >= machineSize) {
                            // Too Big (Exceed Machine Max)
                            exceedMax = true;
                            fixedAddress = firstDigit * 1000 + machineSize - 1; // Machine Size is 300 (counting from 1), (Address start at 0 so -1)
                        }
                        System.out.printf("%2d:  %4d", currentOffset, fixedAddress);
                        // Absolute Exceeds Size Error Message
                        if (exceedMax) {
                            System.out.print("    Error: This address exceeds machine size. Largest legal value was used. (Error 4)");
                        }
                        break;
                    case 3: // Relative (add the increment)
                        middle3 = trueAddress % 1000; // Modulo get the last 3 from trueAddress
                        fixedAddress = firstDigit * 1000 + middle3 + increment;
                        System.out.printf("%2d:  %4d", currentOffset, fixedAddress);
                        break;
                    case 4: //External (use symbol)
                        // Find which symbol to use
                        String symUse = symList[j];
                        if (symUse != null) {
                            int symIndex = symbols.indexOf(symUse);
                            int symValue = values.get(symIndex);
                            fixedAddress = firstDigit * 1000 + symValue;
                            notUsedSym.set(symIndex, false);
                        }
                        System.out.printf("%2d:  %4d", currentOffset, fixedAddress);
                        // Not Declared Symbol Error Message
                        if (missingSym.contains(symUse)) {
                            System.out.printf("    Error: The symbol \"%s\" was not defined. Value of 111 used. (Error 2)", symUse);
                        }
                        // Multi Sym Error Message
                        if (multiSym[j]) {
                            System.out.print("    Error: Multiple symbols were used for this instruction. Last use case applied. (Error 5)");
                        }
                        break;
                    }
                    System.out.print("\n");
                }
                increment = increment + nAddress;

            }
            // House Keeping - Close, Warnings and Delete File
            System.out.print("\n");
            // Close file reader, save resources
            reader.close();
            // Find unused symbols.
            int nSymbols = symbols.size();
            for(int i = 0; i < nSymbols; i++){
                // All cases of TRUE for the ArrayList are ones that were NOT used. The FALSE cases were used.
                if(notUsedSym.get(i)) {
                    System.out.printf("Warning: The symbol \"%s\" was defined in module %d but was never used (Warning 3). \n", symbols.get(i), moduleDef.get(i));
                }
            }
            fileName.deleteOnExit();
        }

    }