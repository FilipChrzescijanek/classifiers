package pwr.chrzescijanek.filip.ml.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.Fold;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class PoliEvaluator implements Evaluator {
	
	private final List<AbstractClassifier> classifiers;
	
	public PoliEvaluator(List<AbstractClassifier> classifiers) {
		this.classifiers = Collections.unmodifiableList(Objects.requireNonNull(classifiers));
		if (classifiers.isEmpty()) {
			throw new IllegalArgumentException("There has to be at least one classifier!");
		}
	}

	public List<AbstractClassifier> getClassifiers() {
		return classifiers;
	}

	@Override
	public Eval crossValidate(final DataSet ds, final Integer folds) {
		final List<Eval> evals = new ArrayList<>();
		final Iterator<Fold> it = Objects.requireNonNull(ds).foldIterator(Objects.requireNonNull(folds));
		while(it.hasNext()) {
			final Fold f = it.next();
			List<List<String>> votes = new ArrayList<>();
			List<TestRecord> records = f.getTestDataSet().getRecords();
			records.forEach(r -> votes.add(new ArrayList<>()));
			for (AbstractClassifier c : getClassifiers()) {
				c.train(f.getTrainingDataSet()).test(f.getTestDataSet());
				for (int i = 0; i < records.size(); i++) {
					String assignedClazz = records.get(i).getAssignedClazz();
					if (!assignedClazz.equals(Classifier.NULL_CLASS)) {
						votes.get(i).add(assignedClazz);
						records.get(i).setAssignedClazz(null);
					}
				}
			}
			for (int i = 0; i < records.size(); i++) {
				records.get(i).setAssignedClazz(votes.get(i).stream()
						.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
						.entrySet()
						.stream()
						.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
						.get().getKey());
			}
			evals.add(Eval.createFromDataSet(f.getTestDataSet()));
		}
		return Eval.createAverage(evals);
	}

}