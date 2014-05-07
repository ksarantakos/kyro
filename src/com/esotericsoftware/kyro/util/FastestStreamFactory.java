package com.esotericsoftware.kyro.util;

import java.io.InputStream;
import java.io.OutputStream;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.StreamFactory;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.io.UnsafeInput;
import com.esotericsoftware.kyro.io.UnsafeOutput;

/**
 * This StreamFactory tries to provide fastest possible Input/Output streams on a given platform.
 * It may return sun.misc.Unsafe based implementations of streams, which are
 * very fast, but not portable across platforms.
 * 
 * @author Roman Levenstein <romixlev@gmail.com>
 *
 */
public class FastestStreamFactory implements StreamFactory {
	
	static private boolean isUnsafe = UnsafeUtil.unsafe() != null;

	@Override
	public Input getInput() {
		return (isUnsafe)? new UnsafeInput() : new Input();
	}

	@Override
	public Input getInput(int bufferSize) {
		return (isUnsafe)? new UnsafeInput(bufferSize) : new Input(bufferSize);
	}

	@Override
	public Input getInput(byte[] buffer) {
		return (isUnsafe)? new UnsafeInput(buffer) : new Input(buffer);
	}

	@Override
	public Input getInput(byte[] buffer, int offset, int count) {
		return (isUnsafe)? new UnsafeInput(buffer, offset, count) : new Input(buffer, offset, count);
	}

	@Override
	public Input getInput(InputStream inputStream) {
		return (isUnsafe)? new UnsafeInput(inputStream) : new Input(inputStream);
	}

	@Override
	public Input getInput(InputStream inputStream, int bufferSize) {
		return (isUnsafe)? new UnsafeInput(inputStream, bufferSize) : new Input(inputStream, bufferSize);
	}

	@Override
	public Output getOutput() {
		return (isUnsafe)? new UnsafeOutput() : new Output();
	}

	@Override
	public Output getOutput(int bufferSize) {
		return (isUnsafe)? new UnsafeOutput(bufferSize) : new Output(bufferSize);
	}

	@Override
	public Output getOutput(int bufferSize, int maxBufferSize) {
		return (isUnsafe)? new UnsafeOutput(bufferSize, maxBufferSize) : new Output(bufferSize, maxBufferSize);
	}

	@Override
	public Output getOutput(byte[] buffer) {
		return (isUnsafe)? new UnsafeOutput(buffer) : new Output(buffer);
	}

	@Override
	public Output getOutput(byte[] buffer, int maxBufferSize) {
		return (isUnsafe)? new UnsafeOutput(buffer, maxBufferSize) : new Output(buffer, maxBufferSize);
	}

	@Override
	public Output getOutput(OutputStream outputStream) {
		return (isUnsafe)? new UnsafeOutput(outputStream) : new Output(outputStream);
	}

	@Override
	public Output getOutput(OutputStream outputStream, int bufferSize) {
		return (isUnsafe)? new UnsafeOutput(outputStream, bufferSize) : new Output(outputStream, bufferSize);
	}

	@Override
	public void setKryo(Kyro kyro) {
		// Only use Unsafe-based streams if this Kyro instance supports it
		//isUnsafe = UnsafeUtil.unsafe() != null && kyro.getUnsafe();
	}

}
