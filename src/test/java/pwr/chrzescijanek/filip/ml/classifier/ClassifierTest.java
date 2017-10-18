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

import static org.junit.Assert.*;

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

		checkResults(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(), folds));
		checkResults(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new RangeDiscretizer()), folds));
		checkResults(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new CardinalityDiscretizer()), folds));
		checkResults(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new EntropyDiscretizer()), folds));
	}

	private void checkResults(final Eval e) {
		assertNotNull(e);
		System.out.println(e.getAccuracy() + ", "
		                   + e.getRecall() + ", "
		                   + e.getPrecision() + ", "
		                   + e.getFscore());
	}

}