package pwr.chrzescijanek.filip.ml.data;

import java.util.Objects;

public abstract class Attribute {

	private final String name;

	protected Attribute(final String name) {this.name = Objects.requireNonNull(name);}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof Attribute)) return false;

		final Attribute attribute = (Attribute) o;

		return getName().equals(attribute.getName());

	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
