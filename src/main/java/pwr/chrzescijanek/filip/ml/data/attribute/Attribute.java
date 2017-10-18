package pwr.chrzescijanek.filip.ml.data.attribute;

import java.util.Objects;

public abstract class Attribute {

	private final String name;

	protected Attribute(final String name) {this.name = Objects.requireNonNull(name);}

	public String getName() {
		return name;
	}

}
