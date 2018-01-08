package pwr.chrzescijanek.filip.ml.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.Fold;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class BoostingEvaluator implements Evaluator {
	
	private final List<AbstractClassifier> classifiers;
	
	public BoostingEvaluator(List<AbstractClassifier> classifiers) {
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
			List<Map<String, Double>> votes = new ArrayList<>();
			List<TestRecord> records = f.getTestDataSet().getRecords();
			
			records.forEach(r -> votes.add(new HashMap<>()));
			records.forEach(r -> r.setWeight(1.0 / records.size()));
			
			for (AbstractClassifier c : getClassifiers()) {
				c.train(f.getTrainingDataSet()).test(f.getTestDataSet());
				double sum = records.stream().mapToDouble(TestRecord::getWeight).sum();
				double error = records.stream()
						.filter(r -> !r.getAssignedClazz().equals(r.getClazz()))
						.mapToDouble(TestRecord::getWeight)
						.sum() / sum;
				double weight = Math.log((1.0 - error) / error) / 2.0;
				
				for (int i = 0; i < records.size(); i++) {
					String assignedClazz = records.get(i).getAssignedClazz();
					votes.get(i).put(assignedClazz, votes.get(i).getOrDefault(assignedClazz, 0.0) + weight);
				}

				records.stream()
					.filter(r -> r.getAssignedClazz().equals(r.getClazz()))
					.forEach(r -> r.setWeight(r.getWeight() * Math.pow(Math.E, -weight)));
				records.stream()
					.filter(r -> !r.getAssignedClazz().equals(r.getClazz()))
					.forEach(r -> r.setWeight(r.getWeight() * Math.pow(Math.E, weight)));
				double newSum = records.stream().mapToDouble(TestRecord::getWeight).sum();
				records.forEach(r -> r.setWeight(r.getWeight() / newSum));
				
				records.forEach(r -> r.setAssignedClazz(null));
			}
			
			for (int i = 0; i < records.size(); i++) {
				records.get(i).setAssignedClazz(votes.get(i).entrySet()
						.stream()
						.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
						.get().getKey());
			}
			
			evals.add(Eval.createFromDataSet(f.getTestDataSet()));
		}
		return Eval.createAverage(evals);
	}

}