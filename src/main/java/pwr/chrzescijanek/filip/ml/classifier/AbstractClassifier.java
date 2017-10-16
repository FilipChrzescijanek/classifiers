package pwr.chrzescijanek.filip.ml.classifier;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.Fold;
import pwr.chrzescijanek.filip.ml.data.TestDataSet;
import pwr.chrzescijanek.filip.ml.data.TestRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
		tds.getTestRecords().forEach(this::assignClass);
		return new Eval(tds.getConfusionMatrix());
	}

	protected abstract void assignClass(TestRecord tr);

	@Override
	public Eval crossValidate(final DataSet ds, final Integer folds) {
		Objects.requireNonNull(ds);
		Objects.requireNonNull(folds);
		final List<Eval> evals = new ArrayList<>();
		final Iterator<Fold> it = ds.foldIterator(folds);
		while(it.hasNext()) {
			final Fold f = it.next();
			evals.add(train(f.getTrainingDataSet()).test(f.getTestDataSet()));
		}
		return new Eval(evals);
	}

}