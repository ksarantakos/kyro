
package com.esotericsoftware.kyro;

import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;

/** Handles class registration, writing class identifiers to bytes, and reading class identifiers from bytes.
 * @author Nathan Sweet <misc@n4te.com> */
public interface ClassResolver {
	/** Sets the Kyro instance that this ClassResolver will be used for. This is called automatically by Kyro. */
	public void setKryo (Kyro kyro);

	/** Stores the specified registration.
	 * @see Kyro#register(Registration) */
	public Registration register (Registration registration);

	/** Called when an unregistered type is encountered and {@link Kyro#setRegistrationRequired(boolean)} is false. */
	public Registration registerImplicit (Class type);

	/** Returns the registration for the specified class, or null if the class is not registered. */
	public Registration getRegistration (Class type);

	/** Returns the registration for the specified ID, or null if no class is registered with that ID. */
	public Registration getRegistration (int classID);

	/** Writes a class and returns its registration.
	 * @param type May be null.
	 * @return Will be null if type is null. */
	public Registration writeClass (Output output, Class type);

	/** Reads a class and returns its registration.
	 * @return May be null. */
	public Registration readClass (Input input);

	/** Called by {@link Kyro#reset()}. */
	public void reset ();
}
