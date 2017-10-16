package pwr.chrzescijanek.filip.ml.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataSource {

	private final String uri;

	private final Integer classIndex;

	public DataSource(final String uri, final Integer classIndex) {
		this.uri = Objects.requireNonNull(uri);
		this.classIndex = Objects.requireNonNull(classIndex);
	}

	public DataSet asDataSet() throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(uri)))) {
			final List<String> lines = br.lines().filter(s -> !s.isEmpty()).collect(Collectors.toList());
			final List<String> attributeNames = new ArrayList<>(Arrays.asList(lines.remove(0).split(",")));
			final String className = attributeNames.remove(classIndex.intValue());
			final Integer noOfAttributes = attributeNames.size();
			final List<List<String>> data = lines.stream().map(l -> new ArrayList<>(Arrays.asList(l.split(",")))).collect(Collectors.toList());
			final List<String> classValues = new ArrayList<>();
			data.forEach(row -> classValues.add(row.remove(classIndex.intValue())));
			final List<List<Double>> values = data.stream()
			                                      .map(row -> row.stream()
			                                                     .map(Double::parseDouble)
			                                                     .collect(Collectors.toList()))
			                                      .collect(Collectors.toList());
			final List<List<Bin>> discreteData = new ArrayList<>();
			values.forEach(l -> discreteData.add(new ArrayList<>()));
			for (int i = 0; i < noOfAttributes; i++) {
				final int index = i;
				final List<Double> column = values.stream().map(row -> row.get(index)).collect(Collectors.toList());
				final Integer max = (int) Math.ceil(column.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
				final Integer min = (int) Math.floor(column.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
				final Integer noOfBins = (int) Math.sqrt(max - min);
				final List<Bin> bins = new ArrayList<>();
				final Integer separation = ((max - min) + noOfBins - 1) / noOfBins;
				for (int j = 0; j < noOfBins; j++) {
					bins.add(new Bin(min + separation * j, min + separation * (j + 1)));
				}
				final List<Bin> discrete = column.stream().map(d -> bins.stream().filter(b -> b.contains(d)).findFirst().get()).collect(Collectors.toList());
				for (int j = 0; j < discreteData.size(); j++) {
					discreteData.get(j).add(discrete.get(j));
				}
			}
			final List<Record> records = new ArrayList<>();
			for (int i = 0; i < discreteData.size(); i++) {
				final List<ValueAttribute> attributes = new ArrayList<>();
				final List<Bin> row = discreteData.get(i);
				for (int j = 0; j < row.size(); j++) {
					attributes.add(new ValueAttribute(attributeNames.get(j), row.get(j)));
				}
				records.add(new Record(attributes, new ClassAttribute(className, classValues.get(i))));
			}
			return new DataSet(records, new DataClassAttribute(className, classValues.stream().distinct().collect(Collectors.toList())), noOfAttributes);
		}
	}

	public String getUri() {
		return uri;
	}

	public Integer getClassIndex() {
		return classIndex;
	}

}
