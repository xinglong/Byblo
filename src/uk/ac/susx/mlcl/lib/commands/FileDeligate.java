/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.susx.mlcl.lib.commands;

import com.beust.jcommander.Parameter;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.nio.charset.Charset;
import uk.ac.susx.mlcl.lib.Checks;
import uk.ac.susx.mlcl.lib.io.Files;

/**
 *
 * @author hiam20
 */
public class FileDeligate extends AbstractDeligate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Parameter(names = {"-c", "--charset"},
    description = "The character set encoding to use for both reading input and writing output files.")
    private Charset charset = Files.DEFAULT_CHARSET;

    @Parameter(names = {"--disable-compact-format"}, hidden = true)
    private boolean compactFormatDisabled = false;

    public FileDeligate(Charset charset) {
        setCharset(charset);
    }

    public FileDeligate() {
    }

    public boolean isCompactFormatDisabled() {
        return compactFormatDisabled;
    }

    public void setCompactFormatDisabled(boolean compactFormatDisabled) {
        this.compactFormatDisabled = compactFormatDisabled;
    }

    public final Charset getCharset() {
        return charset;
    }

    public final void setCharset(Charset charset) {
        Checks.checkNotNull(charset);
        this.charset = charset;
    }

    @Override
    protected Objects.ToStringHelper toStringHelper() {
        return super.toStringHelper().
                add("charset", getCharset()).
                add("compact", !isCompactFormatDisabled());
    }

}