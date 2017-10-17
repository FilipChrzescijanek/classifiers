package pwr.chrzescijanek.filip.ml.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

public class DataSet {

	private final List<? extends Record> records;
	private final List<? extends DataAttribute> attributes;
	private final DiscreteAttribute clazz;

	public DataSet(final List<? extends Record> records, List<String> attributeNames, List<String> attributeTypes, 
			final String className) {
		final List<String> classValues = records.stream().map(r -> r.getClazz()).collect(Collectors.toList());
		this.clazz = new DiscreteAttribute(className, classValues);
		
		List<DataAttribute> attributes = new ArrayList<>();
		
		for (int i = 0; i < attributeNames.size(); i++) {
			final int index = i;
			if (attributeTypes.get(i).equals("c")) {
				final List<Double> values = records.stream().map(r -> (Double) r.getValues().get(index)).collect(Collectors.toList());      
				attributes.add(new ContinuousAttribute(attributeNames.get(i), values));
			} else if (attributeTypes.get(i).equals("d")) {
				final List<String> values = records.stream().map(r -> (String) r.getValues().get(index)).collect(Collectors.toList());
				attributes.add(new DiscreteAttribute(attributeNames.get(i), values));
			} else {
				throw new UnknownFormatConversionException("Unknown attribute type" + attributeTypes.get(i));
			}
		}
		
		this.records = Collections.unmodifiableList(Objects.requireNonNull(records));
		this.attributes = Collections.unmodifiableList(Objects.requireNonNull(attributes));
	}

	public List<? extends Record> getRecords() {
		return records;
	}

	public List<? extends DataAttribute> getAttributes() {
		return attributes;
	}
	
	public List<Double> getValues(ContinuousAttribute attribute) {
		final int index = attributes.indexOf(attribute);
		return records.stream().map(r -> (Double) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public List<Double> getValuesForClass(ContinuousAttribute attribute, String clazz) {
		final int index = attributes.indexOf(attribute);
		return records.stream().filter(r -> r.getClazz().equals(clazz)).map(r -> (Double) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public List<String> getValues(DiscreteAttribute attribute) {
		final int index = attributes.indexOf(attribute);
		return records.stream().map(r -> (String) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public List<String> getValuesForClass(DiscreteAttribute attribute, String clazz) {
		final int index = attributes.indexOf(attribute);
		return records.stream().filter(r -> r.getClazz().equals(clazz)).map(r -> (String) r.getValues().get(index)).collect(Collectors.toList());
	}

	public DiscreteAttribute getClazz() {
		return clazz;
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
			final List<TestRecord> testSet = test.stream().map(r -> new TestRecord(r.getValues(), r.getClazz())).collect(Collectors.toList());
			final Fold fold = new Fold(new DataSet(copy, 
					getAttributes().stream().map(a -> a.getName()).collect(Collectors.toList()), 
					getAttributes().stream().map(a -> a.isDiscrete() ? "d" : "c").collect(Collectors.toList()), 
					getClazz().getName()), new TestDataSet(testSet, getClazz()));
			position++;
			return fold;
		}
	}

}
