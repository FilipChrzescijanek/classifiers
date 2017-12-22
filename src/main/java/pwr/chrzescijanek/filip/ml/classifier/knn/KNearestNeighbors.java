package pwr.chrzescijanek.filip.ml.classifier.knn;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.util.Pair;
import pwr.chrzescijanek.filip.ml.classifier.AbstractClassifier;
import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.DataType;
import pwr.chrzescijanek.filip.ml.data.attribute.ContinuousAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DataAttribute;
import pwr.chrzescijanek.filip.ml.data.attribute.DiscreteAttribute;
import pwr.chrzescijanek.filip.ml.data.record.Record;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class KNearestNeighbors extends AbstractClassifier {

	private final Integer k;
	
	private final BiFunction<TestRecord, Record, Double>       distance;
	private final Function<List<Pair<String, Double>>, String> voting;

	private DataSet dataSet;

	public KNearestNeighbors(final Integer k, final BiFunction<TestRecord, Record, Double> function,
			final Function<List<Pair<String, Double>>, String> voting) {
		this.k        = Objects.requireNonNull(k);
		this.distance = Objects.requireNonNull(function);
		this.voting   = Objects.requireNonNull(voting);
	}
	
	@Override
	protected void buildModel(final DataSet ds) {
		dataSet = ds;
	}

	@Override
	protected DataSet normalize(DataSet ds) {
		checkAttributes(ds);
		final List<String> attributeTypes = getAttributeTypes(ds);
		final List<List<Object>> values   = getValues(ds);
		
		updateValues(ds, values);
		
		final List<Record> records = updateRecords(ds, values);
		
		return new DataSet(records, ds.getAttributeNames(), attributeTypes, ds.getClazz().getName());
	}

	private void checkAttributes(final DataSet ds) {
		ds.getAttributes().forEach(a -> {
			if (a instanceof DiscreteAttribute) {
				throw new IllegalArgumentException("Attributes must have continuous values!");
			}
		});
	}

	private List<String> getAttributeTypes(DataSet ds) {
		final List<String> attributeTypes = ds.getAttributes()
                .stream()
                .map(a -> DataType.C.toString().toLowerCase())
                .collect(Collectors.toList());
		return attributeTypes;
	}

	private List<List<Object>> getValues(DataSet ds) {
		final List<List<Object>> values = ds.getRecords()
		                                      .stream()
		                                      .map(r -> new ArrayList<>(r.getRawValues()))
		                                      .collect(Collectors.toList());
		return values;
	}

	private void updateValues(DataSet ds, final List<List<Object>> values) {
		for (final DataAttribute attribute : ds.getAttributes()) {
			final int index = ds.getAttributes().indexOf(attribute);
			
			final Double max = ds.getValues((ContinuousAttribute) attribute)
					.stream()
					.mapToDouble(Double::doubleValue)
					.max()
					.orElse(1.0);
			
			for (int i = 0; i < values.size(); i++) {
				values.get(i).set(index, ((Double) values.get(i).get(index)) / max);
			}
		}
	}

	private List<Record> updateRecords(DataSet ds, final List<List<Object>> values) {
		final List<Record> records = new ArrayList<>();
		for (int i = 0; i < values.size(); i++) {
			records.add(new Record(values.get(i), ds.getAttributeNames(), ds.getRecords().get(i).getClazz()));
		}
		return records;
	}

	@Override
	protected void assignClass(final TestRecord tr) {
		List<Pair<String, Double>> voters = dataSet.getRecords()
				.parallelStream()
				.map(r -> new Pair<String, Double>(r.getClazz(), distance.apply(tr, r)))
				.sorted((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
				.limit(k)
				.collect(Collectors.toList());
		tr.setAssignedClazz(voting.apply(voters));
	}

}
