
package com.esotericsoftware.kyro.serializers;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.InputChunked;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.io.OutputChunked;
import com.esotericsoftware.kyro.util.ObjectMap;

import static com.esotericsoftware.minlog.Log.*;

/** Serializes objects using direct field assignment, with limited support for forward and backward compatibility. Fields can be
 * added or removed without invalidating previously serialized bytes. Note that changing the type of a field is not supported.
 * <p>
 * There is additional overhead compared to {@link FieldSerializer}. A header is output the first time an object of a given type
 * is serialized. The header consists of an int for the number of fields, then a String for each field name. Also, to support
 * skipping the bytes for a field that no longer exists, for each field value an int is written that is the length of the value in
 * bytes.
 * <p>
 * Note that the field data is identified by name. The situation where a super class has a field with the same name as a subclass
 * must be avoided.
 * @author Nathan Sweet <misc@n4te.com> */
public class CompatibleFieldSerializer<T> extends FieldSerializer<T> {
	public CompatibleFieldSerializer (Kyro kyro, Class type) {
		super(kyro, type);
	}

	public void write (Kyro kyro, Output output, T object) {
		CachedField[] fields = getFields();
		ObjectMap context = kyro.getGraphContext();
		if (!context.containsKey(this)) {
			context.put(this, null);
			if (TRACE) trace("kyro", "Write " + fields.length + " field names.");
			output.writeVarInt(fields.length, true);
			for (int i = 0, n = fields.length; i < n; i++)
				output.writeString(fields[i].field.getName());
		}

		OutputChunked outputChunked = new OutputChunked(output, 1024);
		for (int i = 0, n = fields.length; i < n; i++) {
			fields[i].write(outputChunked, object);
			outputChunked.endChunks();
		}
	}

	public T read (Kyro kyro, Input input, Class<T> type) {
		T object = create(kyro, input, type);
		kyro.reference(object);
		ObjectMap context = kyro.getGraphContext();
		CachedField[] fields = (CachedField[])context.get(this);
		if (fields == null) {
			int length = input.readVarInt(true);
			if (TRACE) trace("kyro", "Read " + length + " field names.");
			String[] names = new String[length];
			for (int i = 0; i < length; i++)
				names[i] = input.readString();

			fields = new CachedField[length];
			CachedField[] allFields = getFields();
			outer:
			for (int i = 0, n = names.length; i < n; i++) {
				String schemaName = names[i];
				for (int ii = 0, nn = allFields.length; ii < nn; ii++) {
					if (allFields[ii].field.getName().equals(schemaName)) {
						fields[i] = allFields[ii];
						continue outer;
					}
				}
				if (TRACE) trace("kyro", "Ignore obsolete field: " + schemaName);
			}
			context.put(this, fields);
		}

		InputChunked inputChunked = new InputChunked(input, 1024);
		boolean hasGenerics = getGenerics() != null;
		for (int i = 0, n = fields.length; i < n; i++) {
			CachedField cachedField = fields[i];
			if(cachedField != null && hasGenerics) {
				// Generic type used to instantiate this field could have 
				// been changed in the meantime. Therefore take the most 
				// up-to-date definition of a field
				cachedField = getField(cachedField.field.getName());
			}
			if (cachedField == null) {
				if (TRACE) trace("kyro", "Skip obsolete field.");
				inputChunked.nextChunks();
				continue;
			}
			cachedField.read(inputChunked, object);
			inputChunked.nextChunks();
		}
		return object;
	}
}
