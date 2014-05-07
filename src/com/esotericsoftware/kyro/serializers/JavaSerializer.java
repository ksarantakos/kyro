
package com.esotericsoftware.kyro.serializers;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.KyroException;
import com.esotericsoftware.kyro.KyroSerializable;
import com.esotericsoftware.kyro.Serializer;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.util.ObjectMap;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/** Serializes objects using Java's built in serialization mechanism. Note that this is very inefficient and should be avoided if
 * possible.
 * @see Serializer
 * @see FieldSerializer
 * @see KyroSerializable
 * @author Nathan Sweet <misc@n4te.com> */
public class JavaSerializer extends Serializer {
	public void write (Kyro kyro, Output output, Object object) {
		try {
			ObjectMap graphContext = kyro.getGraphContext();
			ObjectOutputStream objectStream = (ObjectOutputStream)graphContext.get(this);
			if (objectStream == null) {
				objectStream = new ObjectOutputStream(output);
				graphContext.put(this, objectStream);
			}
			objectStream.writeObject(object);
			objectStream.flush();
		} catch (Exception ex) {
			throw new KyroException("Error during Java serialization.", ex);
		}
	}

	public Object read (Kyro kyro, Input input, Class type) {
		try {
			ObjectMap graphContext = kyro.getGraphContext();
			ObjectInputStream objectStream = (ObjectInputStream)graphContext.get(this);
			if (objectStream == null) {
				objectStream = new ObjectInputStream(input);
				graphContext.put(this, objectStream);
			}
			return objectStream.readObject();
		} catch (Exception ex) {
			throw new KyroException("Error during Java deserialization.", ex);
		}
	}
}
