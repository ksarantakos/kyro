
package com.esotericsoftware.kyro.serializers;

import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.KyroException;
import com.esotericsoftware.kyro.Serializer;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.InputChunked;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.io.OutputChunked;

public class DeflateSerializer extends Serializer {
	private final Serializer serializer;
	private boolean noHeaders = true;
	private int compressionLevel = 4;

	public DeflateSerializer (Serializer serializer) {
		this.serializer = serializer;
	}

	public void write (Kyro kyro, Output output, Object object) {
		Deflater deflater = new Deflater(compressionLevel, noHeaders);
		OutputChunked outputChunked = new OutputChunked(output, 256);
		DeflaterOutputStream deflaterStream = new DeflaterOutputStream(outputChunked, deflater);
		Output deflaterOutput = new Output(deflaterStream, 256);
		kyro.writeObject(deflaterOutput, object, serializer);
		deflaterOutput.flush();
		try {
			deflaterStream.finish();
		} catch (IOException ex) {
			throw new KyroException(ex);
		}
		outputChunked.endChunks();
	}

	public Object read (Kyro kyro, Input input, Class type) {
		// The inflater would read from input beyond the compressed bytes if chunked enoding wasn't used.
		InflaterInputStream inflaterStream = new InflaterInputStream(new InputChunked(input, 256), new Inflater(noHeaders));
		return kyro.readObject(new Input(inflaterStream, 256), type, serializer);
	}

	public void setNoHeaders (boolean noHeaders) {
		this.noHeaders = noHeaders;
	}

	/** Default is 4.
	 * @see Deflater#setLevel(int) */
	public void setCompressionLevel (int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public Object copy (Kyro kyro, Object original) {
		return serializer.copy(kyro, original);
	}
}
