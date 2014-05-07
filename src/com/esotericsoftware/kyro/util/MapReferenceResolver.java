
package com.esotericsoftware.kyro.util;

import java.util.ArrayList;

import com.esotericsoftware.kyro.Kyro;
import com.esotericsoftware.kyro.ReferenceResolver;

/** Uses an {@link IdentityObjectIntMap} to track objects that have already been written. This can handle graph with any number of
 * objects, but is slightly slower than {@link ListReferenceResolver} for graphs with few objects.
 * @author Nathan Sweet <misc@n4te.com> */
public class MapReferenceResolver implements ReferenceResolver {
	protected Kyro kyro;
	protected final IdentityObjectIntMap writtenObjects = new IdentityObjectIntMap();
	protected final ArrayList readObjects = new ArrayList();

	public void setKryo (Kyro kyro) {
		this.kyro = kyro;
	}

	public int addWrittenObject (Object object) {
		int id = writtenObjects.size;
		writtenObjects.put(object, id);
		return id;
	}

	public int getWrittenId (Object object) {
		return writtenObjects.get(object, -1);
	}

	public int nextReadId (Class type) {
		int id = readObjects.size();
		readObjects.add(null);
		return id;
	}

	public void setReadObject (int id, Object object) {
		readObjects.set(id, object);
	}

	public Object getReadObject (Class type, int id) {
		return readObjects.get(id);
	}

	public void reset () {
		readObjects.clear();
		writtenObjects.clear();
	}

	/** Returns false for all primitive wrappers. */
	public boolean useReferences (Class type) {
		return !Util.isWrapperClass(type);
	}
}
