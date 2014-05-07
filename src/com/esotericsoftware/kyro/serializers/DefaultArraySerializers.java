
package com.esotericsoftware.kyro.serializers;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

import com.esotericsoftware.kyro.Generics;
import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.Registration;
import com.esotericsoftware.kyro.Serializer;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;

import static com.esotericsoftware.kyro.Kyro.*;
import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/** Contains many serializer classes for specific array types that are provided by {@link Kyro#addDefaultSerializer(Class, Class)
 * default}.
 * @author Nathan Sweet <misc@n4te.com> */
public class DefaultArraySerializers{
	static public class ByteArraySerializer extends Serializer<byte[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, byte[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeBytes(object);
		}

		public byte[] read (Kyro kyro, Input input, Class<byte[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readBytes(length - 1);
		}

		public byte[] copy (Kyro kyro, byte[] original) {
			byte[] copy = new byte[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class IntArraySerializer extends Serializer<int[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, int[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeInts(object, false);
		}

		public int[] read (Kyro kyro, Input input, Class<int[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readInts(length - 1, false);
		}

		public int[] copy (Kyro kyro, int[] original) {
			int[] copy = new int[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class FloatArraySerializer extends Serializer<float[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, float[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeFloats(object);
		}

		public float[] read (Kyro kyro, Input input, Class<float[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readFloats(length-1);
		}

		public float[] copy (Kyro kyro, float[] original) {
			float[] copy = new float[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class LongArraySerializer extends Serializer<long[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, long[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeLongs(object, false);
		}

		public long[] read (Kyro kyro, Input input, Class<long[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readLongs(length-1, false);
		}

		public long[] copy (Kyro kyro, long[] original) {
			long[] copy = new long[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class ShortArraySerializer extends Serializer<short[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, short[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeShorts(object);
		}

		public short[] read (Kyro kyro, Input input, Class<short[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readShorts(length-1);
		}

		public short[] copy (Kyro kyro, short[] original) {
			short[] copy = new short[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class CharArraySerializer extends Serializer<char[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, char[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeChars(object);
		}

		public char[] read (Kyro kyro, Input input, Class<char[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readChars(length-1);
		}

		public char[] copy (Kyro kyro, char[] original) {
			char[] copy = new char[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class DoubleArraySerializer extends Serializer<double[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, double[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			output.writeDoubles(object);
		}

		public double[] read (Kyro kyro, Input input, Class<double[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			return input.readDoubles(length-1);
		}

		public double[] copy (Kyro kyro, double[] original) {
			double[] copy = new double[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class BooleanArraySerializer extends Serializer<boolean[]> {
		{
			setAcceptsNull(true);
		}

		public void write (Kyro kyro, Output output, boolean[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			for (int i = 0, n = object.length; i < n; i++)
				output.writeBoolean(object[i]);
		}

		public boolean[] read (Kyro kyro, Input input, Class<boolean[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			boolean[] array = new boolean[--length];
			for (int i = 0; i < length; i++)
				array[i] = input.readBoolean();
			return array;
		}

		public boolean[] copy (Kyro kyro, boolean[] original) {
			boolean[] copy = new boolean[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class StringArraySerializer extends Serializer<String[]> {
		{
			setAcceptsNull(true);
		}
		
		public void write (Kyro kyro, Output output, String[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			if (kyro.getReferences() && kyro.getReferenceResolver().useReferences(String.class)) {
				Serializer serializer = kyro.getSerializer(String.class);
				for (int i = 0, n = object.length; i < n; i++)
					kyro.writeObjectOrNull(output, object[i], serializer);
			} else {
				for (int i = 0, n = object.length; i < n; i++)
					output.writeString(object[i]);
			}
		}

		public String[] read (Kyro kyro, Input input, Class<String[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			String[] array = new String[--length];
			if (kyro.getReferences() && kyro.getReferenceResolver().useReferences(String.class)) {
				Serializer serializer = kyro.getSerializer(String.class);
				for (int i = 0; i < length; i++) {
					array[i] = kyro.readObjectOrNull(input, String.class, serializer);
				}
			} else {
				for (int i = 0; i < length; i++)
					array[i] = input.readString();
			}
			return array;
		}

		public String[] copy (Kyro kyro, String[] original) {
			String[] copy = new String[original.length];
			System.arraycopy(original, 0, copy, 0, copy.length);
			return copy;
		}
	}

	static public class ObjectArraySerializer extends Serializer<Object[]> {
		private boolean elementsAreSameType;
		private boolean elementsCanBeNull = true;
		private Class[] generics;
		private final Class type;

		{
			setAcceptsNull(true);
		}
		
		public ObjectArraySerializer(Kyro kyro, Class type) {
			this.type = type;
			Class componentType = type.getComponentType();
			boolean isFinal = 0!=(componentType.getModifiers() & Modifier.FINAL);
			if(isFinal)
				setElementsAreSameType(true);
		}

		public void write (Kyro kyro, Output output, Object[] object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.length + 1, true);
			Class elementClass = object.getClass().getComponentType();
			if (elementsAreSameType || Modifier.isFinal(elementClass.getModifiers())) {
				Serializer elementSerializer = kyro.getSerializer(elementClass);
//				if(generics!=null) 
					elementSerializer.setGenerics(kyro, generics);
				for (int i = 0, n = object.length; i < n; i++) {
					if (elementsCanBeNull)
						kyro.writeObjectOrNull(output, object[i], elementSerializer);
					else
						kyro.writeObject(output, object[i], elementSerializer);
				}
			} else {
//				Generics genericsScope = null;
//				Class componentType = type;
//				while(componentType.getComponentType() != null) {
//					componentType = componentType.getComponentType();
//				}
//				TypeVariable[] typeVars = type.getComponentType().getTypeParameters();
//				if(typeVars != null && generics != null) {
//					if(TRACE) trace("kyro", "Creating a new GenericsScope for " + type.getName() + " with type vars: " + Arrays.toString(typeVars));
//					genericsScope = new Generics();
//					int i = 0;
//					for(TypeVariable typeVar: typeVars) {
//						genericsScope.add(typeVar.getName(), generics[i]);
//						i++;
//					}
//					kyro.pushGenericsScope(type, genericsScope);
//				}
//				
				for (int i = 0, n = object.length; i < n; i++) {
					// Propagate generics?
					if (object[i] != null) {
						Serializer serializer = kyro.getSerializer(object[i].getClass());
						serializer.setGenerics(kyro, generics);
					}
					kyro.writeClassAndObject(output, object[i]);
				}
				
//				if(genericsScope != null)
//					kyro.popGenericsScope();
			}
		}

		public Object[] read (Kyro kyro, Input input, Class<Object[]> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			Object[] object = (Object[])Array.newInstance(type.getComponentType(), length - 1);
			kyro.reference(object);
			Class elementClass = object.getClass().getComponentType();
			if (elementsAreSameType || Modifier.isFinal(elementClass.getModifiers())) {
				Serializer elementSerializer = kyro.getSerializer(elementClass);
//				if(generics!=null) 
					elementSerializer.setGenerics(kyro, generics);
				for (int i = 0, n = object.length; i < n; i++) {
					if (elementsCanBeNull)
						object[i] = kyro.readObjectOrNull(input, elementClass, elementSerializer);
					else
						object[i] = kyro.readObject(input, elementClass, elementSerializer);
				}
			} else {
				for (int i = 0, n = object.length; i < n; i++) {
					// Propagate generics
					Registration registration = kyro.readClass(input);
					if (registration != null) {
						registration.getSerializer().setGenerics(kyro, generics);
						object[i] = kyro.readObject(input, registration.getType(), registration.getSerializer());
					} else {
						object[i] = null;
					}
				}
			}
			return object;
		}

		public Object[] copy (Kyro kyro, Object[] original) {
			Object[] copy = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length);
			for (int i = 0, n = original.length; i < n; i++)
				copy[i] = kyro.copy(original[i]);
			return copy;
		}

		/** @param elementsCanBeNull False if all elements are not null. This saves 1 byte per element if the array type is final or
		 *           elementsAreSameClassAsType is true. True if it is not known (default). */
		public void setElementsCanBeNull (boolean elementsCanBeNull) {
			this.elementsCanBeNull = elementsCanBeNull;
		}

		/** @param elementsAreSameType True if all elements are the same type as the array (ie they don't extend the array type). This
		 *           saves 1 byte per element if the array type is not final. Set to false if the array type is final or elements
		 *           extend the array type (default). */
		public void setElementsAreSameType (boolean elementsAreSameType) {
			this.elementsAreSameType = elementsAreSameType;
		}
		
		public void setGenerics(Kyro kyro, Class[] generics) {
			if(TRACE) trace("kyro", "setting generics for ObjectArraySerializer");
			this.generics = generics;
		}
	}
}
