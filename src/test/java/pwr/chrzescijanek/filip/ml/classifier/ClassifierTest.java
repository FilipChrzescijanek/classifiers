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
import java.util.Objects;

public class ClassifierTest {

	@Test
	public void bayes() throws Exception {
		testCrossValidation(new Bayes(), false);
	}

	@Test	
	public void knn() throws Exception {
		testCrossValidation(new KNearestNeighbors(), false);
	}

	@Test
	public void ila() throws Exception {
		testCrossValidation(new InductiveLearning(), true);
	}

	private void testCrossValidation(final Classifier c, final Boolean onlyDiscrete) throws IOException {
		final Integer classIndex = 0;
		final Integer folds      = 10;
		final String dataSet     = getClass().getResource("/iris.csv").getPath();

		if (!onlyDiscrete) {
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(), folds));
		}
		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new RangeDiscretizer()), folds));
		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new CardinalityDiscretizer()), folds));
		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new EntropyDiscretizer()), folds));
	}

	private void print(final Eval e) {
		System.out.println(Objects.requireNonNull(e));
	}

}