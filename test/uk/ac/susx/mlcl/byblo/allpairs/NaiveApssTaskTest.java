/*
 * Copyright (c) 2010-2011, University of Sussex
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
package uk.ac.susx.mlcl.byblo.allpairs;

import uk.ac.susx.mlcl.byblo.io.WeightedEntryFeatureSource;
import uk.ac.susx.mlcl.byblo.io.WeightedEntryFeatureVectorSource;
import com.google.common.base.Predicate;
import uk.ac.susx.mlcl.byblo.measure.Proximity;
import uk.ac.susx.mlcl.lib.io.IOUtil;
import java.io.File;
import java.nio.charset.Charset;
import uk.ac.susx.mlcl.byblo.measure.Jaccard;
import uk.ac.susx.mlcl.lib.collect.Pair;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Hamish Morgan (hamish.morgan@sussex.ac.uk)
 */
public class NaiveApssTaskTest {

    private static final File FEATURES_FILE =
            new File("sampledata", "bnc-gramrels-fruit.features");

    private static final Charset CHARSET = IOUtil.DEFAULT_CHARSET;

    private static final Proximity MEASURE = new Jaccard();

    private static final Predicate<Pair> PAIR_FILTER =
            Pair.similarityGTE(0.1);

    public NaiveApssTaskTest() {
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

    /**
     * Test of runTask method, of class AbstractAPSS2.
     */
    @Test
    public void testRunTask() throws Exception {
        System.out.println("runTask");

        NaiveApssTask instance = new NaiveApssTask();

        WeightedEntryFeatureSource mdbsa = new WeightedEntryFeatureSource(
                FEATURES_FILE, CHARSET);
        WeightedEntryFeatureVectorSource vsa = mdbsa.getVectorSource();

        WeightedEntryFeatureSource mdbsb = new WeightedEntryFeatureSource(
                FEATURES_FILE, CHARSET, mdbsa.getEntryIndex(), mdbsa.
                getFeatureIndex());
        WeightedEntryFeatureVectorSource vsb = mdbsb.getVectorSource();

        List<Pair> result = new ArrayList<Pair>();

        instance.setSourceA(vsa);
        instance.setSourceB(vsb);
        instance.setSink(IOUtil.asSink(result));
        instance.setMeasure(MEASURE);
        instance.setProducatePair(PAIR_FILTER);

        instance.run();


        assertTrue(!result.isEmpty());
//        System.out.println(result);
    }
}
