package pwr.chrzescijanek.filip.ml.data.discretizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;

public class RangeDiscretizer extends AbstractDiscretizer {

	@Override
	protected List<Bin> createBins(DataSet ds, List<Double> column) {
		final List<Bin> bins     = new ArrayList<>();
		final Integer max        = (int) Math.ceil(column.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
		final Integer min        = (int) Math.floor(column.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
		
		final Integer noOfBins   = (int) Math.sqrt(max - min);
		final Integer separation = ((max - min) + noOfBins - 1) / noOfBins;
		
		for (int j = 0; j < noOfBins; j++) {
			bins.add(new Bin((double) (min + separation * j), (double) (min + separation * (j + 1))));
		}
		
		return bins;
	}

}
