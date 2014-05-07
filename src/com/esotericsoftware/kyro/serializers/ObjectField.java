package com.esotericsoftware.kyro.serializers;

import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.KyroException;
import com.esotericsoftware.kyro.Registration;
import com.esotericsoftware.kyro.Serializer;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.serializers.FieldSerializer.CachedField;
import com.esotericsoftware.reflectasm.FieldAccess;

/*** Defer generation of serializers until it is really required at run-time. By default, use reflection-based approach. 
 * @author Nathan Sweet <misc@n4te.com>
 * @author Roman Levenstein <romixlev@gmail.com> */
class ObjectField extends CachedField {
	public Class[] generics;
	final FieldSerializer fieldSerializer;
	final Class type;
	final Kyro kyro;

	ObjectField (FieldSerializer fieldSerializer) {
		this.fieldSerializer = fieldSerializer;
		this.kyro = fieldSerializer.kyro;
		this.type = fieldSerializer.type;
	}

	public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
		return field.get(object);
	}

	public void setField (Object object, Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(object, value);
	}

	 public void write (Output output, Object object) {
		try {
			// if(typeVar2concreteClass != null) {
			// // Push a new scope for generics
			// kyro.pushGenericsScope(type, new Generics(typeVar2concreteClass));
			// }

			if (TRACE)
				trace("kyro", "Write field: " + this + " (" + object.getClass().getName() + ")" + " pos=" + output.position());

			Object value = getField(object);

			Serializer serializer = this.serializer;
			if (valueClass == null) {
				// The concrete type of the field is unknown, write the class first.
				if (value == null) {
					kyro.writeClass(output, null);
					return;
				}
				Registration registration = kyro.writeClass(output, value.getClass());
				if (serializer == null) serializer = registration.getSerializer();
				// if (generics != null)
				serializer.setGenerics(kyro, generics);
				kyro.writeObject(output, value, serializer);
			} else {
				// The concrete type of the field is known, always use the same serializer.
				if (serializer == null) this.serializer = serializer = kyro.getSerializer(valueClass);
				// if (generics != null)
				serializer.setGenerics(kyro, generics);
				if (canBeNull) {
					kyro.writeObjectOrNull(output, value, serializer);
				} else {
					if (value == null) {
						throw new KyroException("Field value is null but canBeNull is false: " + this + " (" + object.getClass().getName() + ")");
					}
					kyro.writeObject(output, value, serializer);
				}
			}
		} catch (IllegalAccessException ex) {
			throw new KyroException("Error accessing field: " + this + " (" + object.getClass().getName() + ")", ex);
		} catch (KyroException ex) {
			ex.addTrace(this + " (" + object.getClass().getName() + ")");
			throw ex;
		} catch (RuntimeException runtimeEx) {
			KyroException ex = new KyroException(runtimeEx);
			ex.addTrace(this + " (" + object.getClass().getName() + ")");
			throw ex;
		} finally {
			// if(typeVar2concreteClass != null)
			// kyro.popGenericsScope();
		}
	}

	public void read (Input input, Object object) {
		try {
			if (TRACE) trace("kyro", "Read field: " + this + " (" + type.getName() + ")" + " pos=" + input.position());
			Object value;

			Class concreteType = valueClass;
			Serializer serializer = this.serializer;
			if (concreteType == null) {
				Registration registration = kyro.readClass(input);
				if (registration == null)
					value = null;
				else {
					if (serializer == null) serializer = registration.getSerializer();
					// if (generics != null)
					serializer.setGenerics(kyro, generics);
					value = kyro.readObject(input, registration.getType(), serializer);
				}
			} else {
				if (serializer == null) this.serializer = serializer = kyro.getSerializer(valueClass);
				// if (generics != null)
				serializer.setGenerics(kyro, generics);
				if (canBeNull)
					value = kyro.readObjectOrNull(input, concreteType, serializer);
				else
					value = kyro.readObject(input, concreteType, serializer);
			}

			setField(object, value);
		} catch (IllegalAccessException ex) {
			throw new KyroException("Error accessing field: " + this + " (" + type.getName() + ")", ex);
		} catch (KyroException ex) {
			ex.addTrace(this + " (" + type.getName() + ")");
			throw ex;
		} catch (RuntimeException runtimeEx) {
			KyroException ex = new KyroException(runtimeEx);
			ex.addTrace(this + " (" + type.getName() + ")");
			throw ex;
		} finally {
			// if(typeVar2concreteClass != null)
			// kyro.popGenericsScope();
		}
	}

	public void copy (Object original, Object copy) {
		try {
			if (accessIndex != -1) {
				FieldAccess access = (FieldAccess)fieldSerializer.access;
				access.set(copy, accessIndex, kyro.copy(access.get(original, accessIndex)));
			} else
				setField(copy, kyro.copy(getField(original)));
		} catch (IllegalAccessException ex) {
			throw new KyroException("Error accessing field: " + this + " (" + type.getName() + ")", ex);
		} catch (KyroException ex) {
			ex.addTrace(this + " (" + type.getName() + ")");
			throw ex;
		} catch (RuntimeException runtimeEx) {
			KyroException ex = new KyroException(runtimeEx);
			ex.addTrace(this + " (" + type.getName() + ")");
			throw ex;
		}
	}

	final static class ObjectIntField extends ObjectField {
		public ObjectIntField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getInt(object);
		}

		public void write (Output output, Object object) {
			try {
				if (varIntsEnabled)
					output.writeInt(field.getInt(object), false);
				else
					output.writeInt(field.getInt(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				if (varIntsEnabled)
					field.setInt(object, input.readInt(false));
				else
					field.setInt(object, input.readInt());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setInt(copy, field.getInt(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectFloatField extends ObjectField {
		public ObjectFloatField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getFloat(object);
		}

		public void write (Output output, Object object) {
			try {
				output.writeFloat(field.getFloat(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				field.setFloat(object, input.readFloat());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setFloat(copy, field.getFloat(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectShortField extends ObjectField {
		public ObjectShortField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getShort(object);
		}

		public void write (Output output, Object object) {
			try {
				output.writeShort(field.getShort(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				field.setShort(object, input.readShort());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setShort(copy, field.getShort(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectByteField extends ObjectField {
		public ObjectByteField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getByte(object);
		}

		public void write (Output output, Object object) {
			try {
				output.writeByte(field.getByte(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				field.setByte(object, input.readByte());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setByte(copy, field.getByte(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectBooleanField extends ObjectField {
		public ObjectBooleanField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getBoolean(object);
		}

		public void write (Output output, Object object) {
			try {
				output.writeBoolean(field.getBoolean(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				field.setBoolean(object, input.readBoolean());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setBoolean(copy, field.getBoolean(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectCharField extends ObjectField {
		public ObjectCharField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getChar(object);
		}

		public void write (Output output, Object object) {
			try {
				output.writeChar(field.getChar(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				field.setChar(object, input.readChar());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setChar(copy, field.getChar(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectLongField extends ObjectField {
		public ObjectLongField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getLong(object);
		}

		public void write (Output output, Object object) {
			try {
				if (varIntsEnabled)
					output.writeLong(field.getLong(object), false);
				else
					output.writeLong(field.getLong(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				if (varIntsEnabled)
					field.setLong(object, input.readLong(false));
				else
					field.setLong(object, input.readLong());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setLong(copy, field.getLong(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}

	final static class ObjectDoubleField extends ObjectField {
		public ObjectDoubleField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			return field.getDouble(object);
		}

		public void write (Output output, Object object) {
			try {
				output.writeDouble(field.getDouble(object));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void read (Input input, Object object) {
			try {
				field.setDouble(object, input.readDouble());
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}

		public void copy (Object original, Object copy) {
			try {
				field.setDouble(copy, field.getDouble(original));
			} catch (Exception e) {
				KyroException ex = new KyroException(e);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}
}

