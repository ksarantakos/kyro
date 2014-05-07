package com.esotericsoftware.kyro.io;

import com.esotericsoftware.kyro.Kyro;

import java.io.IOException;
import java.io.ObjectOutput;

/**
 * A kyro adapter for the {@link java.io.ObjectOutput} class. Note that this is not a Kyro implementation
 * of {@link java.io.ObjectOutputStream} which has special handling for default serialization and serialization
 * extras like writeReplace. By default it will simply delegate to the appropriate kyro method. Also, using
 * it will currently add one extra byte for each time {@link #writeObject(Object)} is invoked since we need
 * to allow unknown null objects.
 *
 * @author Robert DiFalco <robert.difalco@gmail.com>
 */
public class KryoObjectOutput extends KryoDataOutput implements ObjectOutput {

	 private final Kyro kyro;

	 public KryoObjectOutput (Kyro kyro, Output output) {
		  super(output);
		  this.kyro = kyro;
	 }

	 public void writeObject (Object obj) throws IOException {
		  kyro.writeClassAndObject(output, obj);
	 }

	 public void flush () throws IOException {
		  output.flush();
	 }

	 public void close () throws IOException {
		  output.close();
	 }
}
