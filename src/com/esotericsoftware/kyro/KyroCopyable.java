
package com.esotericsoftware.kyro;

/** Allows implementing classes to perform their own copying. Hand written copying can be more efficient in some cases.
 * <p>
 * This method is used instead of the registered serializer {@link Serializer#copy(Kyro, Object)} method.
 * @author Nathan Sweet <misc@n4te.com> */
public interface KyroCopyable<T> {
	/** Returns a copy that has the same values as this object. Before Kyro can be used to copy child objects,
	 * {@link Kyro#reference(Object)} must be called with the copy to ensure it can be referenced by the child objects.
	 * @see Serializer#copy(Kyro, Object) */
	public T copy (Kyro kyro);
}
