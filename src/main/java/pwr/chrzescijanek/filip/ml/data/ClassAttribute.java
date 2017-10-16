package pwr.chrzescijanek.filip.ml.data;

import java.util.Objects;

public class ClassAttribute extends Attribute {

	private final String value;

	public ClassAttribute(final String name, final String value) {
		super(name);
		this.value = Objects.requireNonNull(value);
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ClassAttribute)) return false;
		if (!super.equals(o)) return false;

		final ClassAttribute that = (ClassAttribute) o;

		return getValue().equals(that.getValue());

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + getValue().hashCode();
		return result;
	}

}
