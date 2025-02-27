package com.esotericsoftware.kyro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Assert;

import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.Serializer;
import com.esotericsoftware.kyro.io.ByteBufferInput;
import com.esotericsoftware.kyro.io.ByteBufferOutput;
import com.esotericsoftware.kyro.io.FastInput;
import com.esotericsoftware.kyro.io.FastOutput;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.io.UnsafeInput;
import com.esotericsoftware.kyro.io.UnsafeMemoryInput;
import com.esotericsoftware.kyro.io.UnsafeMemoryOutput;
import com.esotericsoftware.kyro.io.UnsafeOutput;
import com.esotericsoftware.minlog.Log;

/**
 * Convenience methods for round tripping objects.
 * 
 * @author Nathan Sweet <misc@n4te.com>
 */
abstract public class KryoTestCase extends TestCase {
	protected Kyro kyro;
	protected Output output;
	protected Input input;
	protected Object object1, object2;
	protected boolean supportsCopy;

	static interface StreamFactory {
		public Output createOutput(OutputStream os);

		public Output createOutput(OutputStream os, int size);

		public Output createOutput(int size, int limit);

		public Input createInput(InputStream os, int size);

		public Input createInput(byte[] buffer);
	}

	protected void setUp() throws Exception {
		// Log.TRACE();

		kyro = new Kyro();
		kyro.setReferences(false);
		kyro.setRegistrationRequired(true);
		// kyro.useAsmBackend(false);
	}

	public <T> T roundTrip(int length, int unsafeLength, T object1) {
		
		roundTripWithStreamFactory(unsafeLength, object1, new StreamFactory() {
			public Output createOutput(OutputStream os) {
				return new UnsafeMemoryOutput(os);
			}

			public Output createOutput(OutputStream os, int size) {
				return new UnsafeMemoryOutput(os, size);
			}

			public Output createOutput(int size, int limit) {
				return new UnsafeMemoryOutput(size, limit);
			}

			public Input createInput(InputStream os, int size) {
				return new UnsafeMemoryInput(os, size);
			}

			public Input createInput(byte[] buffer) {
				return new UnsafeMemoryInput(buffer);
			}
		});

		roundTripWithStreamFactory(unsafeLength, object1, new StreamFactory() {
			public Output createOutput(OutputStream os) {
				return new UnsafeOutput(os);
			}

			public Output createOutput(OutputStream os, int size) {
				return new UnsafeOutput(os, size);
			}

			public Output createOutput(int size, int limit) {
				return new UnsafeOutput(size, limit);
			}

			public Input createInput(InputStream os, int size) {
				return new UnsafeInput(os, size);
			}

			public Input createInput(byte[] buffer) {
				return new UnsafeInput(buffer);
			}
		});
		
		roundTripWithStreamFactory(length, object1, new StreamFactory() {
			public Output createOutput(OutputStream os) {
				return new ByteBufferOutput(os);
			}

			public Output createOutput(OutputStream os, int size) {
				return new ByteBufferOutput(os, size);
			}

			public Output createOutput(int size, int limit) {
				return new ByteBufferOutput(size, limit);
			}

			public Input createInput(InputStream os, int size) {
				return new ByteBufferInput(os, size);
			}

			public Input createInput(byte[] buffer) {
				return new ByteBufferInput(buffer);
			}
		});

		roundTripWithStreamFactory(unsafeLength, object1, new StreamFactory() {
			public Output createOutput(OutputStream os) {
				return new FastOutput(os);
			}

			public Output createOutput(OutputStream os, int size) {
				return new FastOutput(os, size);
			}

			public Output createOutput(int size, int limit) {
				return new FastOutput(size, limit);
			}

			public Input createInput(InputStream os, int size) {
				return new FastInput(os, size);
			}

			public Input createInput(byte[] buffer) {
				return new FastInput(buffer);
			}
		});
		
		return roundTripWithStreamFactory(length, object1, new StreamFactory() {
			public Output createOutput(OutputStream os) {
				return new Output(os);
			}

			public Output createOutput(OutputStream os, int size) {
				return new Output(os, size);
			}

			public Output createOutput(int size, int limit) {
				return new Output(size, limit);
			}

			public Input createInput(InputStream os, int size) {
				return new Input(os, size);
			}

			public Input createInput(byte[] buffer) {
				return new Input(buffer);
			}
		});
	}

	public <T> T roundTripWithStreamFactory(int length, T object1,
			StreamFactory sf) {
		this.object1 = object1;

		// Test output to stream, large buffer.
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		output = sf.createOutput(outStream, 4096);
		kyro.writeClassAndObject(output, object1);
		output.flush();

		// Test input from stream, large buffer.
		byte[] out = outStream.toByteArray();
		input = sf.createInput(
				new ByteArrayInputStream(outStream.toByteArray()), 4096);
		object2 = kyro.readClassAndObject(input);
		assertEquals("Incorrect number of bytes read.", length, input.total());
		assertEquals("Incorrect number of bytes written.", length, output.total());
		assertEquals(object1, object2);

		// Test output to stream, small buffer.
		outStream = new ByteArrayOutputStream();
		output = sf.createOutput(outStream, 10);
		kyro.writeClassAndObject(output, object1);
		output.flush();

		// Test input from stream, small buffer.
		input = sf.createInput(
				new ByteArrayInputStream(outStream.toByteArray()), 10);
		object2 = kyro.readClassAndObject(input);
		assertEquals("Incorrect number of bytes read.", length, input.total());
		assertEquals(object1, object2);

		if (object1 != null) {
			// Test null with serializer.
			Serializer serializer = kyro.getRegistration(object1.getClass())
					.getSerializer();
			output.clear();
			outStream.reset();
			kyro.writeObjectOrNull(output, null, serializer);
			output.flush();

			// Test null from byte array with and without serializer.
			input = sf.createInput(
					new ByteArrayInputStream(outStream.toByteArray()), 10);
			assertEquals(null, kyro.readObjectOrNull(input, object1.getClass(),
					serializer));

			input = sf.createInput(
					new ByteArrayInputStream(outStream.toByteArray()), 10);
			assertEquals(null, kyro.readObjectOrNull(input, object1.getClass()));
		}

		// Test output to byte array.
		output = sf.createOutput(length * 2, -1);
		kyro.writeClassAndObject(output, object1);
		output.flush();

		// Test input from byte array.
		input = sf.createInput(output.toBytes());
		object2 = kyro.readClassAndObject(input);
		assertEquals(object1, object2);
		assertEquals("Incorrect length.", length, output.total());
		assertEquals("Incorrect number of bytes read.", length, input.total());
		input.rewind();

		if (supportsCopy) {
			// Test copy.
			T copy = kyro.copy(object1);
			assertEquals(object1, copy);
			copy = kyro.copyShallow(object1);
			assertEquals(object1, copy);
		}

		return (T) object2;
	}

	static public void assertEquals(Object object1, Object object2) {
		Assert.assertEquals(arrayToList(object1), arrayToList(object2));
	}

	static public Object arrayToList(Object array) {
		if (array == null || !array.getClass().isArray())
			return array;
		ArrayList list = new ArrayList(Array.getLength(array));
		for (int i = 0, n = Array.getLength(array); i < n; i++)
			list.add(arrayToList(Array.get(array, i)));
		return list;
	}

	static public ArrayList list(Object... items) {
		ArrayList list = new ArrayList();
		for (Object item : items)
			list.add(item);
		return list;
	}
}
