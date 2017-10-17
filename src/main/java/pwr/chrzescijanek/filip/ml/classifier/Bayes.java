package pwr.chrzescijanek.filip.ml.classifier;

import pwr.chrzescijanek.filip.ml.data.ContinuousAttribute;
import pwr.chrzescijanek.filip.ml.data.DataAttribute;
import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.DiscreteAttribute;
import pwr.chrzescijanek.filip.ml.data.TestRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Bayes extends AbstractClassifier {

	private Model model;

	@Override
	protected void buildModel(final DataSet ds) {
		model = new Model(ds);
	}
	
	public Model getModel() {
		return model;
	}

	@Override
	protected void assignClass(final TestRecord tr) {
		Optional.ofNullable(getModel()).ifPresent(m -> {
			final Map<ClassAttributePair, ProbabilityProvider> probabilities = m.getProbabilities();
			final Map<String, Double> classProbabilities = m.getClassProbabilities();
			classProbabilities
					.entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
					                          e -> classProbabilities.getOrDefault(e.getKey(), 0.0) *
					                               tr.getValues()
					                                 .stream()
					                                 .mapToDouble(
					                                		 value -> probabilities.getOrDefault(new ClassAttributePair(e.getKey(), value.getAttributeName()), new ZeroProbability()).getProbability(value.getValue())).reduce(1.0, (a, b) -> a * b)))
					.entrySet().stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).ifPresent(
							max -> tr.setAssignedClazz(max.getKey())
					);
		});
	}

	private class Model {

		private final Map<ClassAttributePair, ProbabilityProvider> probabilities;
		private final Map<String, Double> classProbabilities;

		Model(final DataSet ds) {
			Objects.requireNonNull(ds);
			this.classProbabilities = Collections.unmodifiableMap(initializeClassProbabilities(ds));
			this.probabilities = Collections.unmodifiableMap(initializeProbabilities(ds));
		}

		public Map<String, Double> getClassProbabilities() {
			return classProbabilities;
		}

		public Map<ClassAttributePair, ProbabilityProvider> getProbabilities() {
			return probabilities;
		}

		private Map<String, Double> initializeClassProbabilities(final DataSet ds) {
			 return ds.getClazz().getValues()
					.stream()
					.collect(Collectors.toMap(s -> s, s -> ds.getRecords()
							.stream()
							.filter(r -> r.getClazz().equals(s))
							.count() / (1.0 * ds.getRecords().size())));
		}

		private Map<ClassAttributePair, ProbabilityProvider> initializeProbabilities(final DataSet ds) {
			List<ClassAttributePair> keys = generateKeys(ds);
			Map<ClassAttributePair, ProbabilityProvider> probabilities = new HashMap<>();
			for (ClassAttributePair key : keys) {
				DataAttribute attribute = ds.getAttributeByName(key.getAttributeName());
				ProbabilityProvider provider = createProvider(ds, key, attribute);
				probabilities.put(key, provider);
			}
			return probabilities;
		}

		private List<ClassAttributePair> generateKeys(final DataSet ds) {
			List<ClassAttributePair> keys = new ArrayList<>();
			List<String> attributeNames = ds.getAttributeNames();
			for (String classValue : ds.getClazz().getValues()) {
				for (String attributeName : attributeNames) {
					keys.add(new ClassAttributePair(classValue, attributeName));
				}
			}
			return keys;
		}

		private ProbabilityProvider createProvider(final DataSet ds, ClassAttributePair key, DataAttribute attribute) {
			ProbabilityProvider provider;
			if (attribute.isDiscrete()) {
				DiscreteAttribute da = (DiscreteAttribute) attribute;
				provider = new DiscreteProbability(ds.getValuesForClass(da, key.getClassValue()));
			} else {
				ContinuousAttribute ca = (ContinuousAttribute) attribute;
				provider = new GaussianProbability(ca.getMean(), ca.getStdDev());
			}
			return provider;
		}
	
	}

}
