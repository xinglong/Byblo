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
 * POSSIBILITY OF SUCH DAMAGE.To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.lib.tasks;

import com.google.common.base.Objects;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import static java.text.MessageFormat.format;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.susx.mlcl.lib.Checks;

/**
 *
 * @author Hamish I A Morgan &lt;hamish.morgan@sussex.ac.uk&gt;
 */
public class FileCopyTask extends AbstractTask {

    private static final Log LOG = LogFactory.getLog(FileCopyTask.class);

    private File sourceFile;

    private File destFile;

    public FileCopyTask(File sourceFile, File destinationFile) {
        setSrcFile(sourceFile);
        setDstFile(destinationFile);
    }

    public FileCopyTask() {
    }

    protected static void copy(File from, File to) throws IOException {

        if (!to.exists()) {
            to.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {

            source = new FileInputStream(from).getChannel();
            destination = new FileOutputStream(to).getChannel();

            long remaining = source.size();
            while (remaining > 0)
                remaining -= destination.transferFrom(source, 0, remaining);

        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    @Override
    protected void runTask() throws Exception {
        LOG.info(format("Copying file from \"{0}\" to \"{1}\".",
                        getSrcFile(), getDstFile()));

        // Check the configuration state
        if (sourceFile.equals(destFile))
            throw new IllegalStateException("sourceFile equals destinationFile");

        copy(sourceFile, destFile);

        if (LOG.isInfoEnabled())
            LOG.info("Completed copy.");
    }

    public final File getSrcFile() {
        return sourceFile;
    }

    public final File getDstFile() {
        return destFile;
    }

    public final void setSrcFile(final File sourceFile) throws NullPointerException {
        Checks.checkNotNull("sourceFile", sourceFile);
        this.sourceFile = sourceFile;
    }

    public final void setDstFile(final File destFile) throws NullPointerException {
        Checks.checkNotNull("destFile", destFile);
        this.destFile = destFile;
    }

    @Override
    protected Objects.ToStringHelper toStringHelper() {
        return super.toStringHelper().
                add("in", sourceFile).
                add("out", destFile);
    }

}
