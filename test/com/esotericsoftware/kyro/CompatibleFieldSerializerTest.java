
package com.esotericsoftware.kyro;

import java.io.FileNotFoundException;

import com.esotericsoftware.kyro.serializers.CompatibleFieldSerializer;

/** @author Nathan Sweet <misc@n4te.com> */
public class CompatibleFieldSerializerTest extends KryoTestCase {
	{
		supportsCopy = true;
	}

	public void testCompatibleFieldSerializer () throws FileNotFoundException {
		TestClass object1 = new TestClass();
		object1.child = new TestClass();
		object1.other = new AnotherClass();
		object1.other.value = "meow";
		kyro.setDefaultSerializer(CompatibleFieldSerializer.class);
		kyro.register(TestClass.class);
		kyro.register(AnotherClass.class);
		roundTrip(100, 100, object1);
	}

	public void testAddedField () throws FileNotFoundException {
		TestClass object1 = new TestClass();
		object1.child = new TestClass();
		object1.other = new AnotherClass();
		object1.other.value = "meow";

		CompatibleFieldSerializer serializer = new CompatibleFieldSerializer(kyro, TestClass.class);
		serializer.removeField("text");
		kyro.register(TestClass.class, serializer);
		kyro.register(AnotherClass.class, new CompatibleFieldSerializer(kyro, AnotherClass.class));
		roundTrip(74, 74, object1);

		kyro.register(TestClass.class, new CompatibleFieldSerializer(kyro, TestClass.class));
		Object object2 = kyro.readClassAndObject(input);
		assertEquals(object1, object2);
	}

	public void testRemovedField () throws FileNotFoundException {
		TestClass object1 = new TestClass();
		object1.child = new TestClass();

		kyro.register(TestClass.class, new CompatibleFieldSerializer(kyro, TestClass.class));
		roundTrip(88, 88, object1);

		CompatibleFieldSerializer serializer = new CompatibleFieldSerializer(kyro, TestClass.class);
		serializer.removeField("text");
		kyro.register(TestClass.class, serializer);
		Object object2 = kyro.readClassAndObject(input);
		assertEquals(object1, object2);
	}

	static public class TestClass {
		public String text = "something";
		public int moo = 120;
		public long moo2 = 1234120;
		public TestClass child;
		public int zzz = 123;
		public AnotherClass other;

		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			TestClass other = (TestClass)obj;
			if (child == null) {
				if (other.child != null) return false;
			} else if (!child.equals(other.child)) return false;
			if (moo != other.moo) return false;
			if (moo2 != other.moo2) return false;
			if (text == null) {
				if (other.text != null) return false;
			} else if (!text.equals(other.text)) return false;
			if (zzz != other.zzz) return false;
			return true;
		}
	}

	static public class AnotherClass {
		String value;
	}
}
