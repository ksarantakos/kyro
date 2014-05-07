
package com.esotericsoftware.kyro;

import javax.crypto.KeyGenerator;

import com.esotericsoftware.kyro.serializers.BlowfishSerializer;
import com.esotericsoftware.kyro.serializers.DefaultSerializers.StringSerializer;

/** @author Nathan Sweet <misc@n4te.com> */
public class BlowfishSerializerTest extends KryoTestCase {
	public void testZip () throws Exception {
		byte[] key = KeyGenerator.getInstance("Blowfish").generateKey().getEncoded();
		kyro.register(String.class, new BlowfishSerializer(new StringSerializer(), key));
		roundTrip(49, 49,  "abcdefabcdefabcdefabcdefabcdefabcdefabcdef");
	}
}
