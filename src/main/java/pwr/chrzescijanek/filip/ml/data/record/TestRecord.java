package pwr.chrzescijanek.filip.ml.data.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import pwr.chrzescijanek.filip.ml.classifier.Classifier;

public class TestRecord {

	private final List<AttributeValuePair> values;
	private final String clazz;
	private String assignedClazz;

	public TestRecord(final List<Object> recordValues, List<String> attributeNames, final String clazz) {
		Objects.requireNonNull(recordValues);
		Objects.requireNonNull(attributeNames);
		
		List<AttributeValuePair> values = new ArrayList<>();
		for (int i = 0; i < recordValues.size(); i++) {
			values.add(new AttributeValuePair(attributeNames.get(i), recordValues.get(i)));
		}
		
		this.values = Collections.unmodifiableList(values);
		this.clazz = Objects.requireNonNull(clazz);
	}
	
	public List<AttributeValuePair> getValues() {
		return values;
	}

	public String getClazz() {
		return clazz;
	}

	public String getAssignedClazz() {
		return Optional.ofNullable(assignedClazz).orElse(Classifier.NULL_CLASS);
	}

	public void setAssignedClazz(final String assignedClazz) {
		this.assignedClazz = Objects.requireNonNull(assignedClazz);
	}

}
