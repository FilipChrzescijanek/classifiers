package pwr.chrzescijanek.filip.ml.data.record;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Record {

	private final List<Object> values;
	private final String clazz;

	public Record(final List<Object> values, final String clazz) {
		this.values = Collections.unmodifiableList(Objects.requireNonNull(values));
		this.clazz = Objects.requireNonNull(clazz);
	}

	public List<Object> getValues() {
		return values;
	}

	public String getClazz() {
		return clazz;
	}

}
