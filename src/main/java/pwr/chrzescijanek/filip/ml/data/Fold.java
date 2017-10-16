package pwr.chrzescijanek.filip.ml.data;

import java.util.Objects;

public class Fold {

	private final DataSet trainingDataSet;

	private final TestDataSet testDataSet;

	public Fold(final DataSet trainingDataSet, final TestDataSet testDataSet) {
		this.trainingDataSet = Objects.requireNonNull(trainingDataSet);
		this.testDataSet = Objects.requireNonNull(testDataSet);
	}

	public DataSet getTrainingDataSet() {
		return trainingDataSet;
	}

	public TestDataSet getTestDataSet() {
		return testDataSet;
	}

}
