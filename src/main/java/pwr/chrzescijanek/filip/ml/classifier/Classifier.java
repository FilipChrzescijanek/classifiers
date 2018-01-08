package pwr.chrzescijanek.filip.ml.classifier;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.TestDataSet;

public interface Classifier {
	
	static final String NULL_CLASS = "null";

	Classifier train(DataSet tds);

	Eval test(TestDataSet tds);

}
