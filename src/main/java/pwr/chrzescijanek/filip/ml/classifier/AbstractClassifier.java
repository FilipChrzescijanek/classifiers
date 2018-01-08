package pwr.chrzescijanek.filip.ml.classifier;

import java.util.Objects;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.TestDataSet;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public abstract class AbstractClassifier implements Classifier {

	@Override
	public Classifier train(final DataSet tds) {
		Objects.requireNonNull(tds);
		buildModel(tds);
		return this;
	}

	protected abstract void buildModel(final DataSet tds);

	@Override
	public Eval test(final TestDataSet tds) {
		Objects.requireNonNull(tds);
		tds.getRecords().forEach(this::assignClass);
		return Eval.createFromDataSet(tds);
	}

	protected abstract void assignClass(TestRecord tr);
	
	protected DataSet normalize(final DataSet ds) {
		return ds;
	}

}
