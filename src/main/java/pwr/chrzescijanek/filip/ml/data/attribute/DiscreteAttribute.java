package pwr.chrzescijanek.filip.ml.data.attribute;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiscreteAttribute extends DataAttribute {

	private final List<String> values;
	
	public DiscreteAttribute(String name, List<String> values) {
		super(name);
		this.values = Collections.unmodifiableList(Objects.requireNonNull(values).stream().distinct().collect(Collectors.toList()));
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}

	public List<String> getValues() {
		return values;
	}

}
