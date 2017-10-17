package pwr.chrzescijanek.filip.ml.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardinalityDiscretizer extends AbstractDiscretizer {

	@Override
	protected List<Bin> createBins(final List<Double> column) {
		final List<Bin> bins      = new ArrayList<>();
		final List<Double> sorted = column.stream().sorted().collect(Collectors.toList());
		
		final Integer noOfBins    = (int) Math.sqrt(column.size());
		final Integer separation  = (column.size() + noOfBins - 1) / noOfBins;
		
		for (int j = 0; j < noOfBins; j++) {
			bins.add(new Bin(sorted.get(separation * j), sorted.get(separation * (j + 1) - 1)));
		}
		
		return bins;
	}

}
