package pwr.chrzescijanek.filip.ml.classifier;

import java.util.List;
import java.util.Objects;

public class ConfusionMatrix {

	private final Integer truePositives;
	private final Integer trueNegatives;
	private final Integer falsePositives;
	private final Integer falseNegatives;

	public ConfusionMatrix(final Integer truePositives, final Integer trueNegatives,
	                       final Integer falsePositives, final Integer falseNegatives) {
		this.truePositives  = Objects.requireNonNull(truePositives);
		this.trueNegatives  = Objects.requireNonNull(trueNegatives);
		this.falsePositives = Objects.requireNonNull(falsePositives);
		this.falseNegatives = Objects.requireNonNull(falseNegatives);
	}

	public ConfusionMatrix(final List<ConfusionMatrix> matrices) {
		Objects.requireNonNull(matrices);
		this.truePositives  = (int) matrices.stream().mapToInt(ConfusionMatrix::getTruePositives ).average().orElse(0);
		this.trueNegatives  = (int) matrices.stream().mapToInt(ConfusionMatrix::getTrueNegatives ).average().orElse(0);
		this.falsePositives = (int) matrices.stream().mapToInt(ConfusionMatrix::getFalsePositives).average().orElse(0);
		this.falseNegatives = (int) matrices.stream().mapToInt(ConfusionMatrix::getFalseNegatives).average().orElse(0);
	}

	public Integer getTruePositives() {
		return truePositives;
	}

	public Integer getTrueNegatives() {
		return trueNegatives;
	}

	public Integer getFalsePositives() {
		return falsePositives;
	}

	public Integer getFalseNegatives() {
		return falseNegatives;
	}
	
	@Override
	public String toString() {
		return "[" + truePositives + ", " + trueNegatives + ", " + falsePositives + ", " + falseNegatives + "]";
	}

}
