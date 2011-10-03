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
package uk.ac.susx.mlcl.byblo.io;

import uk.ac.susx.mlcl.lib.ObjectIndex;
import uk.ac.susx.mlcl.lib.io.AbstractTSVSource;
import uk.ac.susx.mlcl.lib.io.Source;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author Hamish Morgan &lt;hamish.morgan@sussex.ac.uk&gt;
 */
public class EntryFeatureSource
        extends AbstractTSVSource<EntryFeatureRecord>
        implements Source<EntryFeatureRecord> {

    private final ObjectIndex<String> entryIndex;

    private final ObjectIndex<String> featureIndex;

    public EntryFeatureSource(
            File file, Charset charset,
            ObjectIndex<String> entryIndex,
            ObjectIndex<String> featureIndex)
            throws FileNotFoundException, IOException {
        super(file, charset);
        if (entryIndex == null)
            throw new NullPointerException("entryIndex == null");
        if (featureIndex == null)
            throw new NullPointerException("featureIndex == null");
        this.entryIndex = entryIndex;
        this.featureIndex = featureIndex;
    }

    public EntryFeatureSource(File file, Charset charset,
            ObjectIndex<String> combinedIndex)
            throws FileNotFoundException, IOException {
        this(file, charset, combinedIndex, combinedIndex);
    }

    public EntryFeatureSource(File file, Charset charset)
            throws FileNotFoundException, IOException {
        this(file, charset, new ObjectIndex<String>());
    }

    public final ObjectIndex<String> getEntryIndex() {
        return entryIndex;
    }

    public ObjectIndex<String> getFeatureIndex() {
        return featureIndex;
    }

    public boolean isIndexCombined() {
        return getEntryIndex() == getFeatureIndex();
    }

    @Override
    public EntryFeatureRecord read() throws IOException {
        final int entryId = readEntry();
        parseValueDelimiter();
        final int featureId = readFeature();
        parseRecordDelimiter();
        return new EntryFeatureRecord(entryId, featureId);
    }

    protected final int readEntry() throws IOException {
        return entryIndex.get(parseString());
    }

    protected final int readFeature() throws IOException {
        return featureIndex.get(parseString());
    }
}
