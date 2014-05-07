
package com.esotericsoftware.kyro;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.TimeZone;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;

/** @author Nathan Sweet <misc@n4te.com> */
public class DefaultSerializersTest extends KryoTestCase {
	{
		supportsCopy = true;
	}

	public void testBoolean () {
		roundTrip(2, 2, true);
		roundTrip(2, 2, false);
	}

	public void testByte () {
		roundTrip(2, 2, (byte)1);
		roundTrip(2, 2, (byte)125);
		roundTrip(2, 2, (byte)-125);
	}

	public void testChar () {
		roundTrip(3, 3, 'a');
		roundTrip(3, 3, 'z');
	}

	public void testDouble () {
		roundTrip(9, 9, 0d);
		roundTrip(9, 9, 1234d);
		roundTrip(9, 9, 1234.5678d);
	}

	public void testFloat () {
		roundTrip(5, 5, 0f);
		roundTrip(5, 5, 123f);
		roundTrip(5, 5, 123.456f);
	}

	public void testInt () {
		roundTrip(2, 5, 0);
		roundTrip(2, 5, 63);
		roundTrip(3, 5, 64);
		roundTrip(3, 5, 127);
		roundTrip(3, 5, 128);
		roundTrip(3, 5, 8191);
		roundTrip(4, 5, 8192);
		roundTrip(4, 5, 16383);
		roundTrip(4, 5, 16384);
		roundTrip(5, 5, 2097151);
		roundTrip(4, 5, 1048575);
		roundTrip(5, 5, 134217727);
		roundTrip(6, 5, 268435455);
		roundTrip(6, 5, 134217728);
		roundTrip(6, 5, 268435456);
		roundTrip(2, 5, -64);
		roundTrip(3, 5, -65);
		roundTrip(3, 5, -8192);
		roundTrip(4, 5, -1048576);
		roundTrip(5, 5, -134217728);
		roundTrip(6, 5, -134217729);
	}

	public void testLong () {
		roundTrip(2, 9, 0l);
		roundTrip(2, 9, 63l);
		roundTrip(3, 9, 64l);
		roundTrip(3, 9, 127l);
		roundTrip(3, 9, 128l);
		roundTrip(3, 9, 8191l);
		roundTrip(4, 9, 8192l);
		roundTrip(4, 9, 16383l);
		roundTrip(4, 9, 16384l);
		roundTrip(5, 9, 2097151l);
		roundTrip(4, 9, 1048575l);
		roundTrip(5, 9, 134217727l);
		roundTrip(6, 9, 268435455l);
		roundTrip(6, 9, 134217728l);
		roundTrip(6, 9, 268435456l);
		roundTrip(2, 9, -64l);
		roundTrip(3, 9, -65l);
		roundTrip(3, 9, -8192l);
		roundTrip(4, 9, -1048576l);
		roundTrip(5, 9, -134217728l);
		roundTrip(6, 9, -134217729l);
		roundTrip(10, 9, 2368365495612416452l);
		roundTrip(10, 9, -2368365495612416452l);
	}

	public void testShort () {
		roundTrip(3, 3, (short)0);
		roundTrip(3, 3, (short)123);
		roundTrip(3, 3, (short)123);
		roundTrip(3, 3, (short)-123);
		roundTrip(3, 3, (short)250);
		roundTrip(3, 3, (short)123);
		roundTrip(3, 3, (short)400);
	}

	public void testString () {
		kyro = new Kyro();
		kyro.setRegistrationRequired(true);
		roundTrip(6, 6, "meow");
		roundTrip(70, 70, "abcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdefabcdef");

		kyro.setReferences(false);
		roundTrip(5, 5, "meow");

		roundTrip(3, 3, "a");
		roundTrip(3, 3, "\n");
		roundTrip(2, 2, "");
		roundTrip(100, 100,  "ABCDEFGHIJKLMNOPQRSTUVWXYZ\rabcdefghijklmnopqrstuvwxyz\n1234567890\t\"!`?'.,;:()[]{}<>|/@\\^$-%+=#_&~*");

		roundTrip(21, 21, "abcdef\u00E1\u00E9\u00ED\u00F3\u00FA\u7C9F");
	}

	public void testVoid () throws InstantiationException, IllegalAccessException {
		roundTrip(1, 1, (Void)null);
	}
	
	public void testNull () {
		kyro = new Kyro();
		kyro.setRegistrationRequired(true);
		kyro.register(ArrayList.class);
		roundTrip(1, 1, null);
		testNull(Long.class);
		testNull(ArrayList.class);

		kyro.setReferences(false);
		roundTrip(1, 1, null);
		testNull(Long.class);
		testNull(ArrayList.class);
	}

	private void testNull (Class type) {
		kyro.writeObjectOrNull(output, null, type);
		input.setBuffer(output.toBytes());
		Object object = kyro.readObjectOrNull(input, type);
		assertNull(object);
	}

	public void testDateSerializer () {
		kyro.register(Date.class);
		roundTrip(10, 9, new Date(-1234567));
		roundTrip(2, 9, new Date(0));
		roundTrip(4, 9, new Date(1234567));
		roundTrip(10, 9, new Date(-1234567));
	}

	public void testBigDecimalSerializer () {
		kyro.register(BigDecimal.class);
		roundTrip(5, 8, BigDecimal.valueOf(12345, 2));
	}

	public void testBigIntegerSerializer () {
		kyro.register(BigInteger.class);
		roundTrip(8, 8, BigInteger.valueOf(1270507903945L));
	}

	public void testEnumSerializer () {
		kyro.register(TestEnum.class);
		roundTrip(2, 2, TestEnum.a);
		roundTrip(2, 2, TestEnum.b);
		roundTrip(2, 2, TestEnum.c);

		kyro = new Kyro();
		kyro.setRegistrationRequired(false);
		// 1 byte identifying it's a class name
		// 1 byte for the class name id
		// 57 bytes for the class name characters
		// 1 byte for the reference id
		// 1 byte for the enum value
		roundTrip(61, 61, TestEnum.c);
	}

	public void testEnumSetSerializer () {
		kyro.register(EnumSet.class);
		kyro.register(TestEnum.class);
		roundTrip(5, 8, EnumSet.of(TestEnum.a, TestEnum.c));
		roundTrip(4, 7, EnumSet.of(TestEnum.a));
		roundTrip(6, 9, EnumSet.allOf(TestEnum.class));

		// Test empty EnumSet
		roundTrip(3, 6, EnumSet.noneOf(TestEnum.class));

		kyro = new Kyro();
		kyro.setRegistrationRequired(false);
		roundTrip(89, 92, EnumSet.of(TestEnum.a, TestEnum.c));
	}

	public void testEnumSerializerWithMethods () {
		kyro.register(TestEnumWithMethods.class);
		roundTrip(2, 2, TestEnumWithMethods.a);
		roundTrip(2, 2, TestEnumWithMethods.b);
		roundTrip(2, 2, TestEnumWithMethods.c);

		kyro = new Kyro();
		kyro.setRegistrationRequired(false);
		roundTrip(76, 76, TestEnumWithMethods.c);
	}

	public void testCollectionsMethods () {
		kyro.setRegistrationRequired(false);
		ArrayList test = new ArrayList();
		test.add(Collections.EMPTY_LIST);
		test.add(Collections.EMPTY_MAP);
		test.add(Collections.EMPTY_SET);
		test.add(Collections.singletonList("meow"));
		test.add(Collections.singletonMap("moo", 1234));
		test.add(Collections.singleton(12.34));
		roundTrip(249, 251, test);
	}

	public void testCalendar () {
		kyro.setRegistrationRequired(false);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		calendar.set(1980, 7, 26, 12, 22, 46);
		roundTrip(64, 73, calendar);
	}
	
	public void testClassSerializer() {
		kyro.register(Class.class);
		kyro.register(ArrayList.class);
		kyro.setRegistrationRequired(false);
		final Output out = new Output(1024);

		kyro.writeObject(out, String.class);
		kyro.writeObject(out, Integer.class);
		kyro.writeObject(out, Short.class);
		kyro.writeObject(out, Long.class);
		kyro.writeObject(out, Double.class);
		kyro.writeObject(out, Float.class);
		kyro.writeObject(out, Boolean.class);
		kyro.writeObject(out, Character.class);
		kyro.writeObject(out, Void.class);

		kyro.writeObject(out, int.class);
		kyro.writeObject(out, short.class);
		kyro.writeObject(out, long.class);
		kyro.writeObject(out, double.class);
		kyro.writeObject(out, float.class);
		kyro.writeObject(out, boolean.class);
		kyro.writeObject(out, char.class);
		kyro.writeObject(out, void.class);
		kyro.writeObject(out, ArrayList.class);
		kyro.writeObject(out, TestEnum.class);

		final Input in = new Input(out.getBuffer());

		assertEquals(String.class, kyro.readObject(in, Class.class));
		assertEquals(Integer.class, kyro.readObject(in, Class.class));
		assertEquals(Short.class, kyro.readObject(in, Class.class));
		assertEquals(Long.class, kyro.readObject(in, Class.class));
		assertEquals(Double.class, kyro.readObject(in, Class.class));
		assertEquals(Float.class, kyro.readObject(in, Class.class));
		assertEquals(Boolean.class, kyro.readObject(in, Class.class));
		assertEquals(Character.class, kyro.readObject(in, Class.class));
		assertEquals(Void.class, kyro.readObject(in, Class.class));
		assertEquals(int.class, kyro.readObject(in, Class.class));
		assertEquals(short.class, kyro.readObject(in, Class.class));
		assertEquals(long.class, kyro.readObject(in, Class.class));
		assertEquals(double.class, kyro.readObject(in, Class.class));
		assertEquals(float.class, kyro.readObject(in, Class.class));
		assertEquals(boolean.class, kyro.readObject(in, Class.class));
		assertEquals(char.class, kyro.readObject(in, Class.class));
		assertEquals(void.class, kyro.readObject(in, Class.class));
		assertEquals(ArrayList.class, kyro.readObject(in, Class.class));
		assertEquals(TestEnum.class, kyro.readObject(in, Class.class));
	}

	public enum TestEnum {
		a, b, c
	}

	public enum TestEnumWithMethods {
		a {
		},
		b {
		},
		c {
		}
	}
}
