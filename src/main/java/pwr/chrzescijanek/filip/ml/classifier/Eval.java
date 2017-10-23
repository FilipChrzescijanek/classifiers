package pwr.chrzescijanek.filip.ml.classifier;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Eval {

	private final List<ConfusionMatrix> matrices;

	private final Double accuracy;
	private final Double precision;
	private final Double recall;
	private final Double fscore;

	private Eval(final List<ConfusionMatrix> matrices, final Double accuracy, final Double precision, final Double recall, final Double fscore) {
		this.matrices  = Objects.requireNonNull(matrices);
		this.accuracy  = accuracy;
		this.precision = precision;
		this.recall    = recall;
		this.fscore    = fscore;
	}

	public static Eval createFromMatrices(final List<ConfusionMatrix> matrices) {
		Double accuracy  = 0.0;
		Double precision = 0.0;
		Double recall    = 0.0;
		Double fscore    = 0.0;

		for (final ConfusionMatrix cm : matrices) {
			final Double tp = cm.getTruePositives();
			final Double tn = cm.getTrueNegatives();
			final Double fp = cm.getFalsePositives();
			final Double fn = cm.getFalseNegatives();

			final Double currentPrecision = (tp + fp) != 0 ? (1.0 * tp) / (tp + fp) : 1.0;
			final Double currentRecall    = (tp + fn) != 0 ? (1.0 * tp) / (tp + fn) : 1.0;

			accuracy  += (tp + tn + fp + fn) != 0 ? (1.0 * (tp + tn)) / (tp + tn + fp + fn) : 1.0;
			precision += currentPrecision;
			recall    += currentRecall;
			fscore    += 2 / ((1 / currentRecall) + (1 / currentPrecision));
		}

		final Integer size = matrices.size();
		return new Eval(matrices, accuracy / size, precision / size, recall / size, fscore / size);
	}

	public static Eval createAverage(final List<Eval> evals) {
		final List<ConfusionMatrix> matrices  = Objects.requireNonNull(evals)
		                                               .stream()
		                                               .map(e -> new ConfusionMatrix(e.getMatrices()))
		                                               .collect(Collectors.toList());
		final Double accuracy  = evals.stream().mapToDouble(Eval::getAccuracy).average().orElse(0.0);
		final Double precision = evals.stream().mapToDouble(Eval::getPrecision).average().orElse(0.0);
		final Double recall    = evals.stream().mapToDouble(Eval::getRecall).average().orElse(0.0);
		final Double fscore    = evals.stream().mapToDouble(Eval::getFscore).average().orElse(0.0);
		return new Eval(matrices, accuracy, precision, recall, fscore);
	}

	public List<ConfusionMatrix> getMatrices() {
		return matrices;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public Double getPrecision() {
		return precision;
	}

	public Double getRecall() {
		return recall;
	}

	public Double getFscore() {
		return fscore;
	}

	@Override
	public String toString() {
		return String.format("[%.3f, %.3f, %.3f, %.3f]\n%s", getAccuracy(), getRecall(), getPrecision(), getFscore(), getMatrices());
	}

}
