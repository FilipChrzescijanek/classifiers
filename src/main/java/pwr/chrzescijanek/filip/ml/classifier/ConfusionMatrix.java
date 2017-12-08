package pwr.chrzescijanek.filip.ml.classifier;

import java.util.Objects;

public class ConfusionMatrix {

	private final Double truePositives;
	private final Double trueNegatives;
	private final Double falsePositives;
	private final Double falseNegatives;

	public ConfusionMatrix(final Integer truePositives, final Integer trueNegatives,
	                       final Integer falsePositives, final Integer falseNegatives) {
		this.truePositives  = Objects.requireNonNull(truePositives ).doubleValue();
		this.trueNegatives  = Objects.requireNonNull(trueNegatives ).doubleValue();
		this.falsePositives = Objects.requireNonNull(falsePositives).doubleValue();
		this.falseNegatives = Objects.requireNonNull(falseNegatives).doubleValue();
	}

	public Double getTruePositives() {
		return truePositives;
	}

	public Double getTrueNegatives() {
		return trueNegatives;
	}

	public Double getFalsePositives() {
		return falsePositives;
	}

	public Double getFalseNegatives() {
		return falseNegatives;
	}
	
	@Override
	public String toString() {
		return String.format("[[%.2f, %.2f], [%.2f, %.2f]]", truePositives, falsePositives, falseNegatives, trueNegatives);
	}

}
