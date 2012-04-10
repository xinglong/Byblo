/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.byblo.commands;

import uk.ac.susx.mlcl.byblo.io.IndexDeligatePairImpl;
import com.beust.jcommander.ParametersDelegate;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import uk.ac.susx.mlcl.byblo.io.*;
import uk.ac.susx.mlcl.lib.Checks;
import uk.ac.susx.mlcl.lib.Enumerators;
import uk.ac.susx.mlcl.lib.io.Sink;
import uk.ac.susx.mlcl.lib.io.Source;

/**
 *
 * @author hiam20
 */
public class IndexTPCommand extends AbstractCopyCommand<TokenPair> {

    @ParametersDelegate
    private IndexDeligatePair indexDeligate;

    public IndexTPCommand(
            File sourceFile, File destinationFile, Charset charset,
            IndexDeligatePair indexDeligate) {
        super(sourceFile, destinationFile, charset);
        this.indexDeligate = indexDeligate;
    }

    public IndexTPCommand() {
        super();
        indexDeligate = new IndexDeligatePairImpl();
    }

    @Override
    public void runCommand() throws Exception {
        Checks.checkNotNull("indexFile1", indexDeligate.getEntryIndexFile());
        Checks.checkNotNull("indexFile2", indexDeligate.getFeatureIndexFile());

        super.runCommand();

        Enumerators.saveStringEnumerator(indexDeligate.getEntryEnumerator(),
                                         indexDeligate.getEntryIndexFile());
        Enumerators.saveStringEnumerator(indexDeligate.getFeatureEnumerator(),
                                         indexDeligate.getFeatureIndexFile());
    }

    @Override
    protected Source<TokenPair> openSource(File file)
            throws FileNotFoundException, IOException {

        return TokenPairSource.open(
                file, getFilesDeligate().getCharset(),
                sourceIndexDeligate());
    }

    @Override
    protected Sink<TokenPair> openSink(File file)
            throws FileNotFoundException, IOException {
        return TokenPairSink.open(
                file, getFilesDeligate().getCharset(),
                sinkIndexDeligate(),
                !getFilesDeligate().isCompactFormatDisabled());
    }

    public IndexDeligatePair getIndexDeligate() {
        return indexDeligate;
    }

    public void setIndexDeligate(IndexDeligatePair indexDeligate) {
        this.indexDeligate = indexDeligate;
    }

    protected IndexDeligatePair sourceIndexDeligate() {
        return IndexDeligates.decorateEnumerated(indexDeligate, false);
    }

    protected IndexDeligatePair sinkIndexDeligate() {
        return IndexDeligates.decorateEnumerated(indexDeligate, true);
    }

}
