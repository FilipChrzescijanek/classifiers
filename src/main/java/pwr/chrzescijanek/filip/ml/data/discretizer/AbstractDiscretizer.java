package pwr.chrzescijanek.filip.ml.data.discretizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.DataType;
import pwr.chrzescijanek.filip.ml.data.attribute.ContinuousAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DataAttribute;
import pwr.chrzescijanek.filip.ml.data.record.Record;

public abstract class AbstractDiscretizer implements Discretizer {

	public static final Double EPSILON = 0.000000001;

	@Override
	public DataSet discretize(final DataSet ds) {
		final List<ContinuousAttribute> continuousAttributes = ds.getAttributes()
				.stream()
				.filter(a -> !a.isDiscrete())
				.map(a -> (ContinuousAttribute) a)
				.collect(Collectors.toList());
		
		final List<List<Object>> values   = ds.getRecords()
		                                      .stream()
		                                      .map(r -> new ArrayList<>(r.getRawValues()))
		                                      .collect(Collectors.toList());
		final List<Record> records        = updateRecords(ds, continuousAttributes, values);
		final List<String> attributeNames = ds.getAttributeNames();
		final List<String> attributeTypes = ds.getAttributes()
		                                      .stream()
		                                      .map(a -> DataType.D.toString().toLowerCase())
		                                      .collect(Collectors.toList());
		return new DataSet(records, attributeNames, attributeTypes, ds.getClazz().getName());
	}

	private List<Record> updateRecords(final DataSet ds, final List<ContinuousAttribute> attributes,
	                                   final List<List<Object>> values) {
		updateValues(ds, values, attributes);
		final List<String> attributeNames = attributes
				.stream()
				.map(DataAttribute::getName)
				.collect(Collectors.toList());
		
		final List<Record> records = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			records.add(new Record(values.get(i), attributeNames, ds.getRecords().get(i).getClazz()));
		}
		return records;
	}
	
	private void updateValues(final DataSet ds, final List<List<Object>> values, final List<ContinuousAttribute> attributes) {
		for (final ContinuousAttribute attribute : attributes) {
			final int index = ds.getAttributes().indexOf(attribute);
			
			final List<Double> column    = ds.getValues(attribute);
			final List<Bin> bins         = createBins(ds, column);
			
			final List<Bin> discretized  = column
					.stream()
					.map(d -> bins.stream().filter(b -> b.contains(d)).findFirst().get())
					.collect(Collectors.toList());
			
			for (int i = 0; i < values.size(); i++) {
				values.get(i).set(index, discretized.get(i).toString());
			}
		}
	}

	protected Integer findNoOfBins(final Number breadth) {
		return (int) Math.max(Math.round(Math.sqrt(breadth.doubleValue())), 1);
	}
	
	protected abstract List<Bin> createBins(DataSet ds, List<Double> column);

}
