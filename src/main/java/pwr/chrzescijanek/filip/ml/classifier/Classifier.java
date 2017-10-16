package pwr.chrzescijanek.filip.ml.classifier;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.TestDataSet;

public interface Classifier {

	Classifier train(DataSet tds);

	Eval test(TestDataSet tds);

	Eval crossValidate(DataSet ds, Integer folds);

}
