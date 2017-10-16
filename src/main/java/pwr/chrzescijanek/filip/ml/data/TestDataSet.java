package pwr.chrzescijanek.filip.ml.data;

import pwr.chrzescijanek.filip.ml.classifier.ConfusionMatrix;

import java.util.ArrayList;
import java.util.List;

public class TestDataSet extends DataSet {

	public TestDataSet(final List<TestRecord> records, final DataClassAttribute clazz, final Integer noOfAttributes) {
		super(records, clazz, noOfAttributes);
	}

	public List<TestRecord> getTestRecords() {
		return (List<TestRecord>) getRecords();
	}

	public ConfusionMatrix getConfusionMatrix() {
		final List<ConfusionMatrix> matrices = new ArrayList<>();
		for (final String s : getClazz().getValues()) {
			Integer truePositives = 0;
			Integer trueNegatives = 0;
			Integer falsePositives = 0;
			Integer falseNegatives = 0;

			for (final TestRecord tr : getTestRecords()) {
				if (tr.getClazz().getValue().equals(s) && tr.getClazz().equals(tr.getAssignedClazz())) {
					truePositives++;
				} else if (tr.getClazz().getValue().equals(s)) {
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
