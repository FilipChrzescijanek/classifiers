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
		testCrossValidation(new Bayes());
	}

	@Test
	public void knn() throws Exception {
		testCrossValidation(new KNearestNeighbors());
	}

	@Test
	public void ila() throws Exception {
		testCrossValidation(new InductiveLearning());
	}

	private void testCrossValidation(final Classifier c) throws IOException {
		final Integer classIndex = 4;
		final Integer folds      = 10;
		final String dataSet     = "/iris.csv";

		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(), folds));
		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new RangeDiscretizer()), folds));
		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new CardinalityDiscretizer()), folds));
		print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new EntropyDiscretizer()), folds));
	}

	private void print(final Eval e) {
		System.out.println(Objects.requireNonNull(e));
	}

}