package pwr.chrzescijanek.filip.ml.classifier;

public class GaussianProbability implements ProbabilityProvider {

	private final Double mean;
	private final Double variance;
	
	public GaussianProbability(Double mean, Double stdDev) {
		this.mean = mean;
		this.variance = Math.pow(stdDev, 2);
	}

	public Double getMean() {
		return mean;
	}

	public Double getVariance() {
		return variance;
	}

	@Override
	public Double getProbability(Object value) {
		if (!(value instanceof Double)) {
			throw new IllegalArgumentException();
		}
		Double d = (Double) value;
		return 1 / Math.sqrt(2 * Math.PI * getVariance()) * Math.pow(Math.E, -Math.pow(d - getMean(), 2) / (2 * getVariance()));
	}

}
