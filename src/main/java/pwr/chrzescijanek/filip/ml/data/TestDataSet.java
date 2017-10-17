package pwr.chrzescijanek.filip.ml.data;

import pwr.chrzescijanek.filip.ml.classifier.ConfusionMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TestDataSet {

	private final List<TestRecord> records;
	private final DiscreteAttribute clazz;
	
	public TestDataSet(List<TestRecord> records, DiscreteAttribute clazz) {
		this.records = Collections.unmodifiableList(Objects.requireNonNull(records));
		this.clazz = Objects.requireNonNull(clazz);
	}

	public List<TestRecord> getRecords() {
		return records;
	}
	
	public DiscreteAttribute getClazz() {
		return clazz;
	}

	public ConfusionMatrix getConfusionMatrix() {
		final List<ConfusionMatrix> matrices = new ArrayList<>();
		for (final String s : getClazz().getValues()) {
			Integer truePositives = 0;
			Integer trueNegatives = 0;
			Integer falsePositives = 0;
			Integer falseNegatives = 0;

			for (final TestRecord tr : getRecords()) {
				if (tr.getClazz().equals(s) && tr.getClazz().equals(tr.getAssignedClazz())) {
					truePositives++;
				} else if (tr.getClazz().equals(s)) {
					falseNegatives++;
				} else if (tr.getAssignedClazz().equals(s)) {
					falsePositives++;
				} else {
					trueNegatives++;
				}
			}

			matrices.add(new ConfusionMatrix(truePositives, trueNegatives, falsePositives, falseNegatives));
		}
		return new ConfusionMatrix(matrices);
	}

}
