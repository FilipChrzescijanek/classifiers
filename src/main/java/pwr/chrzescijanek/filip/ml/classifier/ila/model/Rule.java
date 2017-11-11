package pwr.chrzescijanek.filip.ml.classifier.ila.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.record.AttributeValuePair;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class Rule {

	private final List<AttributeValuePair> condition;
	private final String clazz;
	
	public Rule(List<AttributeValuePair> condition, String clazz) {
		this.condition = Collections.unmodifiableList(Objects.requireNonNull(condition));
		this.clazz     = Objects.requireNonNull(clazz);
	}
	
	public Boolean isFulfilledBy(TestRecord tr) {
		return condition
				.stream()
				.allMatch(pair -> tr.getValues()
						.stream()
						.anyMatch(value -> value.getAttributeName().equals(pair.getAttributeName()) && value.getValue().equals(pair.getValue())));
	}
	
	public List<AttributeValuePair> getCondition() {
		return condition;
	}
	
	public String getClazz() {
		return clazz;
	}
	
	@Override
	public String toString() {
		return String.format("IF %s THEN %s", condition.stream().map(AttributeValuePair::toString).collect(Collectors.joining(" and ")), clazz);
	}
	
}
