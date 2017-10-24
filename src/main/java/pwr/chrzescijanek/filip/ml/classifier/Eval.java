package pwr.chrzescijanek.filip.ml.classifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Eval {

	private final double[][] classMatrix;
	private final List<String> classValues;
	
	private final Double accuracy;
	private final Double precision;
	private final Double recall;
	private final Double fscore;

	private Eval(final Double accuracy, final Double precision, final Double recall, final Double fscore, 
			final double[][] classMatrix, final List<String> classValues) {
		this.accuracy    = accuracy;
		this.precision   = precision;
		this.recall      = recall;
		this.fscore      = fscore;
		this.classMatrix = classMatrix;
		this.classValues = classValues;
	}

	public static Eval createFromMatrices(final List<ConfusionMatrix> matrices, final double[][] classMatrix, final List<String> classValues) {
		Double accuracy  = 0.0;
		Double precision = 0.0;
		Double recall    = 0.0;
		Double fscore    = 0.0;

		for (final ConfusionMatrix cm : Objects.requireNonNull(matrices)) {
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
		return new Eval(accuracy / size, precision / size, recall / size, fscore / size, classMatrix, classValues);
	}

	public static Eval createAverage(final List<Eval> evals) {
		final Double accuracy  = evals.stream().mapToDouble(Eval::getAccuracy).average().orElse(0.0);
		final Double precision = evals.stream().mapToDouble(Eval::getPrecision).average().orElse(0.0);
		final Double recall    = evals.stream().mapToDouble(Eval::getRecall).average().orElse(0.0);
		final Double fscore    = evals.stream().mapToDouble(Eval::getFscore).average().orElse(0.0);
		int length = evals.get(0).getClassMatrix().length;
		final double[][] classMatrix = new double[length][length];
		for (Eval e : evals) {
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < length; j++) {
					classMatrix[i][j] += e.getClassMatrix()[i][j];
				}
			}
		}
		return new Eval(accuracy, precision, recall, fscore, classMatrix, evals.get(0).getClassValues());
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
	
	public double[][] getClassMatrix() {
		return classMatrix;
	}

	public List<String> getClassValues() {
		return classValues;
	}

	@Override
	public String toString() {
		return String.format("[%.3f, %.3f, %.3f, %.3f]\n%s\n%s", 
				getAccuracy(), getRecall(), getPrecision(), getFscore(), Arrays.deepToString(getClassMatrix()), getClassValues());
	}

}
