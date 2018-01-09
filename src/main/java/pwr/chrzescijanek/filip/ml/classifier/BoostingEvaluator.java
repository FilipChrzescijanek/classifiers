package pwr.chrzescijanek.filip.ml.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.DataType;
import pwr.chrzescijanek.filip.ml.data.Fold;
import pwr.chrzescijanek.filip.ml.data.TestDataSet;
import pwr.chrzescijanek.filip.ml.data.record.Record;
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
			final TestDataSet trainingDataSet = convertToTestDataSet(f);
			
			List<TestRecord> testRecords = f.getTestDataSet().getRecords();
			List<TestRecord> trainingRecords = trainingDataSet.getRecords();
			
			trainingRecords.forEach(r -> r.setWeight(1.0 / trainingRecords.size()));
			testRecords.forEach(r -> votes.add(new HashMap<>()));
			
			for (AbstractClassifier c : getClassifiers()) {
				Classifier classifier = c.train(getRandomSample(f, trainingRecords)); 
				classifier.test(trainingDataSet);
				classifier.test(f.getTestDataSet());
				
				double sum = trainingRecords.stream().mapToDouble(TestRecord::getWeight).sum();
				double error = trainingRecords.stream()
						.filter(r -> !r.getAssignedClazz().equals(r.getClazz()))
						.mapToDouble(TestRecord::getWeight)
						.sum() / sum;
				double weight = Math.log((1.0 - error) / error) / 2.0;
				
				for (int i = 0; i < testRecords.size(); i++) {
					String assignedClazz = testRecords.get(i).getAssignedClazz();
					votes.get(i).put(assignedClazz, votes.get(i).getOrDefault(assignedClazz, 0.0) + weight);
				}

				trainingRecords.stream()
					.filter(r -> r.getAssignedClazz().equals(r.getClazz()))
					.forEach(r -> r.setWeight(r.getWeight() * Math.pow(Math.E, -weight)));
				trainingRecords.stream()
					.filter(r -> !r.getAssignedClazz().equals(r.getClazz()))
					.forEach(r -> r.setWeight(r.getWeight() * Math.pow(Math.E, weight)));
				double newSum = trainingRecords.stream().mapToDouble(TestRecord::getWeight).sum();
				trainingRecords.forEach(r -> r.setWeight(r.getWeight() / newSum));

				trainingRecords.forEach(r -> r.setAssignedClazz(null));
				testRecords.forEach(r -> r.setAssignedClazz(null));
			}
			
			for (int i = 0; i < testRecords.size(); i++) {
				testRecords.get(i).setAssignedClazz(votes.get(i).entrySet()
						.stream()
						.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
						.get().getKey());
			}
			
			evals.add(Eval.createFromDataSet(f.getTestDataSet()));
		}
		return Eval.createAverage(evals);
	}

	private TestDataSet convertToTestDataSet(final Fold f) {
		final List<String> attributeNames = f.getTrainingDataSet().getAttributeNames();
		final List<Record> testSublist = f.getTrainingDataSet().getRecords();
		final List<TestRecord> testSet = testSublist.stream().map(r -> new TestRecord(r.getRawValues(), attributeNames, r.getClazz())).collect(Collectors.toList());
		final TestDataSet testDataSet = new TestDataSet(testSet, f.getTrainingDataSet().getClazz().getValues());
		return testDataSet;
	}

	private DataSet getRandomSample(final Fold f, List<TestRecord> trainingRecords) {
		DataSet ids = f.getTrainingDataSet();
		final List<String> attributeNames = ids.getAttributeNames();
		final List<String> attributeTypes = ids.getAttributes()
				.stream()
				.map(a -> a.isDiscrete() ? DataType.D.toString().toLowerCase() : DataType.C.toString().toLowerCase())
				.collect(Collectors.toList());
		final List<Record> copy = new ArrayList<>();
		for (int i = 0; i < trainingRecords.size(); i++) {
			double random = Math.random();
			double temp = 0.0;
			TestRecord current = null;
			for (int j = 0; j < trainingRecords.size(); j++) {
				current = trainingRecords.get(j);
				temp += current.getWeight();
				if (random < temp) {
					break;
				}
			}
			copy.add(new Record(current.getRawValues(), attributeNames, current.getClazz()));
		}
		final DataSet nds = new DataSet(copy, attributeNames, attributeTypes, ids.getClazz().getName());
		return nds;
	}

}