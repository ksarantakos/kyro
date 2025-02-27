
package com.esotericsoftware.kyro;

import com.esotericsoftware.kyro.serializers.DefaultArraySerializers.ObjectArraySerializer;

/** @author Nathan Sweet <misc@n4te.com> */
public class ArraySerializerTest extends KryoTestCase {
	{
		supportsCopy = true;
	}

	public void testArrays () {
		kyro.register(int[].class);
		kyro.register(int[][].class);
		kyro.register(int[][][].class);
		kyro.register(String[].class);
		kyro.register(Object[].class);
		roundTrip(4, 4, new Object[] {null, null});
		roundTrip(6, 6, new Object[] {null, "2"});
		roundTrip(6, 18, new int[] {1, 2, 3, 4});
		roundTrip(7, 18, new int[] {1, 2, -100, 4});
		roundTrip(9, 18, new int[] {1, 2, -100, 40000});
		roundTrip(9, 20, new int[][] { {1, 2}, {100, 4}});
		roundTrip(11, 22, new int[][] { {1}, {2}, {100}, {4}});
		roundTrip(13, 24, new int[][][] { { {1}, {2}}, { {100}, {4}}});
		roundTrip(12, 12, new String[] {"11", "2222", "3", "4"});
		roundTrip(11, 11, new String[] {"11", "2222", null, "4"});
		roundTrip(28, 51,
			new Object[] {new String[] {"11", "2222", null, "4"}, new int[] {1, 2, 3, 4}, new int[][] { {1, 2}, {100, 4}}});

		ObjectArraySerializer serializer = new ObjectArraySerializer(kyro, String[].class);
		kyro.register(String[].class, serializer);
		serializer.setElementsAreSameType(true);
		roundTrip(11, 11, new String[] {"11", "2222", null, "4"});
		serializer.setElementsAreSameType(false);
		roundTrip(11, 11, new String[] {"11", "2222", null, "4"});
		roundTrip(5, 5, new String[] {null, null, null});
		roundTrip(2, 2, new String[] {});
		serializer.setElementsAreSameType(true);
		roundTrip(12, 12, new String[] {"11", "2222", "3", "4"});
		serializer.setElementsCanBeNull(false);
		roundTrip(12, 12, new String[] {"11", "2222", "3", "4"});

		serializer = new ObjectArraySerializer(kyro, Float[].class);
		kyro.register(Float[][].class, serializer);
		kyro.register(Float[].class, serializer);
		Float[][] array = new Float[4][];
		array[0] = new Float[] {0.0f, 1.0f};
		array[1] = null;
		array[2] = new Float[] {2.0f, 3.0f};
		array[3] = new Float[] {3.0f};
		roundTrip(31, 31, array);
	}
}
