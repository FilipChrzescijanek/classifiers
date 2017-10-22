package pwr.chrzescijanek.filip.ml;

import pwr.chrzescijanek.filip.ml.classifier.Classifier;
import pwr.chrzescijanek.filip.ml.classifier.bayes.Bayes;
import pwr.chrzescijanek.filip.ml.data.DataSource;
import pwr.chrzescijanek.filip.ml.data.discretizer.CardinalityDiscretizer;
import pwr.chrzescijanek.filip.ml.data.discretizer.EntropyDiscretizer;
import pwr.chrzescijanek.filip.ml.data.discretizer.RangeDiscretizer;

public class Main {

	public static void main(final String... args) throws Exception {
		final Classifier c = new Bayes();
		for (final String dataSet : args) {
			final Integer classIndex = 0;
			final Integer folds      = 10;

			System.out.println(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(), folds));
			System.out.println(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new RangeDiscretizer()), folds));
			System.out.println(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new CardinalityDiscretizer()), folds));
			System.out.println(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new EntropyDiscretizer()), folds));
		}
	}

}
