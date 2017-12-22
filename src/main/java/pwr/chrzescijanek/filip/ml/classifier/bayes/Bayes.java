package pwr.chrzescijanek.filip.ml.classifier.bayes;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.classifier.AbstractClassifier;
import pwr.chrzescijanek.filip.ml.classifier.bayes.model.BayesModel;
import pwr.chrzescijanek.filip.ml.classifier.bayes.model.ClassAttributePair;
import pwr.chrzescijanek.filip.ml.classifier.bayes.model.ProbabilityProvider;
import pwr.chrzescijanek.filip.ml.classifier.bayes.model.ZeroProbability;
import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class Bayes extends AbstractClassifier {

	private BayesModel model;

	@Override
	protected void buildModel(final DataSet ds) {
		model = new BayesModel(ds);
	}
	
	public BayesModel getModel() {
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

}
