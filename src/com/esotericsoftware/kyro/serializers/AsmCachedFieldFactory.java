package com.esotericsoftware.kyro.serializers;

import java.lang.reflect.Field;

import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmBooleanField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmByteField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmCharField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmDoubleField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmFloatField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmIntField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmLongField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmObjectField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmShortField;
import com.esotericsoftware.kyro.serializers.AsmCacheFields.AsmStringField;
import com.esotericsoftware.kyro.serializers.FieldSerializer.CachedField;
import com.esotericsoftware.kyro.serializers.FieldSerializer.CachedFieldFactory;

class AsmCachedFieldFactory implements CachedFieldFactory {
	public CachedField createCachedField (Class fieldClass, Field field, FieldSerializer ser) {
		CachedField cachedField;
		// Use ASM-based serializers
		if (fieldClass.isPrimitive()) {
			if (fieldClass == boolean.class)
				cachedField = new AsmBooleanField();
			else if (fieldClass == byte.class)
				cachedField = new AsmByteField();
			else if (fieldClass == char.class)
				cachedField = new AsmCharField();
			else if (fieldClass == short.class)
				cachedField = new AsmShortField();
			else if (fieldClass == int.class)
				cachedField = new AsmIntField();
			else if (fieldClass == long.class)
				cachedField = new AsmLongField();
			else if (fieldClass == float.class)
				cachedField = new AsmFloatField();
			else if (fieldClass == double.class)
				cachedField = new AsmDoubleField();
			else {
				cachedField = new AsmObjectField(ser);
			}
		} else if (fieldClass == String.class
			&& (!ser.kyro.getReferences() || !ser.kyro.getReferenceResolver().useReferences(String.class))) {
			cachedField = new AsmStringField();
		} else {
			cachedField = new AsmObjectField(ser);
		}
		return cachedField;
	}
}
