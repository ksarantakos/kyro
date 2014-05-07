
package com.esotericsoftware.kyro.io;

import java.io.IOException;
import java.io.OutputStream;

import com.esotericsoftware.kyro.KyroException;

import static com.esotericsoftware.minlog.Log.*;

/** An OutputStream that buffers data in a byte array and flushes to another OutputStream, writing the length before each flush.
 * The length allows the chunks to be skipped when reading.
 * @author Nathan Sweet <misc@n4te.com> */
public class OutputChunked extends Output {
	/** Creates an uninitialized OutputChunked with a maximum chunk size of 2048. The OutputStream must be set before it can be
	 * used. */
	public OutputChunked () {
		super(2048);
	}

	/** Creates an uninitialized OutputChunked. The OutputStream must be set before it can be used.
	 * @param bufferSize The maximum size of a chunk. */
	public OutputChunked (int bufferSize) {
		super(bufferSize);
	}

	/** Creates an OutputChunked with a maximum chunk size of 2048. */
	public OutputChunked (OutputStream outputStream) {
		super(outputStream, 2048);
	}

	/** @param bufferSize The maximum size of a chunk. */
	public OutputChunked (OutputStream outputStream, int bufferSize) {
		super(outputStream, bufferSize);
	}

	public void flush () throws KyroException {
		if (position() > 0) {
			try {
				writeChunkSize();
			} catch (IOException ex) {
				throw new KyroException(ex);
			}
		}
		super.flush();
	}

	private void writeChunkSize () throws IOException {
		int size = position();
		if (TRACE) trace("kyro", "Write chunk: " + size);
		OutputStream outputStream = getOutputStream();
		if ((size & ~0x7F) == 0) {
			outputStream.write(size);
			return;
		}
		outputStream.write((size & 0x7F) | 0x80);
		size >>>= 7;
		if ((size & ~0x7F) == 0) {
			outputStream.write(size);
			return;
		}
		outputStream.write((size & 0x7F) | 0x80);
		size >>>= 7;
		if ((size & ~0x7F) == 0) {
			outputStream.write(size);
			return;
		}
		outputStream.write((size & 0x7F) | 0x80);
		size >>>= 7;
		if ((size & ~0x7F) == 0) {
			outputStream.write(size);
			return;
		}
		outputStream.write((size & 0x7F) | 0x80);
		size >>>= 7;
		outputStream.write(size);
	}

	/** Marks the end of some data that may have been written by any number of chunks. These chunks can then be skipped when
	 * reading. */
	public void endChunks () {
		flush(); // Flush any partial chunk.
		if (TRACE) trace("kyro", "End chunks.");
		try {
			getOutputStream().write(0); // Zero length chunk.
		} catch (IOException ex) {
			throw new KyroException(ex);
		}
	}
}
