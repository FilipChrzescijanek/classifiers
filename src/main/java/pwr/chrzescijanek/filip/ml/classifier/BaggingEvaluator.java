package pwr.chrzescijanek.filip.ml.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.DataType;
import pwr.chrzescijanek.filip.ml.data.Fold;
import pwr.chrzescijanek.filip.ml.data.record.Record;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class BaggingEvaluator implements Evaluator {
	
	private final List<AbstractClassifier> classifiers;
	
	public BaggingEvaluator(List<AbstractClassifier> classifiers) {
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
				c.train(getRandomSample(f)).test(f.getTestDataSet());
				for (int i = 0; i < records.size(); i++) {
					String assignedClazz = records.get(i).getAssignedClazz();
					if (!assignedClazz.equals(Classifier.NULL_CLASS)) {
						votes.get(i).add(assignedClazz);
						records.get(i).setAssignedClazz(null);
					}				}
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

	private DataSet getRandomSample(final Fold f) {
		DataSet ids = f.getTrainingDataSet();
		final List<String> attributeNames = ids.getAttributeNames();
		final List<String> attributeTypes = ids.getAttributes()
				.stream()
				.map(a -> a.isDiscrete() ? DataType.D.toString().toLowerCase() : DataType.C.toString().toLowerCase())
				.collect(Collectors.toList());
		final List<Record> copy = new ArrayList<>();
		List<Record> records = ids.getRecords();
		for (int i = 0; i < records.size(); i++) {
			copy.add(records.get((int) (Math.random() * records.size())));
		}
		final DataSet nds = new DataSet(copy, attributeNames, attributeTypes, ids.getClazz().getName());
		return nds;
	}

}