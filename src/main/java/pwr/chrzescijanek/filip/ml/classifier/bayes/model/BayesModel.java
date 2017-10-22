package pwr.chrzescijanek.filip.ml.classifier.bayes.model;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.attribute.ContinuousAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DataAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DiscreteAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BayesModel {

	private final Map<ClassAttributePair, ProbabilityProvider> probabilities;

	private final Map<String, Double> classProbabilities;

	public BayesModel(final DataSet ds) {
		Objects.requireNonNull(ds);
		this.probabilities      = Collections.unmodifiableMap(initializeProbabilities(ds));
		this.classProbabilities = Collections.unmodifiableMap(initializeClassProbabilities(ds));
	}

	private Map<ClassAttributePair, ProbabilityProvider> initializeProbabilities(final DataSet ds) {
		final List<ClassAttributePair> keys = generateKeys(ds);
		final Map<ClassAttributePair, ProbabilityProvider> probabilities = new HashMap<>();
		for (final ClassAttributePair key : keys) {
			final ProbabilityProvider provider = createProvider(ds, key);
			probabilities.put(key, provider);
		}
		return probabilities;
	}

	private Map<String, Double> initializeClassProbabilities(final DataSet ds) {
		return ds.getClazz().getValues()
		         .stream()
		         .collect(Collectors.toMap(Function.identity(), s -> ds.getRecords()
		                                                               .stream()
		                                                               .filter(r -> r.getClazz().equals(s))
		                                                               .count() / (1.0 * ds.getRecords().size())));
	}

	private List<ClassAttributePair> generateKeys(final DataSet ds) {
		final List<ClassAttributePair> keys = new ArrayList<>();
		final List<String> attributeNames = ds.getAttributeNames();
		for (final String classValue : ds.getClazz().getValues()) {
			keys.addAll(attributeNames
					            .stream()
					            .map(attributeName -> new ClassAttributePair(classValue, attributeName))
					            .collect(Collectors.toList()));
		}
		return keys;
	}

	private ProbabilityProvider createProvider(final DataSet ds, final ClassAttributePair key) {
		final DataAttribute attribute = ds.getAttributeByName(key.getAttributeName());
		final String clazz = key.getClassValue();

		final ProbabilityProvider provider;
		if (attribute.isDiscrete()) {
			final DiscreteAttribute da = (DiscreteAttribute) attribute;
			provider = new DiscreteProbability(da.getValues(), ds.getValuesForClass(da, clazz));
		}
		else {
			final ContinuousAttribute ca = (ContinuousAttribute) attribute;
			provider = new GaussianProbability(ds.getMean(ca, clazz), ds.getStdDev(ca, clazz));
		}
		return provider;
	}

	public Map<ClassAttributePair, ProbabilityProvider> getProbabilities() {
		return probabilities;
	}

	public Map<String, Double> getClassProbabilities() {
		return classProbabilities;
	}

}