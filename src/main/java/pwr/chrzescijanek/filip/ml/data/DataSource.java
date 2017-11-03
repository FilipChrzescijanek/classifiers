package pwr.chrzescijanek.filip.ml.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.discretizer.Discretizer;
import pwr.chrzescijanek.filip.ml.data.record.Record;

public class DataSource {

	private final String uri;

	private final Integer classIndex;

	public DataSource(final String uri, final Integer classIndex) {
		this.uri = Objects.requireNonNull(uri);
		this.classIndex = Objects.requireNonNull(classIndex);
	}
	
	public DataSet asDataSet() throws IOException {
		return asDataSet(null);
	}

	public DataSet asDataSet(final Discretizer discretizer) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getUri())))) {
			final List<String> lines = br.lines().filter(s -> !s.isEmpty()).collect(Collectors.toList());
			
			final List<String> attributeNames = new ArrayList<>(Arrays.asList(lines.remove(0).split("\\s*,\\s*")));
			final List<String> attributeTypes = new ArrayList<>(Arrays.asList(lines.remove(0).split("\\s*,\\s*")));
			
			final String className = attributeNames.remove(getClassIndex().intValue());
			final String classType = attributeTypes.remove(getClassIndex().intValue());
			checkClassType(classType);
			
			final List<Record> records = processData(lines, attributeNames, attributeTypes);
			
			final DataSet dataSet = new DataSet(records, attributeNames, attributeTypes, className);
		
			if (Objects.nonNull(discretizer)) {
				return discretizer.discretize(dataSet);
			}
			return dataSet;	
		}
	}

	public String getUri() {
		return uri;
	}

	public Integer getClassIndex() {
		return classIndex;
	}

	private void checkClassType(final String classType) {
		if (!DataType.D.toString().toLowerCase().equals(classType)) {
			throw new IllegalArgumentException("Class attribute must have discrete values!");
		}
	}

	private List<Record> processData(final List<String> lines, final List<String> attributeNames, final List<String> attributeTypes) {
		final List<List<String>> data = lines
				.stream()
				.map(l -> new ArrayList<>(Arrays.asList(l.split("\\s*,\\s*"))))
				.collect(Collectors.toList());
		
		final List<String> classValues = new ArrayList<>();
		data.forEach(row -> classValues.add(row.remove(getClassIndex().intValue())));
		
		final List<Record> records = createRecords(attributeTypes, attributeNames, data, classValues);
		return records;
	}

	private List<Record> createRecords(final List<String> attributeTypes, final List<String> attributeNames, final List<List<String>> data,
			final List<String> classValues) {
		final List<Record> records = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			final List<Object> values = new ArrayList<>();
			final List<String> row = data.get(i);
			for (int j = 0; j < row.size(); j++) {
				if (DataType.C.toString().toLowerCase().equals(attributeTypes.get(j))) {
					values.add(Double.parseDouble(row.get(j)));
				} else if (DataType.D.toString().toLowerCase().equals(attributeTypes.get(j))) {
					values.add(row.get(j));
				} else {
					throw new UnknownFormatConversionException("Unknown attribute type" + attributeTypes.get(i));
				}
			}
			records.add(new Record(values, attributeNames, classValues.get(i)));
		}
		return records;
	}

}
