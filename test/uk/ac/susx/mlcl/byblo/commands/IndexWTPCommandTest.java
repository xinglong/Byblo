/*
 * Copyright (c) 2010, Hamish Morgan.
 * All Rights Reserved.
 */
package uk.ac.susx.mlcl.byblo.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.*;
import static uk.ac.susx.mlcl.TestConstants.*;
import uk.ac.susx.mlcl.byblo.io.IndexDeligatePairImpl;
import uk.ac.susx.mlcl.byblo.io.TokenPairSource;
import uk.ac.susx.mlcl.byblo.io.WeightedTokenPairSource;
import static org.junit.Assert.*;
import uk.ac.susx.mlcl.byblo.io.*;
import uk.ac.susx.mlcl.lib.collect.Indexed;
import uk.ac.susx.mlcl.lib.collect.SparseDoubleVector;
import uk.ac.susx.mlcl.lib.io.Tell;

/**
 *
 * @author hamish
 */
public class IndexWTPCommandTest {

    public IndexWTPCommandTest() {
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
    public void testRunOnFruitAPI_noskip_compact() throws Exception {
        testRunOnFruitAPI("compact-noskip-", false, false, true);
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
    public void testRunOnFruitAPI_noskip_verbose() throws Exception {
        testRunOnFruitAPI("verbose-noskip-", false, false, false);
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
        System.out.println("Testing " + IndexWTPCommandTest.class.getName()
                + " on " + TEST_FRUIT_ENTRY_FEATURES);

        final String name = TEST_FRUIT_ENTRY_FEATURES.getName();
        final File out = new File(TEST_OUTPUT_DIR, prefix + name + ".indexed");
        File out2 = suffix(out, ".unindexed");
        final File idx1 = new File(TEST_OUTPUT_DIR, name + ".entry-index");
        final File idx2 = new File(TEST_OUTPUT_DIR, name + ".feature-index");

        deleteIfExist(out, idx1, idx2);

        indexWTP(TEST_FRUIT_ENTRY_FEATURES, out, idx1, idx2, skip1, skip2,
                 compact);

        unindexWTP(out, out2, idx1, idx2, skip1, skip2, compact);

        TokenPairSource.equal(out, out2, DEFAULT_CHARSET);

    }

    @Test
    public void testCompareSkipVsNoSkip() throws Exception {
        System.out.println("Testing " + IndexWTPCommandTest.class.getName()
                + " on " + TEST_FRUIT_ENTRY_FEATURES);


        final String name = TEST_FRUIT_ENTRY_FEATURES.getName();
        String prefixa = "wtp-noskip-";
        String prefixb = "wtp-skip-";

        final File outa = new File(TEST_OUTPUT_DIR, prefixa + name + ".indexed");
        final File outb = new File(TEST_OUTPUT_DIR, prefixb + name + ".indexed");

        final File idx1a = new File(TEST_OUTPUT_DIR,
                                    prefixa + name + ".entry-index");
        final File idx2a = new File(TEST_OUTPUT_DIR,
                                    prefixa + name + ".feature-index");
        final File idx1b = new File(TEST_OUTPUT_DIR,
                                    prefixb + name + ".entry-index");
        final File idx2b = new File(TEST_OUTPUT_DIR,
                                    prefixb + name + ".feature-index");

        boolean skip1a = false;
        boolean skip2a = false;
        boolean skip1b = true;
        boolean skip2b = true;

        deleteIfExist(outa, idx1a, idx2a, outb, idx1b, idx2b);

        indexWTP(TEST_FRUIT_ENTRY_FEATURES, outa, idx1a, idx2a, skip1a, skip2a,
                 true);
        indexWTP(TEST_FRUIT_ENTRY_FEATURES, outb, idx1b, idx2b, skip1b, skip2b, true);

        // Read back the data checking it's identical
        {
            WeightedTokenPairSource wtpsa = WeightedTokenPairSource.open(
                    outa, DEFAULT_CHARSET,
                    new IndexDeligatePairImpl(true, true, skip1a, skip2a));
            WeightedTokenPairSource wtpsb = WeightedTokenPairSource.open(
                    outb, DEFAULT_CHARSET,
                    new IndexDeligatePairImpl(true, true, skip1b, skip2b));
            List<Tell> pa = new ArrayList<Tell>();
            List<Tell> pb = new ArrayList<Tell>();
            List<Weighted<TokenPair>> va = new ArrayList<Weighted<TokenPair>>();
            List<Weighted<TokenPair>> vb = new ArrayList<Weighted<TokenPair>>();

            // sequential
            while (wtpsa.hasNext() && wtpsb.hasNext()) {
                pa.add(wtpsa.position());
                pb.add(wtpsb.position());
                Weighted<TokenPair> a = wtpsa.read();
                Weighted<TokenPair> b = wtpsb.read();
                va.add(a);
                vb.add(b);
                assertEquals(a, b);
            }
            assertTrue(!wtpsa.hasNext());
            assertTrue(!wtpsb.hasNext());

            // random
            Random rand = new Random(0);
            for (int i = 0; i < 1000; i++) {
                int j = rand.nextInt(pa.size());
                wtpsa.position(pa.get(j));
                wtpsb.position(pb.get(j));
                Weighted<TokenPair> a = wtpsa.read();
                Weighted<TokenPair> b = wtpsb.read();

                assertEquals(va.get(j), a);
                assertEquals(vb.get(j), b);
                assertEquals(a, b);
            }
        }

        // Read back the data again, this time as vectors
        {
            WeightedTokenPairVectorSource wtpsa = WeightedTokenPairSource.open(
                    outa, DEFAULT_CHARSET,
                    new IndexDeligatePairImpl(true, true, skip1a, skip2a)).
                    getVectorSource();
            WeightedTokenPairVectorSource wtpsb = WeightedTokenPairSource.open(
                    outb, DEFAULT_CHARSET,
                    new IndexDeligatePairImpl(true, true, skip1b, skip2b)).
                    getVectorSource();

            List<Tell> pa = new ArrayList<Tell>();
            List<Tell> pb = new ArrayList<Tell>();
            List<Indexed<SparseDoubleVector>> va = new ArrayList<Indexed<SparseDoubleVector>>();
            List<Indexed<SparseDoubleVector>> vb = new ArrayList<Indexed<SparseDoubleVector>>();

            // sequential
            while (wtpsa.hasNext() && wtpsb.hasNext()) {
                pa.add(wtpsa.position());
                pb.add(wtpsb.position());

                Indexed<SparseDoubleVector> a = wtpsa.read();
                Indexed<SparseDoubleVector> b = wtpsb.read();
                va.add(a);
                vb.add(b);
                assertEquals(a, b);
            }
            assertTrue(!wtpsa.hasNext());
            assertTrue(!wtpsb.hasNext());

            // random
            Random rand = new Random(0);
            for (int i = 0; i < 1000; i++) {
                int j = rand.nextInt(pa.size());
                wtpsa.position(pa.get(j));
                wtpsb.position(pb.get(j));
                Indexed<SparseDoubleVector> a = wtpsa.read();
                Indexed<SparseDoubleVector> b = wtpsb.read();

                assertEquals(va.get(j), a);
                assertEquals(va.get(j).value(), a.value());
                assertEquals(vb.get(j), b);
                assertEquals(vb.get(j).value(), b.value());
                assertEquals(a, b);
                assertEquals(a.value(), b.value());
            }
        }
    }

    private static void indexWTP(File from, File to, File index1, File index2,
                                 boolean skip1, boolean skip2, boolean compact)
            throws Exception {
        assertValidInputFiles(from);
        assertValidOutputFiles(to, index1, index2);

        IndexWTPCommand unindex = new IndexWTPCommand();
        unindex.getFilesDeligate().setCharset(DEFAULT_CHARSET);
        unindex.getFilesDeligate().setSourceFile(from);
        unindex.getFilesDeligate().setDestinationFile(to);
        unindex.getFilesDeligate().setCompactFormatDisabled(!compact);
        unindex.setIndexDeligate(new IndexDeligatePairImpl(true, true, index1, index2, skip1, skip2));
        unindex.runCommand();

        assertValidInputFiles(to, index1, index2);
        assertSizeGT(from, to);
    }

    private static void unindexWTP(File from, File to, File index1, File index2,
                                   boolean skip1, boolean skip2, boolean compact)
            throws Exception {
        assertValidInputFiles(from, index1, index2);
        assertValidOutputFiles(to);

        UnindexWTPCommand unindex = new UnindexWTPCommand();
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