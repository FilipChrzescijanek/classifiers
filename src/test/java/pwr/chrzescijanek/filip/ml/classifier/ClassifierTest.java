package pwr.chrzescijanek.filip.ml.classifier;

import org.junit.Test;
import pwr.chrzescijanek.filip.ml.data.DataSource;
import pwr.chrzescijanek.filip.ml.data.RangeDiscretizer;

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
		final String dataSet = "/iris.csv";
		final Integer CLASS_INDEX = 4;
		final Integer FOLDS = 10;

		checkResults(c.crossValidate(new DataSource(dataSet, CLASS_INDEX).asDataSet(new RangeDiscretizer()), FOLDS));
	}

	private void checkResults(final Eval e) {
		assertNotNull(e);
		System.out.println(e.getAccuracy() + ", "
		                   + e.getRecall() + ", "
		                   + e.getPrecision() + ", "
		                   + e.getFscore());
	}

}