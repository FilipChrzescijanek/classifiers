package pwr.chrzescijanek.filip.ml.classifier.knn;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javafx.util.Pair;
import pwr.chrzescijanek.filip.ml.classifier.AbstractClassifier;
import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.attribute.DiscreteAttribute;
import pwr.chrzescijanek.filip.ml.data.record.Record;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class KNearestNeighbors extends AbstractClassifier {

	private final Integer k;
	private final BiFunction<TestRecord, Record, Double> function;

	private DataSet dataSet;

	public KNearestNeighbors(final Integer k, final BiFunction<TestRecord, Record, Double> function) {
		this.k = Objects.requireNonNull(k);
		this.function = Objects.requireNonNull(function);
	}
	
	@Override
	protected void buildModel(final DataSet tds) {
		dataSet = minimalizeDifferences(tds);
	}

	private DataSet minimalizeDifferences(DataSet tds) {
		checkAttributes(tds);
		return tds;
	}

	private void checkAttributes(final DataSet tds) {
		tds.getAttributes().forEach(a -> {
			if (a instanceof DiscreteAttribute) {
				throw new IllegalArgumentException("Attributes must have continuous values!");
			}
		});
	}

	@Override
	protected void assignClass(final TestRecord tr) {
		tr.setAssignedClazz(dataSet.getRecords()
				.parallelStream()
				.map(r -> new Pair<String, Double>(r.getClazz(), function.apply(tr, r)))
				.sorted((p1, p2) -> p1.getValue().compareTo(p2.getValue()))
				.limit(k)
				.collect(Collectors.groupingBy(pair -> pair.getKey(), Collectors.counting()))
				.entrySet()
				.stream()
				.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.orElse(new NullEntry()).getKey());
	}

	private final class NullEntry implements Map.Entry<String, Long> {
		
		@Override
		public String getKey() {
			return null;
		}

		@Override
		public Long getValue() {
			return 0L;
		}

		@Override
		public Long setValue(Long value) {
			return 0L;
		}
		
	}

}
