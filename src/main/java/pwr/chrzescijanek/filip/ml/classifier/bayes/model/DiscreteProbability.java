package pwr.chrzescijanek.filip.ml.classifier.bayes.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscreteProbability implements ProbabilityProvider {
	
	private final Map<String, Double> probabilities;

	public DiscreteProbability(final List<String> attributeValues, final List<String> allValues) {
		Objects.requireNonNull(allValues);
		this.probabilities = Objects.requireNonNull(attributeValues)
		                            .stream()
		                            .collect(Collectors.toMap(Function.identity(), v -> allValues.stream().filter(val -> val.equals(v)).count()))
		                            .entrySet()
		                            .stream()
		                            .collect(Collectors.toMap(Map.Entry::getKey, e -> ((e.getValue() + 1) * 1.0) / (allValues.size() + attributeValues.size())));
	}
	
	@Override
	public Double getProbability(final Object value) {
		if (!(value instanceof String)) {
			throw new IllegalArgumentException();
		}
		final String s = (String) value;
		return getProbabilities().getOrDefault(s, 0.0);
	}

	public Map<String, Double> getProbabilities() {
		return probabilities;
	}

}
