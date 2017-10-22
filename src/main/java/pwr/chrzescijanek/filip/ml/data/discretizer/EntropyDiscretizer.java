package pwr.chrzescijanek.filip.ml.data.discretizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;
import pwr.chrzescijanek.filip.ml.data.record.Record;

public class EntropyDiscretizer extends AbstractDiscretizer {

	@Override
	protected List<Bin> createBins(final DataSet ds, final List<Double> column) {
		final Integer noOfBins = findNoOfBins(column.size());
		final Double max       = column.stream().mapToDouble(Double::doubleValue).max().orElse(0.0) + EPSILON;
		final Double min       = column.stream().mapToDouble(Double::doubleValue).min().orElse(0.0) - EPSILON;
		
		final List<String> classes = ds.getRecords().stream().map(Record::getClazz).collect(Collectors.toList());
		final List<Bin> bins       = new ArrayList<>();
		
		bins.add(new Bin(min, max));
		
		return createBins(bins, noOfBins, column, classes);
	}
	
	private List<Bin> createBins(final List<Bin> bins, final Integer noOfBins, final List<Double> column, final List<String> classes) {
		final BinsData allBinsInfo = processBins(bins, column, classes);
		
		final Map.Entry<Bin, Double> maxEntropyBin = getMaxEntropyBin(allBinsInfo);
		bins.remove(maxEntropyBin.getKey());
		
		split(bins, column, classes, allBinsInfo.getDiscreteValues(), maxEntropyBin);
		
		if (bins.size() < noOfBins) {
			return createBins(bins, noOfBins, column, classes);
		}
		return bins;
	}

	private BinsData processBins(final List<Bin> bins, final List<Double> values, final List<String> classes) {
		final List<Bin> discreteValues = discretize(bins, values);
		
		final Map<Bin, Map<String, Integer>> binClassDistribution = getBinsClassDistribution(bins, classes, discreteValues);
		
		final Map<Bin, Double> entropies = getEntropies(binClassDistribution);
		
		return new BinsData(discreteValues, entropies);
	}

	private List<Bin> discretize(final List<Bin> bins, final List<Double> values) {
		final List<Bin> discreteValues = values
				.stream()
				.map(d -> bins.stream().filter(b -> b.contains(d)).findFirst().get())
				.collect(Collectors.toList());
		return discreteValues;
	}

	private Map<Bin, Map<String, Integer>> getBinsClassDistribution(final List<Bin> bins, final List<String> classes,
	                                                                final List<Bin> discreteValues) {
		final Map<Bin, Map<String, Integer>> binClasses = new HashMap<>();
		
		bins.forEach(b -> {
			final Map<String, Integer> classMap = new HashMap<>();
			for (int i = 0; i < discreteValues.size(); i++) {
				if (discreteValues.get(i) == b) {
					final String current = classes.get(i);
					classMap.put(current, classMap.getOrDefault(current, 0) + 1);
				}
			}
			binClasses.put(b, classMap);	
		});
	
		return binClasses;
	}

	private Map<Bin, Double> getEntropies(final Map<Bin, Map<String, Integer>> binClassDistribution) {
		final Map<Bin, Double> entropies = binClassDistribution.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
			final Map<String, Integer> m = e.getValue();
			final Integer binSize = m.values().stream().mapToInt(Integer::intValue).sum();
			return binSize == 0 ? 0.0 : -m.entrySet().stream().mapToDouble(entry -> {
				Double probability = (1.0 * entry.getValue()) / binSize;
				return probability.equals(0.0) ? 0.0 : probability * Math.log(probability) / Math.log(2);
			}).sum();
		}));
		return entropies;
	}
	

	private Map.Entry<Bin, Double> getMaxEntropyBin(final BinsData allBinsInfo) {
		final Map<Bin, Double> allEntropies = allBinsInfo.getEntropies();
		final Map.Entry<Bin, Double> maxEntropy = allEntropies.entrySet()
				.stream()
				.max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get();
		return maxEntropy;
	}

	private void split(final List<Bin> bins, final List<Double> column, final List<String> classes, final List<Bin> discreteValues,
	                   final Map.Entry<Bin, Double> maxEntropy) {
		final List<Double> binValues = new ArrayList<>();
		final List<String> binClasses = new ArrayList<>();
		
		for (int i = 0; i < column.size(); i++) {
			if (discreteValues.get(i) == maxEntropy.getKey()) {
				binValues .add(column .get(i));
				binClasses.add(classes.get(i));
			}
		}
		
		final Double minValue = maxEntropy.getKey().getMinValue();
		final Double maxValue = maxEntropy.getKey().getMaxValue();
		
		final List<Bin> bestSplit = chooseBestSplit(maxEntropy, minValue, maxValue, binValues, binClasses);
		
		bins.addAll(bestSplit);
	}

	private List<Bin> chooseBestSplit(final Map.Entry<Bin, Double> maxEntropy, final Double minValue, final Double maxValue,
	                                  final List<Double> binValues, final List<String> binClasses) {
		final Map<List<Bin>, Double> gains = new HashMap<>();
		
		final List<Double> sorted = binValues.stream().sorted().collect(Collectors.toList());
	
		for (int i = 0; i < sorted.size() - 1; i++) {
			final Double splitValue = (sorted.get(i) + sorted.get(i + 1)) / 2.0;
			final List<Bin> newBinz = createBins(minValue, maxValue, splitValue);
			final Double gain = calculateGain(maxEntropy.getValue(), binValues, binClasses, newBinz);
			gains.put(newBinz, gain);
		}
		
		return gains.entrySet().stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey();
	}

	private List<Bin> createBins(final Double minValue, final Double maxValue, final Double splitValue) {
		final List<Bin> binz = new ArrayList<>();
		final Bin firstBin = new Bin(minValue, splitValue);
		final Bin secondBin = new Bin(splitValue, maxValue);
		binz.add(firstBin);
		binz.add(secondBin);
		return binz;
	}

	private Double calculateGain(final Double maxEntropy, final List<Double> binValues, final List<String> binClasses, final List<Bin> bins) {
		final BinsData binsInfo = processBins(bins, binValues, binClasses);
		final Map<Bin, Double> entropies = binsInfo.getEntropies();
		final Entry<Bin, Double> firstBinData  = entropies.entrySet().stream().filter(e -> e.getKey().equals(bins.get(0))).findFirst().get();
		final Entry<Bin, Double> secondBinData = entropies.entrySet().stream().filter(e -> e.getKey().equals(bins.get(1))).findFirst().get();
		return gain(maxEntropy, info(firstBinData, secondBinData, binsInfo.getDiscreteValues()));
	}	
	
	private Double info(final Entry<Bin, Double> firstBin, final Entry<Bin, Double> secondBin, final List<Bin> discreteValues) {
		final Double firstBinProbability  = discreteValues.stream().filter(b -> b.equals(firstBin.getKey())).count() / (discreteValues.size() * 1.0);
		final Double secondBinProbability = discreteValues.stream().filter(b -> b.equals(secondBin.getKey())).count() / (discreteValues.size() * 1.0);
		
		return firstBinProbability * firstBin.getValue() + secondBinProbability * secondBin.getValue();
	}
	
	private Double gain(final Double maxEntropy, final Double info) {
		return maxEntropy - info;
	}
	
	private class BinsData {
		private final List<Bin> discreteValues;
		private final Map<Bin, Double> entropies;
		
		private BinsData(final List<Bin> discreteValues, final Map<Bin, Double> entropies) {
			this.discreteValues = discreteValues;
			this.entropies = entropies;
		}

		public List<Bin> getDiscreteValues() {
			return discreteValues;
		}
		
		public Map<Bin, Double> getEntropies() {
			return entropies;
		}
	}

}
