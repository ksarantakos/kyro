package com.esotericsoftware.kyro.serializers;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.KyroException;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.serializers.FieldSerializer.CachedField;
import com.esotericsoftware.reflectasm.FieldAccess;

/*** Implementations of ASM-based serializers for fields.
 * 
 * @author Nathan Sweet <misc@n4te.com> */
class AsmCacheFields {

	abstract static class AsmCachedField extends CachedField {
	}

	final static class AsmIntField extends AsmCachedField {
		public void write (Output output, Object object) {
			if (varIntsEnabled)
				output.writeInt(access.getInt(object, accessIndex), false);
			else
				output.writeInt(access.getInt(object, accessIndex));
		}

		public void read (Input input, Object object) {
			if (varIntsEnabled)
				access.setInt(object, accessIndex, input.readInt(false));
			else
				access.setInt(object, accessIndex, input.readInt());
		}

		public void copy (Object original, Object copy) {
			access.setInt(copy, accessIndex, access.getInt(original, accessIndex));
		}
	}

	final static class AsmFloatField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeFloat(access.getFloat(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.setFloat(object, accessIndex, input.readFloat());
		}

		public void copy (Object original, Object copy) {
			access.setFloat(copy, accessIndex, access.getFloat(original, accessIndex));
		}
	}

	final static class AsmShortField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeShort(access.getShort(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.setShort(object, accessIndex, input.readShort());
		}

		public void copy (Object original, Object copy) {
			access.setShort(copy, accessIndex, access.getShort(original, accessIndex));
		}
	}

	final static class AsmByteField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeByte(access.getByte(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.setByte(object, accessIndex, input.readByte());
		}

		public void copy (Object original, Object copy) {
			access.setByte(copy, accessIndex, access.getByte(original, accessIndex));
		}
	}

	final static class AsmBooleanField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeBoolean(access.getBoolean(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.setBoolean(object, accessIndex, input.readBoolean());
		}

		public void copy (Object original, Object copy) {
			access.setBoolean(copy, accessIndex, access.getBoolean(original, accessIndex));
		}
	}

	final static class AsmCharField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeChar(access.getChar(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.setChar(object, accessIndex, input.readChar());
		}

		public void copy (Object original, Object copy) {
			access.setChar(copy, accessIndex, access.getChar(original, accessIndex));
		}
	}

	final static class AsmLongField extends AsmCachedField {
		public void write (Output output, Object object) {
			if (varIntsEnabled)
				output.writeLong(access.getLong(object, accessIndex), false);
			else
				output.writeLong(access.getLong(object, accessIndex));
		}

		public void read (Input input, Object object) {
			if (varIntsEnabled)
				access.setLong(object, accessIndex, input.readLong(false));
			else
				access.setLong(object, accessIndex, input.readLong());
		}

		public void copy (Object original, Object copy) {
			access.setLong(copy, accessIndex, access.getLong(original, accessIndex));
		}
	}

	final static class AsmDoubleField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeDouble(access.getDouble(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.setDouble(object, accessIndex, input.readDouble());
		}

		public void copy (Object original, Object copy) {
			access.setDouble(copy, accessIndex, access.getDouble(original, accessIndex));
		}
	}

	final static class AsmStringField extends AsmCachedField {
		public void write (Output output, Object object) {
			output.writeString(access.getString(object, accessIndex));
		}

		public void read (Input input, Object object) {
			access.set(object, accessIndex, input.readString());
		}

		public void copy (Object original, Object copy) {
			access.set(copy, accessIndex, access.getString(original, accessIndex));
		}
	}

	final static class AsmObjectField extends ObjectField {

		public AsmObjectField (FieldSerializer fieldSerializer) {
			super(fieldSerializer);
		}

		public Object getField (Object object) throws IllegalArgumentException, IllegalAccessException {
			if (accessIndex != -1) return ((FieldAccess)access).get(object, accessIndex);
			throw new KyroException("Unknown acess index");
		}

		public void setField (Object object, Object value) throws IllegalArgumentException, IllegalAccessException {
			if (accessIndex != -1)
				((FieldAccess)access).set(object, accessIndex, value);
			else
				throw new KyroException("Unknown acess index");
		}

		public void copy (Object original, Object copy) {
			try {
				if (accessIndex != -1) {
					access.set(copy, accessIndex, kyro.copy(access.get(original, accessIndex)));
				} else
					throw new KyroException("Unknown acess index");
			} catch (KyroException ex) {
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			} catch (RuntimeException runtimeEx) {
				KyroException ex = new KyroException(runtimeEx);
				ex.addTrace(this + " (" + type.getName() + ")");
				throw ex;
			}
		}
	}
}
