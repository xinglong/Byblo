/*
 * Copyright (c) 2010, Hamish Morgan.
 * All Rights Reserved.
 */
package uk.ac.susx.mlcl.byblo.commands;

import com.google.common.io.Files;
import java.io.File;
import static java.text.MessageFormat.*;
import org.junit.*;
import static org.junit.Assert.*;
import static uk.ac.susx.mlcl.TestConstants.*;
import uk.ac.susx.mlcl.byblo.io.*;

/**
 *
 * @author hamish
 */
public class IndexTPCommandTest {

    public IndexTPCommandTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testRunOnFruitAPI() throws Exception {
        System.out.println("Testing " + IndexTPCommandTest.class.getName()
                + " on " + TEST_FRUIT_INPUT);

        final String name = TEST_FRUIT_INPUT.getName();
        final File out = new File(TEST_OUTPUT_DIR, name + ".indexed");
        final File idx1 = new File(TEST_OUTPUT_DIR, name + ".entry-index");
        final File idx2 = new File(TEST_OUTPUT_DIR, name + ".feature-index");

        deleteIfExist(out, idx1, idx2);

        indexTP(TEST_FRUIT_INPUT, out, idx1, idx2, false, false, true);


        assertTrue(format(
                "Output entries file \"{0}\" differs from expected file \"{1}\".",
                out, TEST_FRUIT_INPUT_INDEXED),
                   TokenPairSource.equal(out, TEST_FRUIT_INPUT_INDEXED,
                                         DEFAULT_CHARSET));
        assertTrue("Output features file differs from test data file.",
                   Files.equal(idx1, TEST_FRUIT_ENTRY_INDEX));
        assertTrue("Output entry/features file differs from test data file.",
                   Files.equal(idx2, TEST_FRUIT_FEATURE_INDEX));


        File out2 = suffix(out, ".unindexed");
        unindexTP(out, out2, idx1, idx2, false, false, true);



        TokenPairSource.equal(out, out2, DEFAULT_CHARSET);

    }

    @Test
    public void testRunOnFruitAPI_skipboth_compact() throws Exception {
        testRunOnFruitAPI("compact-skipboth-", true, true, true);
    }

    @Test
    public void testRunOnFruitAPI_skipleft_compact() throws Exception {
        testRunOnFruitAPI("compact-skipleft-", true, false, true);
    }

    @Test
    public void testRunOnFruitAPI_skipright_compact() throws Exception {
        testRunOnFruitAPI("compact-skipright-", false, true, true);
    }

    @Test
    public void testRunOnFruitAPI_skipboth_verbose() throws Exception {
        testRunOnFruitAPI("verbose-skipboth-", true, true, false);
    }

    @Test
    public void testRunOnFruitAPI_skipleft_verbose() throws Exception {
        testRunOnFruitAPI("verbose-skipleft-", true, false, false);
    }

    @Test
    public void testRunOnFruitAPI_skipright_verbose() throws Exception {
        testRunOnFruitAPI("verbose-skipright-", false, true, false);
    }

    public void testRunOnFruitAPI(
            String prefix, boolean skip1, boolean skip2, boolean compact) throws Exception {
        System.out.println("Testing " + IndexTPCommandTest.class.getName()
                + " on " + TEST_FRUIT_INPUT);

        final String name = TEST_FRUIT_INPUT.getName();
        final File out = new File(TEST_OUTPUT_DIR, prefix + name + ".indexed");
        File out2 = suffix(out, ".unindexed");
        final File idx1 = new File(TEST_OUTPUT_DIR, name + ".entry-index");
        final File idx2 = new File(TEST_OUTPUT_DIR, name + ".feature-index");

        deleteIfExist(out, idx1, idx2);

        indexTP(TEST_FRUIT_INPUT, out, idx1, idx2, skip1, skip2, compact);

        unindexTP(out, out2, idx1, idx2, skip1, skip2, compact);

        TokenPairSource.equal(out, out2, DEFAULT_CHARSET);

    }

    private static void indexTP(File from, File to, File index1, File index2,
                                boolean skip1, boolean skip2, boolean compact)
            throws Exception {
        assertValidInputFiles(from);
        assertValidOutputFiles(to, index1, index2);

        IndexTPCommand unindex = new IndexTPCommand();
        unindex.getFilesDeligate().setCharset(DEFAULT_CHARSET);
        unindex.getFilesDeligate().setSourceFile(from);
        unindex.getFilesDeligate().setDestinationFile(to);
        unindex.getFilesDeligate().setCompactFormatDisabled(!compact);
        unindex.setIndexDeligate(new IndexDeligatePairImpl(true, true, index1, index2, skip1, skip2));
        unindex.runCommand();

        assertValidInputFiles(to, index1, index2);
        assertSizeGT(from, to);
    }

    private static void unindexTP(File from, File to, File index1, File index2,
                                  boolean skip1, boolean skip2, boolean compact)
            throws Exception {
        assertValidInputFiles(from, index1, index2);
        assertValidOutputFiles(to);

        UnindexTPCommand unindex = new UnindexTPCommand();
        unindex.getFilesDeligate().setCharset(DEFAULT_CHARSET);
        unindex.getFilesDeligate().setSourceFile(from);
        unindex.getFilesDeligate().setDestinationFile(to);
        unindex.getFilesDeligate().setCompactFormatDisabled(!compact);

        unindex.setIndexDeligate(new IndexDeligatePairImpl(true, true, index1, index2, skip1, skip2));
        unindex.runCommand();

        assertValidInputFiles(to);
        assertSizeGT(to, from);
    }

}