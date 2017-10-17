package pwr.chrzescijanek.filip.ml.classifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiscreteProbability implements ProbabilityProvider {
	
	private Map<String, Double> probabilities;
	
	public DiscreteProbability(List<String> allValues) {
		this.probabilities = Objects.requireNonNull(allValues).stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> (e.getValue() * 1.0) / allValues.size()));
	}
	
	public Map<String, Double> getProbabilities() {
		return probabilities;
	}

	@Override
	public Double getProbability(Object value) {
		if (!(value instanceof String)) {
			throw new IllegalArgumentException();
		}
		String s = (String) value;
		return getProbabilities().getOrDefault(s, 0.0);
	}

}
