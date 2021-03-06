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
package uk.ac.susx.mlcl.byblo.measures;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;
import static uk.ac.susx.mlcl.TestConstants.*;
import uk.ac.susx.mlcl.byblo.Tools;
import static uk.ac.susx.mlcl.lib.test.ExitTrapper.*;

/**
 *
 * @author Hamish I A Morgan &lt;hamish.morgan@sussex.ac.uk&gt;
 */
public class ConfusionTest {

    @Test
    public void testConfusionCLI() throws Exception {
        System.out.println("Testing Confusion from main method.");

        File output = new File(TEST_OUTPUT_DIR, FRUIT_NAME + ".Confusion");
        output.delete();

        try {
        enableExistTrapping();
        Tools.main(new String[]{
                    "allpairs",
                    "--charset", "UTF-8",
                    "--measure", "Confusion",
                    "--input", TEST_FRUIT_EVENTS.toString(),
                    "--input-features", TEST_FRUIT_FEATURES.toString(),
                    "--input-entries", TEST_FRUIT_ENTRIES.toString(),
                    "--output", output.toString()
                });
        } finally {
            disableExitTrapping();
        }

        assertTrue("Output file " + output + " does not exist.", output.exists());
        assertTrue("Output file " + output + " is empty.", output.length() > 0);
    }
}
