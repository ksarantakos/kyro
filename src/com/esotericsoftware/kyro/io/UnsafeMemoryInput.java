
package com.esotericsoftware.kyro.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import sun.nio.ch.DirectBuffer;

import com.esotericsoftware.kyro.KyroException;

import static com.esotericsoftware.kyro.util.UnsafeUtil.*;

/** An optimized InputStream that reads data directly from the off-heap memory. Utility methods are provided for efficiently
 * reading primitive types, arrays of primitive types and strings. It uses @link{sun.misc.Unsafe} to achieve a very good
 * performance.
 * 
 * <p>
 * Important notes:<br/>
 * <li>Bulk operations, e.g. on arrays of primitive types, are always using native byte order.</li>
 * <li>Fixed-size int, long, short, float and double elements are always read using native byte order.</li>
 * <li>Best performance is achieved if no variable length encoding for integers is used.</li>
 * <li>Serialized representation used as input for this class should always be produced using @link{UnsafeMemoryOutput}</li>
 * </p>
 * @author Roman Levenstein <romixlev@gmail.com> */
public final class UnsafeMemoryInput extends ByteBufferInput {
	/** Start address of the memory buffer The memory buffer should be non-movable, which normally means that is is allocated
	 * off-heap */
	private long bufaddress;

	{
		varIntsEnabled = false;

	}

	/** Creates an uninitialized Input. {@link #setBuffer(byte[], int, int)} must be called before the Input is used. */
	public UnsafeMemoryInput () {
	}

	/** Creates a new Input for reading from a byte array.
	 * @param bufferSize The initial and maximum size of the buffer. An exception is thrown if this size is exceeded. */
	public UnsafeMemoryInput (int bufferSize) {
		super(bufferSize);
		updateBufferAddress();
	}

	/** Creates a new Input for reading from a byte array.
	 * @see #setBuffer(byte[]) */
	public UnsafeMemoryInput (byte[] buffer) {
		super(buffer);
		updateBufferAddress();
	}

	public UnsafeMemoryInput (ByteBuffer buffer) {
		super(buffer);
		updateBufferAddress();
	}

	public UnsafeMemoryInput (long address, int maxBufferSize) {
		super(address, maxBufferSize);
		updateBufferAddress();
	}

	/** Creates a new Input for reading from an InputStream. A buffer size of 4096 is used. */
	public UnsafeMemoryInput (InputStream inputStream) {
		super(inputStream);
		updateBufferAddress();
	}

	/** Creates a new Input for reading from an InputStream. */
	public UnsafeMemoryInput (InputStream inputStream, int bufferSize) {
		super(inputStream, bufferSize);
		updateBufferAddress();
	}

	public void setBuffer (ByteBuffer buffer) {
		super.setBuffer(buffer);
		updateBufferAddress();
	}

	private void updateBufferAddress () {
		bufaddress = ((DirectBuffer)super.niobuffer).address();
	}

	/** Reads a 4 byte int. */
	public int readInt () throws KyroException {
		require(4);
		int result = unsafe().getInt(bufaddress + position);
		position += 4;
		return result;
	}

	/** Reads a 4 byte float. */
	public float readFloat () throws KyroException {
		require(4);
		float result = unsafe().getFloat(bufaddress + position);
		position += 4;
		return result;
	}

	/** Reads a 2 byte short. */
	public short readShort () throws KyroException {
		require(2);
		short result = unsafe().getShort(bufaddress + position);
		position += 2;
		return result;
	}

	/** Reads an 8 byte long. */
	public long readLong () throws KyroException {
		require(8);
		long result = unsafe().getLong(bufaddress + position);
		position += 8;
		return result;
	}

	/** Reads a 1 byte boolean. */
	public boolean readBoolean () throws KyroException {
		super.niobuffer.position(position);
		return super.readBoolean();
	}

	/** Reads a single byte. */
	public byte readByte () throws KyroException {
		super.niobuffer.position(position);
		return super.readByte();
	}

	/** Reads a 2 byte char. */
	public char readChar () throws KyroException {
		super.niobuffer.position(position);
		return super.readChar();
	}

	/** Reads an 8 byte double. */
	public double readDouble () throws KyroException {
		require(8);
		double result = unsafe().getDouble(bufaddress + position);
		position += 8;
		return result;
	}

	public int readInt (boolean optimizePositive) throws KyroException {
		if (!varIntsEnabled)
			return readInt();
		else
			return super.readInt(optimizePositive);
	}

	public long readLong (boolean optimizePositive) throws KyroException {
		if (!varIntsEnabled)
			return readLong();
		else
			return super.readLong(optimizePositive);
	}

	// Methods implementing bulk operations on arrays of primitive types

	/** {@inheritDoc} */
	final public int[] readInts (int length, boolean optimizePositive) throws KyroException {
		if (!varIntsEnabled) {
			int bytesToCopy = length << 2;
			int[] array = new int[length];
			readBytes(array, intArrayBaseOffset, 0, bytesToCopy);
			return array;
		} else
			return super.readInts(length, optimizePositive);
	}

	/** {@inheritDoc} */
	final public long[] readLongs (int length, boolean optimizePositive) throws KyroException {
		if (!varIntsEnabled) {
			int bytesToCopy = length << 3;
			long[] array = new long[length];
			readBytes(array, longArrayBaseOffset, 0, bytesToCopy);
			return array;
		} else
			return super.readLongs(length, optimizePositive);
	}

	/** {@inheritDoc} */
	final public float[] readFloats (int length) throws KyroException {
		int bytesToCopy = length << 2;
		float[] array = new float[length];
		readBytes(array, floatArrayBaseOffset, 0, bytesToCopy);
		return array;
	}

	/** {@inheritDoc} */
	final public short[] readShorts (int length) throws KyroException {
		int bytesToCopy = length << 1;
		short[] array = new short[length];
		readBytes(array, shortArrayBaseOffset, 0, bytesToCopy);
		return array;
	}

	/** {@inheritDoc} */
	final public char[] readChars (int length) throws KyroException {
		int bytesToCopy = length << 1;
		char[] array = new char[length];
		readBytes(array, charArrayBaseOffset, 0, bytesToCopy);
		return array;
	}

	/** {@inheritDoc} */
	final public double[] readDoubles (int length) throws KyroException {
		int bytesToCopy = length << 3;
		double[] array = new double[length];
		readBytes(array, doubleArrayBaseOffset, 0, bytesToCopy);
		return array;
	}

	final public void readBytes (Object dstObj, long offset, long count) throws KyroException {
		/* Unsafe supports efficient bulk reading into arrays of primitives only because of JVM limitations due to GC */
		if (dstObj.getClass().isArray())
			readBytes(dstObj, 0, offset, (int)count);
		else {
			throw new KyroException("Only bulk reads of arrays is supported");
		}
	}

	final private void readBytes (Object dstObj, long dstArrayTypeOffset, long offset, int count) throws KyroException {
		int copyCount = Math.min(limit - position, count);
		while (true) {
			unsafe().copyMemory(null, bufaddress + position, dstObj, dstArrayTypeOffset + offset, copyCount);
			position += copyCount;
			count -= copyCount;
			if (count == 0) break;
			offset += copyCount;
			copyCount = Math.min(count, capacity);
			require(copyCount);
		}
	}
}
