package pwr.chrzescijanek.filip.ml.data;

import pwr.chrzescijanek.filip.ml.classifier.Classifier;
import pwr.chrzescijanek.filip.ml.classifier.ConfusionMatrix;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TestDataSet {

	private final List<TestRecord> records;
	private final List<String> classValues;
	
	public TestDataSet(final List<TestRecord> records, final List<String> classValues) {
		this.records     = Collections.unmodifiableList(Objects.requireNonNull(records));
		this.classValues = Collections.unmodifiableList(Objects.requireNonNull(classValues));
	}

	public List<TestRecord> getRecords() {
		return records;
	}
	
	public List<String> getClassValues() {
		return classValues;
	}
	
	public double[][] getClassMatrix() {
		int size = classValues.size();
		double[][] classMatrix = new double[size][size];
		for (final TestRecord tr : getRecords()) {
			String assignedClass = tr.getAssignedClazz();
			String trueClass     = tr.getClazz();
			if (!Classifier.NULL_CLASS.equals(assignedClass)) {
				classMatrix[getClassValues().indexOf(assignedClass)][getClassValues().indexOf(trueClass)] += 1.0;
			}
		}
		return classMatrix;
	}

	public List<ConfusionMatrix> getConfusionMatrices() {
		final List<ConfusionMatrix> matrices = new ArrayList<>();
		for (final String s : getClassValues()) {
			Integer truePositives  = 0;
			Integer trueNegatives  = 0;
			Integer falsePositives = 0;
			Integer falseNegatives = 0;

			for (final TestRecord tr : getRecords()) {
//				if (!Classifier.NULL_CLASS.equals(tr.getAssignedClazz())) {
					if (tr.getClazz().equals(s) && tr.getClazz().equals(tr.getAssignedClazz())) {
						truePositives++;
					} else if (tr.getClazz().equals(s)) {
						falseNegatives++;
					} else if (tr.getAssignedClazz().equals(s)) {
						falsePositives++;
					} else {
						trueNegatives++;
					}
//				}
			}

			matrices.add(new ConfusionMatrix(truePositives, trueNegatives, falsePositives, falseNegatives));
		}
		return matrices;
	}

}
