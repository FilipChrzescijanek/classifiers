package pwr.chrzescijanek.filip.ml.classifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.Fold;

public class MonoEvaluator implements Evaluator {
	
	private final AbstractClassifier classifier;
	
	public MonoEvaluator(AbstractClassifier classifier) {
		this.classifier = classifier;
	}

	public AbstractClassifier getClassifier() {
		return classifier;
	}

	@Override
	public Eval crossValidate(final DataSet ds, final Integer folds) {
		final List<Eval> evals = new ArrayList<>();
		final Iterator<Fold> it = getClassifier().normalize(Objects.requireNonNull(ds)).foldIterator(Objects.requireNonNull(folds));
		while(it.hasNext()) {
			final Fold f = it.next();
			evals.add(getClassifier().train(f.getTrainingDataSet()).test(f.getTestDataSet()));
		}
		return Eval.createAverage(evals);
	}
	
}
