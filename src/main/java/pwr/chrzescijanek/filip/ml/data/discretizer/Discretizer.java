package pwr.chrzescijanek.filip.ml.data.discretizer;

import pwr.chrzescijanek.filip.ml.data.DataSet;

public interface Discretizer {

	DataSet discretize(DataSet ds);
	
}
