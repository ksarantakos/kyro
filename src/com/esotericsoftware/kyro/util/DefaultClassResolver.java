
package com.esotericsoftware.kyro.util;

import static com.esotericsoftware.kyro.util.Util.*;
import static com.esotericsoftware.minlog.Log.*;

import com.esotericsoftware.kyro.ClassResolver;
import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.KyroException;
import com.esotericsoftware.kyro.Registration;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;

/** Resolves classes by ID or by fully qualified class name.
 * @author Nathan Sweet <misc@n4te.com> */
public class DefaultClassResolver implements ClassResolver {
	static public final byte NAME = -1;

	protected Kyro kyro;

	protected final IntMap<Registration> idToRegistration = new IntMap();
	protected final ObjectMap<Class, Registration> classToRegistration = new ObjectMap();

	protected IdentityObjectIntMap<Class> classToNameId;
	protected IntMap<Class> nameIdToClass;
	protected ObjectMap<String, Class> nameToClass;
	protected int nextNameId;

	private int memoizedClassId = -1;
	private Registration memoizedClassIdValue;
	private Class memoizedClass;
	private Registration memoizedClassValue;

	public void setKryo (Kyro kyro) {
		this.kyro = kyro;
	}

	public Registration register (Registration registration) {
		if (registration == null) throw new IllegalArgumentException("registration cannot be null.");
		if (registration.getId() != NAME) {
			if (TRACE) {
				trace("kyro", "Register class ID " + registration.getId() + ": " + className(registration.getType()) + " ("
					+ registration.getSerializer().getClass().getName() + ")");
			}
			idToRegistration.put(registration.getId(), registration);
		} else if (TRACE) {
			trace("kyro", "Register class name: " + className(registration.getType()) + " ("
				+ registration.getSerializer().getClass().getName() + ")");
		}
		classToRegistration.put(registration.getType(), registration);
		if (registration.getType().isPrimitive()) classToRegistration.put(getWrapperClass(registration.getType()), registration);
		return registration;
	}

	public Registration registerImplicit (Class type) {
		return register(new Registration(type, kyro.getDefaultSerializer(type), NAME));
	}

	public Registration getRegistration (Class type) {
		if (type == memoizedClass) return memoizedClassValue;
		Registration registration = classToRegistration.get(type);
		if (registration != null) {
			memoizedClass = type;
			memoizedClassValue = registration;
		}
		return registration;
	}

	public Registration getRegistration (int classID) {
		return idToRegistration.get(classID);
	}

	public Registration writeClass (Output output, Class type) {
		if (type == null) {
			if (TRACE || (DEBUG && kyro.getDepth() == 1)) log("Write", null);
			output.writeVarInt(Kyro.NULL, true);
			return null;
		}
		Registration registration = kyro.getRegistration(type);
		if (registration.getId() == NAME)
			writeName(output, type, registration);
		else {
			if (TRACE) trace("kyro", "Write class " + registration.getId() + ": " + className(type));
			output.writeVarInt(registration.getId() + 2, true);
		}
		return registration;
	}

	protected void writeName (Output output, Class type, Registration registration) {
		output.writeVarInt(NAME + 2, true);
		if (classToNameId != null) {
			int nameId = classToNameId.get(type, -1);
			if (nameId != -1) {
				if (TRACE) trace("kyro", "Write class name reference " + nameId + ": " + className(type));
				output.writeVarInt(nameId, true);
				return;
			}
		}
		// Only write the class name the first time encountered in object graph.
		if (TRACE) trace("kyro", "Write class name: " + className(type));
		int nameId = nextNameId++;
		if (classToNameId == null) classToNameId = new IdentityObjectIntMap();
		classToNameId.put(type, nameId);
		output.writeVarInt(nameId, true);
		output.writeString(type.getName());
	}

	public Registration readClass (Input input) {
		int classID = input.readVarInt(true);
		switch (classID) {
		case Kyro.NULL:
			if (TRACE || (DEBUG && kyro.getDepth() == 1)) log("Read", null);
			return null;
		case NAME + 2: // Offset for NAME and NULL.
			return readName(input);
		}
		if (classID == memoizedClassId) return memoizedClassIdValue;
		Registration registration = idToRegistration.get(classID - 2);
		if (registration == null) throw new KyroException("Encountered unregistered class ID: " + (classID - 2));
		if (TRACE) trace("kyro", "Read class " + (classID - 2) + ": " + className(registration.getType()));
		memoizedClassId = classID;
		memoizedClassIdValue = registration;
		return registration;
	}

	protected Registration readName (Input input) {
		int nameId = input.readVarInt(true);
		if (nameIdToClass == null) nameIdToClass = new IntMap();
		Class type = nameIdToClass.get(nameId);
		if (type == null) {
			// Only read the class name the first time encountered in object graph.
			String className = input.readString();
			type = getTypeByName(className);
			if (type == null) {
				try {
					type = Class.forName(className, false, kyro.getClassLoader());
				} catch (ClassNotFoundException ex) {
					throw new KyroException("Unable to find class: " + className, ex);
				}
				if (nameToClass == null) nameToClass = new ObjectMap();
				nameToClass.put(className, type);
			}
			nameIdToClass.put(nameId, type);
			if (TRACE) trace("kyro", "Read class name: " + className);
		} else {
			if (TRACE) trace("kyro", "Read class name reference " + nameId + ": " + className(type));
		}
		return kyro.getRegistration(type);
	}

	protected Class<?> getTypeByName(final String className) {
		return nameToClass != null ? nameToClass.get(className) : null;
	}

	public void reset () {
		if (!kyro.isRegistrationRequired()) {
			if (classToNameId != null) classToNameId.clear();
			if (nameIdToClass != null) nameIdToClass.clear();
			nextNameId = 0;
		}
	}
}
