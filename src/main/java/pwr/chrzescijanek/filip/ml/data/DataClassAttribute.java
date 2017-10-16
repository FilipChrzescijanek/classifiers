package pwr.chrzescijanek.filip.ml.data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DataClassAttribute extends Attribute {

	private final List<String> values;

	public DataClassAttribute(final String name, final List<String> values) {
		super(name);
		this.values = Collections.unmodifiableList(Objects.requireNonNull(values));
	}

	public List<String> getValues() {
		return values;
	}

}
