package pwr.chrzescijanek.filip.ml.classifier.knn;

import java.util.Map;

public final class NullEntry<V> implements Map.Entry<String, V> {
	
	@Override
	public String getKey() {
		return null;
	}

	@Override
	public V getValue() {
		return null;
	}

	@Override
	public V setValue(V value) {
		return null;
	}
	
}