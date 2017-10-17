package pwr.chrzescijanek.filip.ml.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RangeDiscretizer implements Discretizer {

	@Override
	public DataSet discretize(DataSet ds) {
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
		}
	}

}
