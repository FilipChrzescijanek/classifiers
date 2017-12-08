package pwr.chrzescijanek.filip.ml.classifier;

import org.junit.Test;

import pwr.chrzescijanek.filip.ml.classifier.bayes.Bayes;
import pwr.chrzescijanek.filip.ml.classifier.ila.InductiveLearning;
import pwr.chrzescijanek.filip.ml.classifier.knn.KNearestNeighbors;
import pwr.chrzescijanek.filip.ml.data.DataSource;
import pwr.chrzescijanek.filip.ml.data.discretizer.CardinalityDiscretizer;
import pwr.chrzescijanek.filip.ml.data.discretizer.EntropyDiscretizer;
import pwr.chrzescijanek.filip.ml.data.discretizer.RangeDiscretizer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClassifierTest {

	@Test
	public void bayes() throws Exception {
		testCrossValidation(new Bayes(), false, false);
	}

	@Test	
	public void knn() throws Exception {
		testCrossValidation(new KNearestNeighbors(5, (r1, r2) -> {
			Double distance = 0.0;
			List<Double> firstValues  = r1.getRawValues().stream().map(o -> (Double) o).collect(Collectors.toList());
			List<Double> secondValues = r2.getRawValues().stream().map(o -> (Double) o).collect(Collectors.toList());
			for (int i = 0; i < firstValues.size(); i++) {
				distance += Math.pow(firstValues.get(i) - secondValues.get(i), 2);
			}
			return Math.sqrt(distance);
		}), false, true);
	}

	@Test
	public void ila() throws Exception {
		testCrossValidation(new InductiveLearning(), true, false);
	}

	private void testCrossValidation(final Classifier c, final Boolean onlyDiscrete, final Boolean onlyContinuous) throws IOException {
		final Integer classIndex = 0;
		final Integer folds      = 10;
		final String dataSet     = getClass().getResource("/iris.csv").getPath();

		if (!onlyDiscrete) {
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(), folds));
		}
		if (!onlyContinuous) {
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new RangeDiscretizer()), folds));
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new CardinalityDiscretizer()), folds));
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new EntropyDiscretizer()), folds));
		}
	}

	private void print(final Eval e) {
		System.out.println(Objects.requireNonNull(e));
	}

}