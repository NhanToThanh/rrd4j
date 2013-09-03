package org.rrd4j.backend.spi.binary;

import java.io.IOException;

/**
 * Abstract byte array based backend.
 *
 */
public abstract class RrdByteArrayBackend extends RrdBinaryBackend {
    protected byte[] buffer;

    /**
     * <p>Constructor for RrdByteArrayBackend.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    protected RrdByteArrayBackend(String path) {
        super(path);
    }

    /**
     * <p>write.</p>
     *
     * @param offset a long.
     * @param bytes an array of byte.
     * @throws java.io.IOException if any.
     */
    protected synchronized void write(long offset, byte[] bytes) throws IOException {
        int pos = (int) offset;
        System.arraycopy(bytes, 0, buffer, pos, bytes.length);
    }

    /**
     * <p>read.</p>
     *
     * @param offset a long.
     * @param bytes an array of byte.
     * @throws java.io.IOException if any.
     */
    protected synchronized void read(long offset, byte[] bytes) throws IOException {
        int pos = (int) offset;
        if (pos + bytes.length <= buffer.length) {
            System.arraycopy(buffer, pos, bytes, 0, bytes.length);
        }
        else {
            throw new IOException("Not enough bytes available in memory; RRD " + getPath());
        }
    }

    /**
     * Returns the number of RRD bytes held in memory.
     *
     * @return Number of all RRD bytes.
     */
    public long getLength() {
        return buffer.length;
    }

    /**
     * {@inheritDoc}
     *
     * Reserves a memory section as a RRD storage.
     */
    protected void setLength(long length) throws IOException {
        if (length > Integer.MAX_VALUE) {
            throw new IOException("Illegal length: " + length);
        }

        buffer = new byte[(int) length];
    }

    /**
     * This method is required by the base class definition, but it does not
     * releases any memory resources at all.
     *
     * @throws java.io.IOException if any.
     */
    public void close() throws IOException {
        // NOP
    }

    /**
     * This method is overridden to disable high-level caching in frontend RRD4J classes.
     *
     * @return Always returns <code>false</code>. There is no need to cache anything in high-level classes
     *         since all RRD bytes are already in memory.
     */
    protected boolean isCachingAllowed() {
        return false;
    }
}