package pwr.chrzescijanek.filip.ml.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractDiscretizer implements Discretizer {

	@Override
	public DataSet discretize(DataSet ds) {
		final List<ContinuousAttribute> continuousAttributes = ds.getAttributes()
				.stream()
				.filter(a -> !a.isDiscrete())
				.map(a -> (ContinuousAttribute) a)
				.collect(Collectors.toList());
		
		List<List<Object>> values = ds.getRecords().stream().map(r -> new ArrayList<>(r.getValues())).collect(Collectors.toList());
		List<Record> records = updateRecords(ds, continuousAttributes, values);
		List<String> attributeNames = ds.getAttributeNames();
		List<String> attributeTypes = ds.getAttributes().stream().map(a -> "d").collect(Collectors.toList());
		return new DataSet(records, attributeNames, attributeTypes, ds.getClazz().getName());
	}

	private List<Record> updateRecords(DataSet ds, final List<ContinuousAttribute> attributes,
			List<List<Object>> values) {
		updateValues(ds, values, attributes);
		List<Record> records = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			records.add(new Record(values.get(i), ds.getRecords().get(i).getClazz()));
		}
		return records;
	}
	
	private void updateValues(DataSet ds, List<List<Object>> values, List<ContinuousAttribute> attributes) {
		for (ContinuousAttribute attribute : attributes) {
			final int index = ds.getAttributes().indexOf(attribute);
			
			final List<Double> column    = ds.getValues(attribute);
			final List<Bin> bins         = createBins(column);
			
			final List<Bin> discretized  = column
					.stream()
					.map(d -> bins.stream().filter(b -> b.contains(d)).findFirst().get())
					.collect(Collectors.toList());
			
			for (int i = 0; i < values.size(); i++) {
				values.get(i).set(index, discretized.get(i).toString());
			}
		}
	}
	
	protected abstract List<Bin> createBins(List<Double> column);

}
