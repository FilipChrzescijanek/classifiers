package pwr.chrzescijanek.filip.ml.classifier.bayes.model;

public class ZeroProbability implements ProbabilityProvider {

	@Override
	public Double getProbability(Object value) {
		return 0.0;
	}

}
