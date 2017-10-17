package pwr.chrzescijanek.filip.ml.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UnknownFormatConversionException;
import java.util.stream.Collectors;

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

	public DataSet asDataSet(Discretizer discretizer) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(uri)))) {
			final List<String> lines = br.lines().filter(s -> !s.isEmpty()).collect(Collectors.toList());
			final List<String> attributeNames = new ArrayList<>(Arrays.asList(lines.remove(0).split(",")));
			final List<String> attributeTypes = new ArrayList<>(Arrays.asList(lines.remove(0).split(",")));
			final String className = attributeNames.remove(classIndex.intValue());
			final String classType = attributeTypes.remove(classIndex.intValue());
			assert(classType.equals("d"));
			final List<List<String>> data = lines.stream().map(l -> new ArrayList<>(Arrays.asList(l.split(",")))).collect(Collectors.toList());
			final List<String> classValues = new ArrayList<>();
			data.forEach(row -> classValues.add(row.remove(classIndex.intValue())));
			
			final List<Record> records = new ArrayList<>();
			for (int i = 0; i < data.size(); i++) {
				final List<Object> values = new ArrayList<>();
				final List<String> row = data.get(i);
				for (int j = 0; j < row.size(); j++) {
					if (attributeTypes.get(i).equals("c")) {
						values.add(Double.parseDouble(row.get(j)));
					} else if (attributeTypes.get(i).equals("d")) {
						values.add(row.get(j));
					} else {
						throw new UnknownFormatConversionException("Unknown attribute type" + attributeTypes.get(i));
					}
				}
				records.add(new Record(values, classValues.get(i)));
			}
			
			return new DataSet(records, attributeNames, attributeTypes, className);
		}
	}

	public String getUri() {
		return uri;
	}

	public Integer getClassIndex() {
		return classIndex;
	}

}
