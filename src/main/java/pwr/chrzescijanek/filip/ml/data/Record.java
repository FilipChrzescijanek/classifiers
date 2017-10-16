package pwr.chrzescijanek.filip.ml.data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Record {

	private final List<ValueAttribute> attributes;
	private final ClassAttribute clazz;

	public Record(final List<ValueAttribute> attributes, final ClassAttribute clazz) {
		this.attributes = Collections.unmodifiableList(Objects.requireNonNull(attributes));
		this.clazz = Objects.requireNonNull(clazz);
	}

	public List<ValueAttribute> getAttributes() {
		return attributes;
	}

	public ClassAttribute getClazz() {
		return clazz;
	}

}
