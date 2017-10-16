package pwr.chrzescijanek.filip.ml.data;

import java.util.Objects;

public class ValueAttribute extends Attribute {

	private final Bin value;

	public ValueAttribute(final String name, final Bin value) {
		super(name);
		this.value = Objects.requireNonNull(value);
	}

	public Bin getValue() {
		return value;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof ValueAttribute)) return false;
		if (!super.equals(o)) return false;

		final ValueAttribute that = (ValueAttribute) o;

		return getValue().equals(that.getValue());

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + getValue().hashCode();
		return result;
	}

}
