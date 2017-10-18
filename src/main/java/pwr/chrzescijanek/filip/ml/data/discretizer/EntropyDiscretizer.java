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
	protected List<Bin> createBins(DataSet ds, List<Double> column) {
		final Integer noOfBins = (int) Math.sqrt(column.size());
		final Double max       = Math.ceil(column.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
		final Double min       = Math.floor(column.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
		
		final List<String> classes = ds.getRecords().stream().map(Record::getClazz).collect(Collectors.toList());
		final List<Bin> bins       = new ArrayList<>();
		
		bins.add(new Bin(min, max));
		
		return createBins(bins, noOfBins, column, classes);
	}
	
	private List<Bin> createBins(List<Bin> bins, Integer noOfBins, List<Double> column, List<String> classes) {
		final BinsData allBinsInfo = processBins(bins, column, classes);
		
		final Map.Entry<Bin, Double> maxEntropyBin = getMaxEntropyBin(allBinsInfo);
		bins.remove(maxEntropyBin.getKey());
		
		split(bins, column, classes, allBinsInfo.getDiscreteValues(), maxEntropyBin);
		
		if (bins.size() < noOfBins) {
			return createBins(bins, noOfBins, column, classes);
		}
		return bins;
	}

	private BinsData processBins(List<Bin> bins, List<Double> values, List<String> classes) {
		final List<Bin> discreteValues = discretize(bins, values);
		
		final Map<Bin, Map<String, Integer>> binClassDistribution = getBinsClassDistribution(bins, classes, discreteValues);
		
		final Map<Bin, Double> entropies = getEntropies(binClassDistribution);
		
		return new BinsData(discreteValues, entropies);
	}

	private List<Bin> discretize(List<Bin> bins, List<Double> values) {
		final List<Bin> discreteValues = values
				.stream()
				.map(d -> bins.stream().filter(b -> b.contains(d)).findFirst().get())
				.collect(Collectors.toList());
		return discreteValues;
	}

	private Map<Bin, Map<String, Integer>> getBinsClassDistribution(List<Bin> bins, List<String> classes,
			final List<Bin> discreteValues) {
		final Map<Bin, Map<String, Integer>> binClasses = new HashMap<>();
		
		bins.forEach(b -> {
			Map<String, Integer> classMap = new HashMap<>();
			for (int i = 0; i < discreteValues.size(); i++) {
				if (discreteValues.get(i) == b) {
					String current = classes.get(i);
					classMap.put(current, classMap.getOrDefault(current, 0) + 1);
				}
			}
			binClasses.put(b, classMap);	
		});
	
		return binClasses;
	}

	private Map<Bin, Double> getEntropies(final Map<Bin, Map<String, Integer>> binClassDistribution) {
		final Map<Bin, Double> entropies = binClassDistribution.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> {
			Map<String, Integer> m = e.getValue();
			Integer binSize = m.values().stream().mapToInt(Integer::intValue).sum();
			return binSize == 0 ? 0.0 : -m.entrySet().stream().mapToDouble(entry -> {
				Double probability = (1.0 * entry.getValue()) / binSize;
				return probability.equals(0.0) ? 0.0 : probability * Math.log(probability) / Math.log(2);
			}).sum();
		}));
		return entropies;
	}
	

	private Map.Entry<Bin, Double> getMaxEntropyBin(BinsData allBinsInfo) {
		Map<Bin, Double> allEntropies = allBinsInfo.getEntropies();
		final Map.Entry<Bin, Double> maxEntropy = allEntropies.entrySet()
				.stream()
				.max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get();
		return maxEntropy;
	}

	private void split(List<Bin> bins, List<Double> column, List<String> classes, List<Bin> discreteValues,
			final Map.Entry<Bin, Double> maxEntropy) {
		List<Double> binValues = new ArrayList<>();
		List<String> binClasses = new ArrayList<>();
		
		for (int i = 0; i < column.size(); i++) {
			if (discreteValues.get(i) == maxEntropy.getKey()) {
				binValues .add(column .get(i));
				binClasses.add(classes.get(i));
			}
		}
		
		Double minValue = maxEntropy.getKey().getMinValue();
		Double maxValue = maxEntropy.getKey().getMaxValue();
		
		List<Bin> bestSplit = chooseBestSplit(maxEntropy, minValue, maxValue, binValues, binClasses);
		
		bins.addAll(bestSplit);
	}

	private List<Bin> chooseBestSplit(final Map.Entry<Bin, Double> maxEntropy, Double minValue, Double maxValue,
			List<Double> binValues, List<String> binClasses) {
		Map<List<Bin>, Double> gains = new HashMap<>();
		
		List<Double> sorted = binValues.stream().sorted().collect(Collectors.toList());
	
		for (int i = 0; i < sorted.size() - 1; i++) {
			Double splitValue = (sorted.get(i) + sorted.get(i + 1)) / 2.0;
			List<Bin> newBinz = createBins(minValue, maxValue, splitValue);
			Double gain = calculateGain(maxEntropy.getValue(), binValues, binClasses, newBinz);
			gains.put(newBinz, gain);
		}
		
		List<Bin> bestSplit = gains.entrySet().stream().max((e1, e2) -> e1.getValue().compareTo(e2.getValue())).get().getKey();
		return bestSplit;
	}

	private List<Bin> createBins(Double minValue, Double maxValue, Double splitValue) {
		List<Bin> binz = new ArrayList<>();
		Bin firstBin = new Bin(minValue, splitValue); 
		Bin secondBin = new Bin(splitValue, maxValue);
		binz.add(firstBin);
		binz.add(secondBin);
		return binz;
	}

	private Double calculateGain(Double maxEntropy, List<Double> binValues, List<String> binClasses, List<Bin> bins) {
		BinsData binsInfo = processBins(bins, binValues, binClasses);
		Map<Bin, Double> entropies = binsInfo.getEntropies();
		Entry<Bin, Double> firstBinData  = entropies.entrySet().stream().filter(e -> e.getKey().equals(bins.get(0))).findFirst().get();
		Entry<Bin, Double> secondBinData = entropies.entrySet().stream().filter(e -> e.getKey().equals(bins.get(1))).findFirst().get();
		Double gain = gain(maxEntropy, info(firstBinData, secondBinData, binsInfo.getDiscreteValues()));
		return gain;
	}	
	
	private Double info(Entry<Bin, Double> firstBin, Entry<Bin, Double> secondBin, List<Bin> discreteValues) {
		Double firstBinProbability  = discreteValues.stream().filter(b -> b.equals(firstBin .getKey())).count() / (discreteValues.size() * 1.0);
		Double secondBinProbability = discreteValues.stream().filter(b -> b.equals(secondBin.getKey())).count() / (discreteValues.size() * 1.0);
		
		return firstBinProbability * firstBin.getValue() + secondBinProbability * secondBin.getValue();
	}
	
	private Double gain(Double maxEntropy, Double info) {
		return maxEntropy - info;
	}
	
	private class BinsData {
		private final List<Bin> discreteValues;
		private final Map<Bin, Double> entropies;
		
		private BinsData(List<Bin> discreteValues, Map<Bin, Double> entropies) {
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
