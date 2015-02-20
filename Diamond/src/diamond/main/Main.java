/**
 * @author Rodolfo Kaplan Depena
 */
package diamond.main;

import java.io.File;

import diamond.gDebugger.DMain;
import diamond.managers.LinkedDataManager;
import diamond.managers.NativeStoreStorageManager;
import diamond.processors.FileQueryProcessor;
import diamond.processors.QueryProcessor;
import diamond.tests.SparqlTestSuite;

/**
 * The Main class is an entry point into Diamond. It's function is to accept a
 * SPARQL query (whether it be through a file or string).
 */
public class Main {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FileArg queryFileArg = new FileArg();
        FileArg dataFileArg = new FileArg();
        DebugArg debugArg = new DebugArg();
        StepsArg stepsArg = new StepsArg();
        TestSuiteArg testSuiteArg = new TestSuiteArg();
        TimerArg timerArg = new TimerArg();

        String manual = "";
        manual += "usage: Diamond.jar [parameters]\n";
        manual += "		-sparqlfile 	<file>		Execute a SPARQL query file on the web of linked data. (May accompany with -datafile)\n";
        manual += "		-debugger		    	Execute the Diamond GUI debugger. (Use this option with no other option)\n";
        manual += "		-testsuite		    	Execute the RAP test suite. (Use this option with no other option)\n";
        manual += "		-datafile 	<file>  	Input RDF Data File to execute query on. (Must use with -sparqlfile option)\n";
        manual += "     		-depth      	<steps>     	Set maximum depth of link traversal. Do not use this feature for continuous querying.\n";
        manual += "     		-timer                  	Execute Timer.\n";

        // iterate through the arguments
        for (int i = 0; i < args.length; ++i) {
            // if we find a file flag
            if (args[i].equals("-sparqlfile")) {
                // look at next argument for file name
                if (i + 1 < args.length) {
                    // store file name for future use
                    String nextArg = args[i + 1];
                    queryFileArg.fileName = nextArg;
                }
            }

            if (args[i].equals("-datafile")) {
                // look at next argument for file name
                if (i + 1 < args.length) {
                    // store file name for future use
                    String nextArg = args[i + 1];
                    dataFileArg.fileName = nextArg;
                }
            }

            // if we find a debug flag
            if (args[i].equals("-debugger")) {
                // make sure we have no other args
                if (queryFileArg.ready() == false && testSuiteArg.ready() == false && stepsArg.ready() == false
                        && timerArg.ready() == false) {
                    // set debug flag for future use
                    debugArg.debug = true;
                }
            }

            // if we find test flag
            if (args[i].equals("-testsuite")) {
                // make sure we have no other args
                if (queryFileArg.ready() == false && debugArg.ready() == false && stepsArg.ready() == false) {
                    // set test flag for future use
                    testSuiteArg.test = true;
                }
            }

            // if we find a depth flag
            if (args[i].equals("-depth")) {
                // look at next argument for file name
                if (i + 1 < args.length) {
                    // store file name for future use
                    String nextArg = args[i + 1];
                    stepsArg.stepsArg = nextArg;
                }
            }

            // if we find timer flag
            if (args[i].equals("-timer")) {
                // make sure we have no other args
                if (debugArg.ready() == false) {
                    // set test flag for future use
                    timerArg.timer = true;
                }
            }
        }

        if (queryFileArg.ready() == false && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == false && timerArg.ready() == false) {
            System.out.println(manual);
            System.exit(0);
        } else if (queryFileArg.ready() == true && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == false && timerArg.ready() == false) {
            String fileName = queryFileArg.fileName;
            File file = new File(fileName);

            QueryProcessor queryProcessor = new FileQueryProcessor(file, false);
            queryProcessor.process();
            
            LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
            linkedDataManager.executeQueryOnWebOfLinkedData(null, false);
            System.exit(0);
        } else if (queryFileArg.ready() == true && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == false && timerArg.ready() == true) {
            String fileName = queryFileArg.fileName;
            File file = new File(fileName);

            QueryProcessor queryProcessor = new FileQueryProcessor(file, false);
            queryProcessor.process();
            LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
            linkedDataManager.executeQueryOnWebOfLinkedData(null, true);
            System.exit(0);
        } else if (queryFileArg.ready() == true && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == true && timerArg.ready() == false) {
            String fileName = queryFileArg.fileName;
            File file = new File(fileName);

            QueryProcessor queryProcessor = new FileQueryProcessor(file, false);
            queryProcessor.process();
            LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
            if (stepsArg.max < 0) {
                System.err.println("May not have a negative depth");
                System.exit(1);
            }
            linkedDataManager.executeQueryOnWebOfLinkedData(stepsArg.max, false);
            System.exit(0);
        } else if (queryFileArg.ready() == true && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == true && timerArg.ready() == true) {
            String fileName = queryFileArg.fileName;
            File file = new File(fileName);

            QueryProcessor queryProcessor = new FileQueryProcessor(file, false);
            queryProcessor.process();
            LinkedDataManager linkedDataManager = new LinkedDataManager(queryProcessor);
            if (stepsArg.max < 0) {
                System.err.println("May not have a negative depth");
                System.exit(1);
            }
            linkedDataManager.executeQueryOnWebOfLinkedData(stepsArg.max, true);
            System.exit(0);
        } else if (queryFileArg.ready() == true && dataFileArg.ready() == true && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == false && timerArg.ready() == false) {
            String queryFileName = queryFileArg.fileName;
            File queryFile = new File(queryFileName);
            String dataFileName = dataFileArg.fileName;
            File dataFile = new File(dataFileName);

            QueryProcessor queryProcessor = new FileQueryProcessor(queryFile, false);
            queryProcessor.process();
            NativeStoreStorageManager nativeStore = new NativeStoreStorageManager(queryProcessor, dataFile);
            nativeStore.executeQuery(false);
            System.exit(0);
        } else if (queryFileArg.ready() == true && dataFileArg.ready() == true && debugArg.ready() == false
                && testSuiteArg.ready() == false && stepsArg.ready() == false && timerArg.ready() == true) {
            String queryFileName = queryFileArg.fileName;
            File queryFile = new File(queryFileName);
            String dataFileName = dataFileArg.fileName;
            File dataFile = new File(dataFileName);

            QueryProcessor queryProcessor = new FileQueryProcessor(queryFile, false);
            queryProcessor.process();
            NativeStoreStorageManager nativeStore = new NativeStoreStorageManager(queryProcessor, dataFile);
            nativeStore.executeQuery(true);
            System.exit(0);
        } else if (queryFileArg.ready() == false && dataFileArg.ready() == false && debugArg.ready() == true
                && testSuiteArg.ready() == false && stepsArg.ready() == false && timerArg.ready() == false) {
            DMain.main(null);
        } else if (queryFileArg.ready() == false && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == true && stepsArg.ready() == false && timerArg.ready() == false) {
            SparqlTestSuite testSuiteManager = new SparqlTestSuite();
            testSuiteManager.executeTestSuite(false);
            System.exit(0);
        } else if (queryFileArg.ready() == false && dataFileArg.ready() == false && debugArg.ready() == false
                && testSuiteArg.ready() == true && stepsArg.ready() == false && timerArg.ready() == true) {
            SparqlTestSuite testSuiteManager = new SparqlTestSuite();
            testSuiteManager.executeTestSuite(true);
            System.exit(0);
        } else {
            System.err.println("Unexpected arguments or combination of arguments ...\n");
            System.err.println(manual);
            System.exit(1);
        }
    }
}

class FileArg {

    String fileName;

    boolean ready() {
        // if file name is not initialized or is empty
        if (fileName == null || fileName.equals("")) {
            return false;// return failure
        }
        // else file name is not empty
        else {
            // test to see that we can create a file with file name
            File file = new File(fileName);
            if (file.exists() == false) {
                System.err.println("The file name \"" + fileName + "\" is not found.");
                System.exit(1);
            }

            // clean-up
            file = null;

            // return success
            return true;
        }
    }
}

class DebugArg {

    boolean debug;

    boolean ready() {
        return debug;
    }
}

class TestSuiteArg {

    boolean test;

    boolean ready() {
        return test;
    }
}

class TimerArg {

    boolean timer;

    boolean ready() {
        return timer;
    }
}

class StepsArg {

    String stepsArg;
    Integer max = -1;

    boolean ready() {
        // if file name is not initialized or is empty
        if (stepsArg == null || stepsArg.equals("")) {
            return false;// return failure
        }
        // else file name is not empty
        else {
            try {
                // test to see that we can create a file with file name
                max = new Integer(stepsArg);

                // return success
                return true;
            } catch (Exception e) {
                System.err.println("The steps \"" + stepsArg + "\" should be an integer.");
                System.exit(1);
            }
        }

        return false;
    }
}
/*
 * Copyright (c) 2010, Rodolfo Kaplan Depena All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions and use of source code, binary forms, and documentation
 * are for personal and educational use only. 2. Redistributions and use of
 * source code, binary forms, and documentation must not be used for monetary
 * gain and/or for business purposes (PROFIT AND NON-PROFIT) of any sort without
 * the express written permission of Rodolfo Kaplan Depena. 3. Redistributions
 * of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 4. Redistributions in binary form
 * must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided
 * with the distribution. 5. All advertising materials mentioning features or
 * use of this software must display the following acknowledgment: This product
 * includes software developed by Rodolfo Kaplan Depena. Any use of this
 * software for monetary gain (or business purposes) of any sort without the
 * express written consent of Rodolfo Kaplan Depena is not allowed. 6. Neither
 * the name of the Rodolfo Kaplan Depena nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY RODOLFO KAPLAN DEPENA (AND CONTRIBUTORS) ''AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL RODOLFO KAPLAN DEPENA (AND
 * CONTRIBUTORS) BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */