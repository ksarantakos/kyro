
package com.esotericsoftware.kyro.serializers;

import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.KyroException;
import com.esotericsoftware.kyro.Serializer;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;

/** Encrypts data using the blowfish cipher.
 * @author Nathan Sweet <misc@n4te.com> */
public class BlowfishSerializer extends Serializer {
	private final Serializer serializer;
	static private SecretKeySpec keySpec;

	public BlowfishSerializer (Serializer serializer, byte[] key) {
		this.serializer = serializer;
		keySpec = new SecretKeySpec(key, "Blowfish");
	}

	public void write (Kyro kyro, Output output, Object object) {
		Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
		CipherOutputStream cipherStream = new CipherOutputStream(output, cipher);
		Output cipherOutput = new Output(cipherStream, 256) {
			public void close () throws KyroException {
				// Don't allow the CipherOutputStream to close the output.
			}
		};
		serializer.write(kyro, cipherOutput, object);
		cipherOutput.flush();
		try {
			cipherStream.close();
		} catch (IOException ex) {
			throw new KyroException(ex);
		}
	}

	public Object read (Kyro kyro, Input input, Class type) {
		Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
		CipherInputStream cipherInput = new CipherInputStream(input, cipher);
		return serializer.read(kyro, new Input(cipherInput, 256), type); 
	}

	public Object copy (Kyro kyro, Object original) {
		return serializer.copy(kyro, original);
	}

	static private Cipher getCipher (int mode) {
		try {
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(mode, keySpec);
			return cipher;
		} catch (Exception ex) {
			throw new KyroException(ex);
		}
	}
}
