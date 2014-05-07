package com.esotericsoftware.kyro.factories;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.Serializer;

/**
 * A serializer factory that allows the creation of serializers. This factory will be called when a {@link Kyro}
 * serializer discovers a new type for which no serializer is yet known. For example, when a factory is registered
 * via {@link Kyro#setDefaultSerializer(SerializerFactory)} a different serializer can be created dependent on the
 * type of a class.
 *
 * @author Rafael Winterhalter <rafael.wth@web.de>
 */
public interface SerializerFactory {

    /**
     * Creates a new serializer
     * @param kyro The serializer instance requesting the new serializer.
     * @param type The type of the object that is to be serialized.
     * @return An implementation of a serializer that is able to serialize an object of type {@code type}.
     */
	Serializer makeSerializer (Kyro kyro, Class<?> type);
}
