
package com.esotericsoftware.kyro;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import junit.framework.Assert;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.io.Input;
import com.esotericsoftware.kyro.io.Output;
import com.esotericsoftware.kyro.serializers.MapSerializer;

/** @author Nathan Sweet <misc@n4te.com> */
public class MapSerializerTest extends KryoTestCase {
	{
		supportsCopy = true;
	}

	public void testMaps () {
		kyro.register(HashMap.class);
		kyro.register(LinkedHashMap.class);
		HashMap map = new HashMap();
		map.put("123", "456");
		map.put("789", "abc");
		roundTrip(18, 21, map);
		roundTrip(2, 5, new LinkedHashMap());
		roundTrip(18, 21, new LinkedHashMap(map));

		MapSerializer serializer = new MapSerializer();
		kyro.register(HashMap.class, serializer);
		kyro.register(LinkedHashMap.class, serializer);
		serializer.setKeyClass(String.class, kyro.getSerializer(String.class));
		serializer.setKeysCanBeNull(false);
		serializer.setValueClass(String.class, kyro.getSerializer(String.class));
		roundTrip(14, 17, map);
		serializer.setValuesCanBeNull(false);
		roundTrip(14, 17, map);
	}

	public void testEmptyHashMap () {
		execute(new HashMap<Object, Object>(), 0);
	}

	public void testNotEmptyHashMap () {
		execute(new HashMap<Object, Object>(), 1000);
	}

	public void testEmptyConcurrentHashMap () {
		execute(new ConcurrentHashMap<Object, Object>(), 0);
	}

	public void testNotEmptyConcurrentHashMap () {
		execute(new ConcurrentHashMap<Object, Object>(), 1000);
	}

	public void testGenerics () {
		kyro.register(HasGenerics.class);
		kyro.register(Integer[].class);
		kyro.register(HashMap.class);

		HasGenerics test = new HasGenerics();
		test.map.put("moo", new Integer[] {1, 2});

		output = new Output(4096);
		kyro.writeClassAndObject(output, test);
		output.flush();

		input = new Input(output.toBytes());
		HasGenerics test2 = (HasGenerics)kyro.readClassAndObject(input);
		assertEquals(test.map.get("moo"), test2.map.get("moo"));
	}

	private void execute (Map<Object, Object> map, int inserts) {
		Random random = new Random();
		for (int i = 0; i < inserts; i++)
			map.put(random.nextLong(), random.nextBoolean());

		Kyro kyro = new Kyro();
		kyro.register(HashMap.class, new MapSerializer());
		kyro.register(ConcurrentHashMap.class, new MapSerializer());

		Output output = new Output(2048, -1);
		kyro.writeClassAndObject(output, map);
		output.close();

		Input input = new Input(output.toBytes());
		Object deserialized = kyro.readClassAndObject(input);
		input.close();

		Assert.assertEquals(map, deserialized);
	}

	public void testTreeMap () {
		kyro.register(TreeMap.class);
		TreeMap map = new TreeMap();
		map.put("123", "456");
		map.put("789", "abc");
		roundTrip(19, 22, map);

		kyro.register(KeyThatIsntComparable.class);
		kyro.register(KeyComparator.class);
		map = new TreeMap(new KeyComparator());
		KeyThatIsntComparable key1 = new KeyThatIsntComparable();
		KeyThatIsntComparable key2 = new KeyThatIsntComparable();
		key1.value = "123";
		map.put(key1, "456");
		key2.value = "1234";
		map.put(key2, "4567");
		roundTrip(21, 24, map);
	}

	public void testTreeMapWithReferences () {
		kyro.setReferences(true);
		kyro.register(TreeMap.class);
		TreeMap map = new TreeMap();
		map.put("123", "456");
		map.put("789", "abc");
		roundTrip(24, 27, map);

		kyro.register(KeyThatIsntComparable.class);
		kyro.register(KeyComparator.class);
		map = new TreeMap(new KeyComparator());
		KeyThatIsntComparable key1 = new KeyThatIsntComparable();
		KeyThatIsntComparable key2 = new KeyThatIsntComparable();
		key1.value = "123";
		map.put(key1, "456");
		key2.value = "1234";
		map.put(key2, "4567");
		roundTrip(29, 32, map);
	}
	
	static public class HasGenerics {
		public HashMap<String, Integer[]> map = new HashMap();
		public HashMap<String, ?> map2 = new HashMap();
	}

	static public class KeyComparator implements Comparator<KeyThatIsntComparable> {
		public int compare (KeyThatIsntComparable o1, KeyThatIsntComparable o2) {
			return o1.value.compareTo(o2.value);
		}
	}

	static public class KeyThatIsntComparable {
		public String value;
		public KeyThatIsntComparable () {
		}
		public KeyThatIsntComparable (String value) {
			this.value = value;
		}
	}
}
