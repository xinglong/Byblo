/*
 * Copyright (c) 2010-2012, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package uk.ac.susx.mlcl.byblo.commands;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import static uk.ac.susx.mlcl.TestConstants.*;
import uk.ac.susx.mlcl.byblo.enumerators.DoubleEnumeratingDeligate;
import uk.ac.susx.mlcl.byblo.enumerators.Enumerating;
import static uk.ac.susx.mlcl.lib.test.ExitTrapper.*;

/**
 *
 * @author Hamish I A Morgan &lt;hamish.morgan@sussex.ac.uk&gt;
 */
public class CountCommandTest {

    private static final String subject = CountCommand.class.getName();

    private void runWithAPI(File inInst, File outE, File outF, File outEF,
                            Charset charset, boolean preIndexEntries,
                            boolean preIndexFeatures)
            throws Exception {
        final CountCommand countTask = new CountCommand();
        countTask.setInstancesFile(inInst);
        countTask.setEntriesFile(outE);
        countTask.setFeaturesFile(outF);
        countTask.setEventsFile(outEF);
        countTask.setCharset(charset);
        DoubleEnumeratingDeligate idx = new DoubleEnumeratingDeligate(
                Enumerating.DEFAULT_TYPE, preIndexEntries, preIndexFeatures, null, null);
        countTask.setIndexDeligate(idx);
        countTask.runCommand();

        assertTrue("Output files not created: " + outE, outE.exists());
        assertTrue("Output files not created: " + outF, outF.exists());
        assertTrue("Output files not created: " + outEF, outEF.exists());

        assertTrue("Empty output file found: " + outE, outE.length() > 0);
        assertTrue("Empty output file found: " + outF, outF.length() > 0);
        assertTrue("Empty output file found: " + outEF, outEF.length() > 0);
    }

    private void runWithCLI(
            File inInst, File outE, File outF, File outEF, Charset charset,
            boolean preindexedEntries, boolean preindexedFeatures)
            throws Exception {

        String[] args = {
            "--input", inInst.toString(),
            "--output-entries", outE.toString(),
            "--output-features", outF.toString(),
            "--output-entry-features", outEF.toString(),
            "--charset", charset.name()
        };

        if (preindexedEntries) {
            List<String> tmp = new ArrayList<String>(Arrays.asList(args));
            tmp.add("--enumerated-entries");
            args = tmp.toArray(new String[0]);
        }
        if (preindexedFeatures) {
            List<String> tmp = new ArrayList<String>(Arrays.asList(args));
            tmp.add("--enumerated-features");
            args = tmp.toArray(new String[0]);
        }

        try {
            enableExistTrapping();
            CountCommand.main(args);
        } finally {
            disableExitTrapping();
        }


        assertTrue("Output files not created: " + outE, outE.exists());
        assertTrue("Output files not created: " + outF, outF.exists());
        assertTrue("Output files not created: " + outEF, outEF.exists());

        assertTrue("Empty output file found: " + outE, outE.length() > 0);
        assertTrue("Empty output file found: " + outF, outF.length() > 0);
        assertTrue("Empty output file found: " + outEF, outEF.length() > 0);
    }

    private void runExpectingNullPointer(File inInst, File outE, File outF,
                                         File outEF, Charset charset,
                                         boolean preIndexEntries,
                                         boolean preIndexFeatures) throws Exception {
        try {
            runWithAPI(inInst, outE, outF, outEF, charset, preIndexEntries,
                       preIndexFeatures);
            fail("NullPointerException should have been thrown.");
        } catch (NullPointerException ex) {
            // pass
        }
    }

    private void runExpectingIllegalState(File inInst, File outE, File outF,
                                          File outEF, Charset charset,
                                          boolean preIndexEntries,
                                          boolean preIndexFeatures) throws Exception {
        try {
            runWithAPI(inInst, outE, outF, outEF, charset, preIndexEntries,
                       preIndexFeatures);
            fail("IllegalStateException should have been thrown.");
        } catch (IllegalStateException ex) {
            // pass
        }
    }

    @Test
    public void testRunOnFruitAPI() throws Exception {
        System.out.println("Testing " + subject + " on " + TEST_FRUIT_INPUT);

        final String fruitPrefix = TEST_FRUIT_INPUT.getName();
        final File eActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".entries");
        final File fActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".features");
        final File efActual = new File(TEST_OUTPUT_DIR,
                                       fruitPrefix + ".events");

        eActual.delete();
        fActual.delete();
        efActual.delete();

        runWithAPI(TEST_FRUIT_INPUT, eActual, fActual, efActual,
                   DEFAULT_CHARSET, false, false);

//        assertTrue("Output entries file differs from sampledata file.",
//                   WeightedTokenSource.equal(eActual, TEST_FRUIT_ENTRIES,
//                                             DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             true, false));
//        assertTrue("Output features file differs from test data file.",
//                   WeightedTokenSource.equal(fActual, TEST_FRUIT_FEATURES,
//                                             DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             true, false));
//        assertTrue("Output entry/features file differs from test data file.",
//                   TokenPairSource.equal(efActual, TEST_FRUIT_ENTRY_FEATURES,
//                                         DEFAULT_CHARSET, true, true, true, true));
    }

    @Test
    public void testRunOnFruitCLI() throws Exception {
        System.out.println("Testing " + subject + " on " + TEST_FRUIT_INPUT);

        final String fruitPrefix = TEST_FRUIT_INPUT.getName();
        final File eActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".entries");
        final File fActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".features");
        final File efActual = new File(TEST_OUTPUT_DIR,
                                       fruitPrefix + ".events");

        eActual.delete();
        fActual.delete();
        efActual.delete();

        runWithCLI(TEST_FRUIT_INPUT, eActual, fActual, efActual,
                   DEFAULT_CHARSET, false, false);

//        assertTrue("Output entries file differs from sampledata file.",
//                   WeightedTokenSource.equal(eActual, TEST_FRUIT_ENTRIES,
//                                             DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             true, false));
//        assertTrue("Output features file differs from test data file.",
//                   WeightedTokenSource.equal(fActual, TEST_FRUIT_FEATURES,
//                                             DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, false, null),
//                                             true, false));
//        assertTrue("Output entry/features file differs from test data file.",
//                   TokenPairSource.equal(efActual, TEST_FRUIT_ENTRY_FEATURES,
//                                         DEFAULT_CHARSET,
//                                         new DoubleEnumeratingDeligate(EnumeratorType.JDBC, false, false, null, null),
//                                         new DoubleEnumeratingDeligate(EnumeratorType.JDBC, false, false, null, null),
//                                         true, true, true, true));
    }

    @Test
    public void testRunOnFruitAPI_Indexed() throws Exception {
        System.out.println(
                "Testing " + subject + " on " + TEST_FRUIT_INPUT_INDEXED + " (Indexed)");

        final String fruitPrefix = TEST_FRUIT_INPUT_INDEXED.getName();
        final File eActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".entries");
        final File fActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".features");
        final File efActual = new File(TEST_OUTPUT_DIR,
                                       fruitPrefix + ".events");

        eActual.delete();
        fActual.delete();
        efActual.delete();

        runWithAPI(TEST_FRUIT_INPUT_INDEXED, eActual, fActual, efActual,
                   DEFAULT_CHARSET, true, true);

//        assertTrue(MessageFormat.format("Output entries file \"{0}\" differs from expected file \"{1}\".", eActual, TEST_FRUIT_INDEXED_ENTRIES),
//                   WeightedTokenSource.equal(eActual, TEST_FRUIT_INDEXED_ENTRIES, DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             BybloIO.ENTRIES_SKIP_INDEXED_COLUMN_1, false));
//
//        assertTrue("Output features file differs from test data file.",
//                   WeightedTokenSource.equal(fActual,
//                                             TEST_FRUIT_INDEXED_FEATURES,
//                                             DEFAULT_CHARSET, new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             BybloIO.FEATURES_SKIP_INDEXED_COLUMN_1, false));
//        assertTrue("Output entry/features file differs from test data file.",
//                   TokenPairSource.equal(efActual,
//                                         TEST_FRUIT_INDEXED_ENTRY_FEATURES,
//                                         DEFAULT_CHARSET,
//                                         new DoubleEnumeratingDeligate(EnumeratorType.JDBC, true, true, null, null),
//                                         new DoubleEnumeratingDeligate(EnumeratorType.JDBC, true, true, null, null),
//                                         BybloIO.EVENTS_SKIP_INDEXED_COLUMN_1,
//                                         BybloIO.EVENTS_SKIP_INDEXED_COLUMN_2,
//                                         false, false));
    }

    @Test
    public void testRunOnFruitCLI_Indexed() throws Exception {
        System.out.println(
                "Testing " + subject + " on " + TEST_FRUIT_INPUT_INDEXED + " (Indexed)");

        final String fruitPrefix = TEST_FRUIT_INPUT_INDEXED.getName();
        final File eActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".entries");
        final File fActual = new File(TEST_OUTPUT_DIR, fruitPrefix + ".features");
        final File efActual = new File(TEST_OUTPUT_DIR,
                                       fruitPrefix + ".events");

        eActual.delete();
        fActual.delete();
        efActual.delete();

        runWithCLI(TEST_FRUIT_INPUT_INDEXED, eActual, fActual, efActual,
                   DEFAULT_CHARSET, true, true);

//        assertTrue("Output entries file differs from sampledata file.",
//                   WeightedTokenSource.equal(eActual,
//                                             TEST_FRUIT_INDEXED_ENTRIES,
//                                             DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             BybloIO.ENTRIES_SKIP_INDEXED_COLUMN_1, false));
//        assertTrue("Output features file differs from test data file.",
//                   WeightedTokenSource.equal(fActual,
//                                             TEST_FRUIT_INDEXED_FEATURES,
//                                             DEFAULT_CHARSET,
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             new SingleEnumeratingDeligate(EnumeratorType.JDBC, true, null),
//                                             BybloIO.FEATURES_SKIP_INDEXED_COLUMN_1, false));
//        assertTrue("Output entry/features file differs from test data file.",
//                   TokenPairSource.equal(efActual,
//                                         TEST_FRUIT_INDEXED_ENTRY_FEATURES,
//                                         DEFAULT_CHARSET,
//                                         new DoubleEnumeratingDeligate(EnumeratorType.JDBC, true, true, null, null),
//                                         new DoubleEnumeratingDeligate(EnumeratorType.JDBC, true, true, null, null),
//                                         BybloIO.EVENTS_SKIP_INDEXED_COLUMN_1,
//                                         BybloIO.EVENTS_SKIP_INDEXED_COLUMN_2,
//                                         false, false));
    }

    @Test
    public void testMissingParameters() throws Exception {
        System.out.println("Testing " + subject + " for bad parameterisation.");

        final File instIn = TEST_FRUIT_INPUT;
        final String fruitPrefix = TEST_FRUIT_INPUT.getName();
        final File eOut = new File(TEST_OUTPUT_DIR, fruitPrefix + ".entries");
        final File fOut = new File(TEST_OUTPUT_DIR, fruitPrefix + ".features");
        final File efOut = new File(TEST_OUTPUT_DIR,
                                    fruitPrefix + ".events");

        runExpectingNullPointer(null, eOut, fOut, efOut, DEFAULT_CHARSET, false,
                                false);
        runExpectingNullPointer(instIn, null, fOut, efOut, DEFAULT_CHARSET,
                                false, false);
        runExpectingNullPointer(instIn, eOut, null, efOut, DEFAULT_CHARSET,
                                false, false);
        runExpectingNullPointer(instIn, eOut, fOut, null, DEFAULT_CHARSET, false,
                                false);
        runExpectingNullPointer(instIn, eOut, fOut, efOut, null, false, false);

        File dir = TEST_OUTPUT_DIR;
        runExpectingIllegalState(dir, eOut, fOut, efOut, DEFAULT_CHARSET, false,
                                 false);
        runExpectingIllegalState(instIn, dir, fOut, efOut, DEFAULT_CHARSET,
                                 false, false);
        runExpectingIllegalState(instIn, eOut, dir, efOut, DEFAULT_CHARSET,
                                 false, false);
        runExpectingIllegalState(instIn, eOut, fOut, dir, DEFAULT_CHARSET, false,
                                 false);
    }

}
