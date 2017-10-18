package pwr.chrzescijanek.filip.ml.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.attribute.ContinuousAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DataAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DiscreteAttribute;
import pwr.chrzescijanek.filip.ml.data.record.Record;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class DataSet {

	private final List<Record> records;
	private final List<? extends DataAttribute> attributes;
	private final DiscreteAttribute clazz;

	public DataSet(final List<Record> records, List<String> attributeNames, List<String> attributeTypes, 
			final String className) {
		final List<String> classValues = records.stream().map(r -> r.getClazz()).collect(Collectors.toList());
		
		this.clazz = new DiscreteAttribute(className, classValues);		
		this.records = Collections.unmodifiableList(Objects.requireNonNull(records));
		this.attributes = Collections.unmodifiableList(Objects.requireNonNull(createAttributes(records, attributeNames, attributeTypes)));
	}

	public List<Record> getRecords() {
		return records;
	}

	public List<? extends DataAttribute> getAttributes() {
		return attributes;
	}
	
	public List<String> getAttributeNames() {
		return getAttributes().stream().map(a -> a.getName()).collect(Collectors.toList());
	}
	
	public DataAttribute getAttributeByName(String name) {
		return getAttributes().stream().filter(a -> a.getName().equals(name)).findFirst().get();
	}
	
	public List<Double> getValues(ContinuousAttribute attribute) {
		final int index = attributes.indexOf(attribute);
		return getRecords().stream().map(r -> (Double) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public List<Double> getValuesForClass(ContinuousAttribute attribute, String clazz) {
		final int index = attributes.indexOf(attribute);
		return getRecords().stream().filter(r -> r.getClazz().equals(clazz)).map(r -> (Double) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public List<String> getValues(DiscreteAttribute attribute) {
		final int index = attributes.indexOf(attribute);
		return getRecords().stream().map(r -> (String) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public List<String> getValuesForClass(DiscreteAttribute attribute, String clazz) {
		final int index = attributes.indexOf(attribute);
		return getRecords().stream().filter(r -> r.getClazz().equals(clazz)).map(r -> (String) r.getValues().get(index)).collect(Collectors.toList());
	}
	
	public Double getMean(ContinuousAttribute attribute, String clazz) {
		return getValuesForClass(attribute, clazz).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
	}
	
	public Double getStdDev(ContinuousAttribute attribute, String clazz) {
		List<Double> values = getValuesForClass(attribute, clazz);
		Double mean         = getMean(attribute, clazz);
		
		return Math.sqrt(values.stream().reduce(0.0, (a, b) -> a + Math.pow(b - mean, 2)) / (values.size() - 1));
	}

	public DiscreteAttribute getClazz() {
		return clazz;
	}

	public Iterator<Fold> foldIterator(final Integer folds) {
		return new FoldIterator(folds);
	}

	private List<DataAttribute> createAttributes(final List<Record> records, List<String> attributeNames,
			List<String> attributeTypes) {
		List<DataAttribute> attributes = new ArrayList<>();
		
		for (int i = 0; i < attributeNames.size(); i++) {
			final int index = i;
			if ("c".equals(attributeTypes.get(i))) {
				final List<Double> values = records.stream().map(r -> (Double) r.getValues().get(index)).collect(Collectors.toList());      
				attributes.add(new ContinuousAttribute(attributeNames.get(i), values));
			} else if ("d".equals(attributeTypes.get(i))) {
				final List<String> values = records.stream().map(r -> (String) r.getValues().get(index)).collect(Collectors.toList());
				attributes.add(new DiscreteAttribute(attributeNames.get(i), values));
			} else {
				throw new UnknownFormatConversionException("Unknown attribute type" + attributeTypes.get(i));
			}
		}
	
		return attributes;
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
			
			List<String> attributeNames = getAttributeNames();
			List<String> attributeTypes = getAttributes().stream().map(a -> a.isDiscrete() ? "d" : "c").collect(Collectors.toList());
			
			final List<Record> copy        = new ArrayList<>(getRecords());
			final List<Record> testSublist = new ArrayList<>(copy.subList(position * foldSize, (position + 1) * foldSize));
			
			copy.removeAll(testSublist);
			
			final List<TestRecord> testSet = testSublist.stream().map(r -> new TestRecord(r.getValues(), attributeNames, r.getClazz())).collect(Collectors.toList());
			
			DataSet trainingDataSet = new DataSet(copy, attributeNames, attributeTypes, getClazz().getName());
			TestDataSet testDataSet = new TestDataSet(testSet, getClazz().getValues());
			
			final Fold fold = new Fold(trainingDataSet, testDataSet);
			position++;
			return fold;
		}
	}

}
