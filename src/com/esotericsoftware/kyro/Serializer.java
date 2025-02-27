
package com.esotericsoftware.kyro;

import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;

/** Reads and writes objects to and from bytes.
 * @author Nathan Sweet <misc@n4te.com> */
public abstract class Serializer<T> {
	private boolean acceptsNull, immutable;

	public Serializer () {
	}

	/** @see #setAcceptsNull(boolean) */
	public Serializer (boolean acceptsNull) {
		this.acceptsNull = acceptsNull;
	}

	/** @see #setAcceptsNull(boolean)
	 * @see #setImmutable(boolean) */
	public Serializer (boolean acceptsNull, boolean immutable) {
		this.acceptsNull = acceptsNull;
		this.immutable = immutable;
	}

	/** Writes the bytes for the object to the output.
	 * <p>
	 * This method should not be called directly, instead this serializer can be passed to {@link Kyro} write methods that accept a
	 * serialier.
	 * @param object May be null if {@link #getAcceptsNull()} is true. */
	abstract public void write (Kyro kyro, Output output, T object);

	/** Reads bytes and returns a new object of the specified concrete type.
	 * <p>
	 * Before Kyro can be used to read child objects, {@link Kyro#reference(Object)} must be called with the parent object to
	 * ensure it can be referenced by the child objects. Any serializer that uses {@link Kyro} to read a child object may need to
	 * be reentrant.
	 * <p>
	 * This method should not be called directly, instead this serializer can be passed to {@link Kyro} read methods that accept a
	 * serialier.
	 * @return May be null if {@link #getAcceptsNull()} is true. */
	abstract public T read (Kyro kyro, Input input, Class<T> type);

	public boolean getAcceptsNull () {
		return acceptsNull;
	}

	/** If true, this serializer will handle writing and reading null values. If false, the Kyro framework handles null values and
	 * the serializer will never receive null.
	 * <p>
	 * This can be set to true on a serializer that does not accept nulls if it is known that the serializer will never encounter
	 * null. Doing this will prevent the framework from writing a byte to denote null. */
	public void setAcceptsNull (boolean acceptsNull) {
		this.acceptsNull = acceptsNull;
	}

	public boolean isImmutable () {
		return immutable;
	}

	/** If true, the type this serializer will be used for is considered immutable. This causes {@link #copy(Kyro, Object)} to
	 * return the original object. */
	public void setImmutable (boolean immutable) {
		this.immutable = immutable;
	}
	
	/** Sets the generic types of the field or method this serializer will be used for on the next call to read or write. Subsequent
	 * calls to read and write must not use this generic type information. The default implementation does nothing. Subclasses may
	 * use the information provided to this method for more efficient serialization, eg to use the same type for all items in a
	 * list.
	 * @param generics Some (but never all) elements may be null if there is no generic type information at that index. */
	public void setGenerics (Kyro kyro, Class[] generics) {
	}

	/** Returns a copy of the specified object. The default implementation returns the original if {@link #isImmutable()} is true,
	 * else throws {@link KyroException}. Subclasses should override this method if needed to support {@link Kyro#copy(Object)}.
	 * <p>
	 * Before Kyro can be used to copy child objects, {@link Kyro#reference(Object)} must be called with the copy to ensure it can
	 * be referenced by the child objects. Any serializer that uses {@link Kyro} to copy a child object may need to be reentrant.
	 * <p>
	 * This method should not be called directly, instead this serializer can be passed to {@link Kyro} copy methods that accept a
	 * serialier. */
	public T copy (Kyro kyro, T original) {
		if (immutable) return original;
		throw new KyroException("Serializer does not support copy: " + getClass().getName());
	}
}
