package pwr.chrzescijanek.filip.ml.classifier.bayes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.attribute.ContinuousAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DataAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DiscreteAttribute;

public class BayesModel {

	private final Map<ClassAttributePair, ProbabilityProvider> probabilities;
	private final Map<String, Double> classProbabilities;

	public BayesModel(final DataSet ds) {
		Objects.requireNonNull(ds);
		this.probabilities      = Collections.unmodifiableMap(initializeProbabilities(ds));
		this.classProbabilities = Collections.unmodifiableMap(initializeClassProbabilities(ds));
	}

	public Map<ClassAttributePair, ProbabilityProvider> getProbabilities() {
		return probabilities;
	}

	public Map<String, Double> getClassProbabilities() {
		return classProbabilities;
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
			ProbabilityProvider provider = createProvider(ds, key);
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

	private ProbabilityProvider createProvider(final DataSet ds, ClassAttributePair key) {
		DataAttribute attribute = ds.getAttributeByName(key.getAttributeName());
		String clazz            = key.getClassValue();
		
		ProbabilityProvider provider;
		if (attribute.isDiscrete()) {
			DiscreteAttribute da = (DiscreteAttribute) attribute;
			provider = new DiscreteProbability(ds.getValuesForClass(da, clazz));
		} else {
			ContinuousAttribute ca = (ContinuousAttribute) attribute;
			provider = new GaussianProbability(ds.getMean(ca, clazz), ds.getStdDev(ca, clazz));
		}
		return provider;
	}

}