package pwr.chrzescijanek.filip.ml.classifier;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

import javafx.util.Pair;
import pwr.chrzescijanek.filip.ml.classifier.bayes.Bayes;
import pwr.chrzescijanek.filip.ml.classifier.ila.InductiveLearning;
import pwr.chrzescijanek.filip.ml.classifier.knn.KNearestNeighbors;
import pwr.chrzescijanek.filip.ml.classifier.knn.NullEntry;
import pwr.chrzescijanek.filip.ml.data.DataSource;
import pwr.chrzescijanek.filip.ml.data.discretizer.CardinalityDiscretizer;
import pwr.chrzescijanek.filip.ml.data.discretizer.EntropyDiscretizer;
import pwr.chrzescijanek.filip.ml.data.discretizer.RangeDiscretizer;
import pwr.chrzescijanek.filip.ml.data.record.Record;
import pwr.chrzescijanek.filip.ml.data.record.TestRecord;

public class ClassifierTest {

	private static final double EPSILON = Math.pow(10, -9);
	
	@Test
	public void bayes() throws Exception {
		testCrossValidation(new Bayes(), false, false);
	}

	@Test	
	public void knn() throws Exception {
		List<String> dataSets = Arrays.asList("/wine.csv", "/ecoli.csv", "/transfusion.csv", "/seeds.csv", "/iris.csv");
		List<Integer> kValues = Arrays.asList(1, 3, 5, 7, 9);

		List<Function<List<Pair<String, Double>>, String>> votingFunctions = Arrays.asList(
			list -> {
				return list
					.stream()
					.collect(Collectors.groupingBy(pair -> pair.getKey(), Collectors.counting()))
					.entrySet()
					.stream()
					.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
					.orElse(new NullEntry<Long>()).getKey();
			}, 
			list -> {
				return list
					.stream()
					.collect(Collectors.groupingBy(
							pair -> pair.getKey(), 
							Collectors.reducing(0.0, p -> 1.0 / (Math.max(EPSILON, p.getValue())), Double::sum)))
					.entrySet()
					.stream()
					.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
					.orElse(new NullEntry<Double>()).getKey();
			}, 
			list -> {
				return list
					.stream()
					.collect(Collectors.groupingBy(
							pair -> pair.getKey(), 
							Collectors.reducing(0.0, p -> 1.0 / Math.pow((Math.max(EPSILON, p.getValue())), 2), Double::sum)))
					.entrySet()
					.stream()
					.max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
					.orElse(new NullEntry<Double>()).getKey();
			});
		
		List<BiFunction<TestRecord, Record, Double>> distanceFunctions = Arrays.asList(
			(r1, r2) -> {
				Double distance = 0.0;
				List<Double> firstValues  = r1.getRawValues().stream().map(o -> (Double) o).collect(Collectors.toList());
				List<Double> secondValues = r2.getRawValues().stream().map(o -> (Double) o).collect(Collectors.toList());
				for (int i = 0; i < firstValues.size(); i++) {
					distance += Math.pow(firstValues.get(i) - secondValues.get(i), 2);
				}
				return Math.sqrt(distance);
			},
			(r1, r2) -> {
				Double distance = 0.0;
				List<Double> firstValues  = r1.getRawValues().stream().map(o -> (Double) o).collect(Collectors.toList());
				List<Double> secondValues = r2.getRawValues().stream().map(o -> (Double) o).collect(Collectors.toList());
				for (int i = 0; i < firstValues.size(); i++) {
					distance += Math.abs(firstValues.get(i) - secondValues.get(i));
				}
				return distance;
			});

		for (String dataSet : dataSets) {
			System.out.println("Data set: " + dataSet);

			System.out.println("Distance functions:");
			for (BiFunction<TestRecord, Record, Double> f : distanceFunctions) {
				for (Integer k : kValues) {
					System.out.println("k = " + k);
					testCrossValidation(new KNearestNeighbors(k, f, votingFunctions.get(0)), dataSet, false, true);
				}
			}
			
			System.out.println("Voting functions:");
			for (Function<List<Pair<String, Double>>, String> f : votingFunctions) {
				for (Integer k : kValues) {
					System.out.println("k = " + k);
					testCrossValidation(new KNearestNeighbors(k, distanceFunctions.get(0), f), dataSet, false, true);
				}
			}
		}
	}

	@Test
	public void ila() throws Exception {
		testCrossValidation(new InductiveLearning(), true, false);
	}
	
	private void testCrossValidation(final Classifier c, final Boolean onlyDiscrete, final Boolean onlyContinuous) throws IOException {
		testCrossValidation(c, "/iris.csv", onlyDiscrete, onlyContinuous);
	}
	
	private void testCrossValidation(final Classifier c, final String dataSetPath, final Boolean onlyDiscrete, final Boolean onlyContinuous) throws IOException {
		final Integer classIndex = 0;
		final Integer folds      = 10;
		final String dataSet     = getClass().getResource(dataSetPath).getPath();

		if (!onlyDiscrete) {
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(), folds));
		}
		if (!onlyContinuous) {
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new RangeDiscretizer()), folds));
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new CardinalityDiscretizer()), folds));
			print(c.crossValidate(new DataSource(dataSet, classIndex).asDataSet(new EntropyDiscretizer()), folds));
		}
	}

	private void print(final Eval e) {
		System.out.println(Objects.requireNonNull(e));
	}

}