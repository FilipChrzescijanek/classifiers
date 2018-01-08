package pwr.chrzescijanek.filip.ml.classifier;

import pwr.chrzescijanek.filip.ml.data.DataSet;

public interface Evaluator {

	Eval crossValidate(DataSet ds, Integer folds);

}
