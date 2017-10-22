package pwr.chrzescijanek.filip.ml.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Eval {

	private final List<ConfusionMatrix> matrices;

	private final Double accuracy;
	private final Double precision;
	private final Double recall;
	private final Double fscore;

	public Eval(final ConfusionMatrix confusionMatrix) {
		this.matrices    = Arrays.asList(Objects.requireNonNull(confusionMatrix));
		final Integer tp = confusionMatrix.getTruePositives();
		final Integer tn = confusionMatrix.getTrueNegatives();
		final Integer fp = confusionMatrix.getFalsePositives();
		final Integer fn = confusionMatrix.getFalseNegatives();

		this.recall    = (tp + fn) != 0 ? (1.0 * tp) / (tp + fn) : 0.0;
		this.precision = (tp + fp) != 0 ? (1.0 * tp) / (tp + fp) : 0.0;
		this.accuracy  = (tp + tn + fp + fn) != 0 ? (1.0 * (tp + tn)) / (tp + tn + fp + fn) : 0.0;
		this.fscore    = 2 / ((1 / this.recall) + (1 / this.precision));
	}

	public Eval(final List<Eval> evals) {
		this.matrices  = Objects.requireNonNull(evals).stream().flatMap(e -> e.getMatrices().stream()).collect(Collectors.toList());
		this.accuracy  = evals.stream().mapToDouble(Eval::getAccuracy).average().orElse(0.0);
		this.precision = evals.stream().mapToDouble(Eval::getPrecision).average().orElse(0.0);
		this.recall    = evals.stream().mapToDouble(Eval::getRecall).average().orElse(0.0);
		this.fscore    = evals.stream().mapToDouble(Eval::getFscore).average().orElse(0.0);
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
		return getAccuracy() + ", " + getRecall() + ", " + getPrecision() + ", " + getFscore()
		       + "\n" + getMatrices();
	}

}
