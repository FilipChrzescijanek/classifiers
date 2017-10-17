package pwr.chrzescijanek.filip.ml.classifier;

import pwr.chrzescijanek.filip.ml.data.Real;
import pwr.chrzescijanek.filip.ml.data.Bin;
import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.NullClassAttribute;
import pwr.chrzescijanek.filip.ml.data.Record;
import pwr.chrzescijanek.filip.ml.data.TestRecord;
import pwr.chrzescijanek.filip.ml.data.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Bayes extends AbstractClassifier {

	private Model model;

	@Override
	protected void buildModel(final DataSet tds) {
		model = new Model(tds);
	}

	@Override
	protected void assignClass(final TestRecord tr) {
		Optional.ofNullable(model).ifPresent(m -> {
			final Map<Real, Map<Value, Double>> probabilities = m.getProbabilities();
			final Map<Real, Double> classProbabilities = m.getClassProbabilities();
			probabilities
					.entrySet()
					.stream()
					.collect(Collectors.toMap(Map.Entry::getKey,
					                          e -> classProbabilities.getOrDefault(e.getKey(), 0.0) *
					                               tr.getValues()
					                                 .stream()
					                                 .mapToDouble(va -> probabilities.getOrDefault(e.getKey(), Collections.emptyMap()).getOrDefault(va, 0.0)).reduce(1.0, (a, b) -> a * b)))
					.entrySet().stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).ifPresent(
							max -> tr.setAssignedClazz(max.getKey())
					);
			if (tr.getAssignedClazz() == null) {
				tr.setAssignedClazz(new NullClassAttribute());
			}
		});
	}

	private class Model {

		private final Map<Real, Map<Value, Double>> probabilities = new HashMap<>();
		private final Map<Real, Double> classProbabilities;

		Model(final DataSet tds) {
			Objects.requireNonNull(tds);
			classProbabilities = tds.getRecords().stream().collect(Collectors.groupingBy(Record::getClazz, Collectors.counting()))
			                        .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (1.0 * e.getValue()) / tds.getRecords().size()));
			final Map<Real, ? extends List<? extends Record>> map = tds.getRecords().stream().collect(Collectors.groupingBy(Record::getClazz));
			for (final Map.Entry<Real, ? extends List<? extends Record>> entry : map.entrySet()) {
				final List<? extends Record> list = entry.getValue();
				for (int i = 0; i < tds.getNoOfAttributes(); i++) {
					final List<Value> column = new ArrayList<>();
					for (int j = 0; j < list.size(); j++) {
						column.add(list.get(j).getValues().get(i));
					}
					final Map<Value, Long> collect = column.stream().collect(Collectors.groupingBy(v -> v, Collectors.counting()));
					final Map<Value, Double> prob = collect.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (1.0 * e.getValue()) / column.size()));
					if (probabilities.get(entry.getKey()) == null) {
						probabilities.put(entry.getKey(), prob);
					} else {
						probabilities.get(entry.getKey()).putAll(prob);
					}
				}
			}
		}

		public Map<Real, Double> getClassProbabilities() {
			return classProbabilities;
		}

		public Map<Real, Map<Value, Double>> getProbabilities() {
			return probabilities;
		}
	
	}

}
