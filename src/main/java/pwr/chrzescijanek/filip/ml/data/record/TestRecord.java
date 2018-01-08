package pwr.chrzescijanek.filip.ml.data.record;

import java.util.List;
import java.util.Optional;

import pwr.chrzescijanek.filip.ml.classifier.Classifier;

public class TestRecord extends Record {

	private String assignedClazz;
	private Double weight;
	
	public TestRecord(final List<Object> rawValues, List<String> attributeNames, final String clazz) {
		super(rawValues, attributeNames, clazz);
		weight = 1.0;
	}
	
	public String getAssignedClazz() {
		return Optional.ofNullable(assignedClazz).orElse(Classifier.NULL_CLASS);
	}
	
	public Double getWeight() {
		return weight;
	}

	public void setAssignedClazz(final String assignedClazz) {
		this.assignedClazz = assignedClazz;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

}
