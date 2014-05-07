
package com.esotericsoftware.kyro;

import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.InputChunked;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.io.OutputChunked;

/** @author Nathan Sweet <misc@n4te.com> */
public class ChunkedTest extends KryoTestCase {
	public void testChunks () {
		Output output = new Output(512);
		output.writeInt(1234);
		OutputChunked outputChunked = new OutputChunked(output);
		outputChunked.writeInt(1);
		outputChunked.endChunks();
		outputChunked.writeInt(2);
		outputChunked.endChunks();
		outputChunked.writeInt(3);
		outputChunked.endChunks();
		outputChunked.writeInt(4);
		outputChunked.endChunks();
		outputChunked.writeInt(5);
		outputChunked.endChunks();
		output.writeInt(5678);
		output.close();

		Input input = new Input(output.getBuffer());
		assertEquals(1234, input.readInt());
		InputChunked inputChunked = new InputChunked(input);
		assertEquals(1, inputChunked.readInt());
		inputChunked.nextChunks();
		inputChunked.nextChunks(); // skip 3
		assertEquals(3, inputChunked.readInt());
		inputChunked.nextChunks();
		inputChunked.nextChunks(); // skip 4
		assertEquals(5, inputChunked.readInt());
		assertEquals(5678, input.readInt());
		input.close();
	}
}
