
package com.esotericsoftware.kyro;

import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.serializers.DefaultSerializers.KryoSerializableSerializer;

/** Allows implementing classes to perform their own serialization. Hand written serialization can be more efficient in some cases.
 * <p>
 * The default serializer for KyroSerializable is {@link KryoSerializableSerializer}, which uses {@link Kyro#newInstance(Class)}
 * to construct the class.
 * @author Nathan Sweet <misc@n4te.com> */
public interface KyroSerializable {
	public void write (Kyro kyro, Output output);

	public void read (Kyro kyro, Input input);
}
