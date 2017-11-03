package pwr.chrzescijanek.filip.ml.data.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Record {

	private final List<AttributeValuePair> values;
	private final List<Object> rawValues;
	
	private final String clazz;

	public Record(final List<Object> rawValues, List<String> attributeNames, final String clazz) {
		List<AttributeValuePair> values = createValues(rawValues, attributeNames);
		
		this.values    = Collections.unmodifiableList(values);
		this.rawValues = Collections.unmodifiableList(rawValues);
		
		this.clazz = Objects.requireNonNull(clazz);
	}

	public List<AttributeValuePair> getValues() {
		return values;
	}

	public List<Object> getRawValues() {
		return rawValues;
	}

	public String getClazz() {
		return clazz;
	}

	private List<AttributeValuePair> createValues(final List<Object> rawValues, List<String> attributeNames) {
		Objects.requireNonNull(rawValues);
		Objects.requireNonNull(attributeNames);
		
		List<AttributeValuePair> values = new ArrayList<>();
		for (int i = 0; i < rawValues.size(); i++) {
			values.add(new AttributeValuePair(attributeNames.get(i), rawValues.get(i)));
		}
		return values;
	}

}
