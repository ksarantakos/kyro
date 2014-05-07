package com.esotericsoftware.kyro.factories;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.Serializer;

import static com.esotericsoftware.kyro.util.Util.className;

/**
 * This factory instantiates new serializers of a given class via reflection. The constructors of the given {@code serializerClass}
 * must either take an instance of {@link Kyro} and an instance of {@link Class} as its parameter, take only a {@link Kyro} or {@link Class}
 * as its only argument or take no arguments. If several of the described constructors are found, the first found constructor is used,
 * in the order as they were just described.
 *
 * @author Rafael Winterhalter <rafael.wth@web.de>
 */
public class ReflectionSerializerFactory implements SerializerFactory {

	private final Class<? extends Serializer> serializerClass;

	public ReflectionSerializerFactory (Class<? extends Serializer> serializerClass) {
		this.serializerClass = serializerClass;
	}

	@Override
	public Serializer makeSerializer (Kyro kyro, Class<?> type) {
		return makeSerializer(kyro, serializerClass, type);
	}

	/** Creates a new instance of the specified serializer for serializing the specified class. Serializers must have a zero
	 * argument constructor or one that takes (Kyro), (Class), or (Kyro, Class).
	*/
	public static Serializer makeSerializer (Kyro kyro, Class<? extends Serializer> serializerClass, Class<?> type) {
		try {
			try {
				return serializerClass.getConstructor(Kyro.class, Class.class).newInstance(kyro, type);
			} catch (NoSuchMethodException ex1) {
				try {
					return serializerClass.getConstructor(Kyro.class).newInstance(kyro);
				} catch (NoSuchMethodException ex2) {
					try {
						return serializerClass.getConstructor(Class.class).newInstance(type);
					} catch (NoSuchMethodException ex3) {
						return serializerClass.newInstance();
					}
				}
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unable to create serializer \"" + serializerClass.getName() + "\" for class: " + className(type), ex);
		}

	}
}
