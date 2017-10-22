package pwr.chrzescijanek.filip.ml.data.discretizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;

public class CardinalityDiscretizer extends AbstractDiscretizer {

	@Override
	protected List<Bin> createBins(final DataSet ds, final List<Double> column) {
		final List<Bin> bins      = new ArrayList<>();
		final List<Double> sorted = column.stream().sorted().collect(Collectors.toList());
		
		final Integer size        = column.size();
		final Integer noOfBins    = (int) Math.sqrt(size);
		final Integer separation  = (size + noOfBins - 1) / noOfBins;
		final Integer max         = (int) Math.ceil(column.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
		final Integer min         = (int) Math.floor(column.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
		
		for (int j = 0; j < noOfBins; j++) {
			final Integer minIndex = separation * j;
			final Integer maxIndex = separation * (j + 1) - 1;
			final Double minValue  = minIndex == 0        ? min.doubleValue() : bins.get(j - 1).getMaxValue();
			final Double maxValue  = maxIndex >= size - 1 ? max.doubleValue() : sorted.get(maxIndex);
			bins.add(new Bin(minValue, maxValue));
		}
		
		return bins;
	}

}
