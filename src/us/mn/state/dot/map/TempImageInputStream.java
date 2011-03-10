/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.map;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.imageio.stream.ImageInputStreamImpl;

/**
 * A temporary image input stream creates a temporary file to cache an image
 * input stream so that it can be used multiple times.  This is similar to
 * javax.imageio.stream.FileCacheImageInputStream, but more useful, since it
 * can be reused without reopening the original stream.
 *
 * @author Douglas Lau
 */
public class TempImageInputStream extends ImageInputStreamImpl {

	/** Size of block buffer */
	static protected final int BLOCK_SZ = 4096;

	/** Buffer to read from stream */
	protected final byte[] buf = new byte[BLOCK_SZ];

	/** Source input stream to read from */
	protected final InputStream i_stream;

	/** Has the end of stream been reached? */
	protected boolean end_of_stream = false;

	/** Temporary file name for caching stream data */
	protected final File tempFile;

	/** Temporary file to cache stream data */
	protected final RandomAccessFile temp;

	/** Count of bytes written to temp file */
	protected long n_written = 0;

	/** Is the stream destroyed? */
	protected boolean is_destroyed = false;

	/** Create a temporary image input stream */
	public TempImageInputStream(InputStream is) throws IOException {
		i_stream = is;
		tempFile = File.createTempFile("timage", ".png", null);
		temp = new RandomAccessFile(tempFile, "rw");
	}

	/** Check if the stream is cached (yes, it is) */
	public boolean isCached() {
		return true;
	}

	/** Check if the stream is a cached file (why yes!) */
	public boolean isCachedFile() {
		return true;
	}

	/** Check if the stream is cached in memeory (no) */
	public boolean isCachedMemory() {
		return false;
	}

	/** Transfer data from input stream to temp file.
	 * @param pos Minimum position to read. */
	protected void transferBlocks(long pos) throws IOException {
		if(pos < n_written || end_of_stream)
			return;
		temp.seek(n_written);
		while(n_written < pos) {
			int n_bytes = i_stream.read(buf, 0, BLOCK_SZ);
			if(n_bytes == -1) {
				end_of_stream = true;
				i_stream.close();
				return;
			}
			temp.write(buf, 0, n_bytes);
			n_written += n_bytes;
		}
	}

	/** Read one byte from the stream */
	public int read() throws IOException {
		checkClosed();
		setBitOffset(0);
		long pos = streamPos + 1;
		transferBlocks(pos);
		if(pos <= n_written) {
			temp.seek(streamPos);
			streamPos++;
			return temp.read();
		} else
			return -1;
	}

	/** Read data from the stream into a buffer */
	public int read(byte[] b, int off, int len) throws IOException {
		checkClosed();
		if(off < 0)
			throw new IndexOutOfBoundsException("off<0");
		if(len < 0)
			throw new IndexOutOfBoundsException("len<0");
		if(off + len > b.length)
			throw new IndexOutOfBoundsException("off+len>b.length");
		if(off + len < 0)
			throw new IndexOutOfBoundsException("off+len<0");
		if(len == 0)
			return 0;
		setBitOffset(0);
		transferBlocks(streamPos + len);
		int n_bytes = (int)Math.min(len, n_written - streamPos);
		if(n_bytes > 0) {
			temp.seek(streamPos);
			temp.readFully(b, off, n_bytes);
			streamPos += n_bytes;
			return n_bytes;
		} else
			return -1;
	}

	/** Flush data before the given position */
	public void flushBefore(long pos) {
		// Lease flushPos at 0 To allow reusing the cache file
	}

	/** Close the stream */
	public void close() {
		// ImageIO.read calls close, but we want to reuse the cache
		// file, so don't actually close the file (sneaky!)
	}

	/** Destroy the stream */
	public void destroy() throws IOException {
		is_destroyed = true;
		tempFile.delete();
		temp.close();
		if(!end_of_stream)
			i_stream.close();
	}

	/** Finalize the stream */
	protected void finalize() throws Throwable {
		if(!is_destroyed)
			destroy();
		super.finalize();
	}
}
