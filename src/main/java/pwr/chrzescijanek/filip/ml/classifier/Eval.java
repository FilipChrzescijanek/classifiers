package pwr.chrzescijanek.filip.ml.classifier;

import java.util.List;
import java.util.Objects;

public class Eval {

	private final Double accuracy;
	private final Double precision;
	private final Double recall;
	private final Double fscore;

	public Eval(final ConfusionMatrix cm) {
		Objects.requireNonNull(cm);
		final Integer tp = cm.getTruePositives();
		final Integer tn = cm.getTrueNegatives();
		final Integer fp = cm.getFalsePositives();
		final Integer fn = cm.getFalseNegatives();

		this.recall    = (tp + fn) != 0 ? (1.0 * tp) / (tp + fn) : 0.0;
		this.precision = (tp + fp) != 0 ? (1.0 * tp) / (tp + fp) : 0.0;
		this.accuracy  = (tp + tn + fp + fn) != 0 ? (1.0 * (tp + tn)) / (tp + tn + fp + fn) : 0.0;
		this.fscore    = 2 / ((1 / this.recall) + (1 / this.precision));
	}

	public Eval(final List<Eval> evals) {
		Objects.requireNonNull(evals);
		this.accuracy  = evals.stream().mapToDouble(Eval::getAccuracy).average().orElse(0.0);
		this.precision = evals.stream().mapToDouble(Eval::getPrecision).average().orElse(0.0);
		this.recall    = evals.stream().mapToDouble(Eval::getRecall).average().orElse(0.0);
		this.fscore    = evals.stream().mapToDouble(Eval::getFscore).average().orElse(0.0);
	}

	public Double getRecall() {
		return recall;
	}

	public Double getPrecision() {
		return precision;
	}

	public Double getAccuracy() {
		return accuracy;
	}

	public Double getFscore() {
		return fscore;
	}
}
