package pwr.chrzescijanek.filip.ml.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataSet {

	private final List<? extends Record> records;
	private final DataClassAttribute clazz;
	private final Integer noOfAttributes;

	public DataSet(final List<? extends Record> records, final DataClassAttribute clazz, final Integer noOfAttributes) {
		this.records = Collections.unmodifiableList(Objects.requireNonNull(records));
		this.clazz = Objects.requireNonNull(clazz);
		this.noOfAttributes = Objects.requireNonNull(noOfAttributes);
	}

	public List<? extends Record> getRecords() {
		return records;
	}

	public DataClassAttribute getClazz() {
		return clazz;
	}

	public Integer getNoOfAttributes() {
		return noOfAttributes;
	}

	public Iterator<Fold> foldIterator(final Integer folds) {
		return new FoldIterator(folds);
	}

	private class FoldIterator implements Iterator<Fold> {

		private final Integer folds;
		private Integer position;

		FoldIterator(final Integer folds) {
			this.folds = Objects.requireNonNull(folds);
			this.position = 0;
		}

		@Override
		public boolean hasNext() {
			return position < folds;
		}

		@Override
		public Fold next() {
			final Integer foldSize = (getRecords().size() + folds - 1) / folds;
			final List<Record> copy = new ArrayList<>(getRecords());
			final List<Record> test = new ArrayList<>(copy.subList(position * foldSize, (position + 1) * foldSize));
			copy.removeAll(test);
			final List<TestRecord> testSet = test.stream().map(r -> new TestRecord(r.getAttributes(), r.getClazz())).collect(Collectors.toList());
			final Fold fold = new Fold(new DataSet(copy, getClazz(), getNoOfAttributes()), new TestDataSet(testSet, getClazz(), getNoOfAttributes()));
			position++;
			return fold;
		}
	}

}
