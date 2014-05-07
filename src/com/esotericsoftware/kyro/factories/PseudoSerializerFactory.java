package com.esotericsoftware.kyro.factories;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.Serializer;

/**
 * A serializer factory that always returns a given serializer instance. This implementation of {@link SerializerFactory}
 * is not a real factory since it only provides a given instance instead of dynamically creating new serializers. It can
 * be used when all types should be serialized by the same serializer. This also allows serializers to be shared among different
 * {@link Kyro} instances.
 *
 * @author Rafael Winterhalter <rafael.wth@web.de>
 */
public class PseudoSerializerFactory implements SerializerFactory {

	private final Serializer<?> serializer;

	public PseudoSerializerFactory (Serializer<?> serializer) {
		this.serializer = serializer;
	}

	@Override
	public Serializer makeSerializer (Kyro kyro, Class<?> type) {
		return serializer;
	}
}
