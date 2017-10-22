package pwr.chrzescijanek.filip.ml.data.discretizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import pwr.chrzescijanek.filip.ml.data.DataSet;

public class RangeDiscretizer extends AbstractDiscretizer {

	@Override
	protected List<Bin> createBins(final DataSet ds, final List<Double> column) {
		final List<Bin> bins = new ArrayList<>();
		final Double max = column.stream().mapToDouble(Double::doubleValue).max().orElse(0.0) + EPSILON;
		final Double min = column.stream().mapToDouble(Double::doubleValue).min().orElse(0.0) - EPSILON;

		final Double romDiff     = Math.log10(max) - Math.log10(Math.max(min, EPSILON));
		final Double breadth     = romDiff > 3 ? max - min : (max - min) / Math.max(min, EPSILON);
		final Integer noOfBins   = findNoOfBins(breadth);
		final Double separation  = (max - min) / noOfBins;
		
		for (int j = 0; j < noOfBins; j++) {
			bins.add(new Bin(min + separation * j, min + separation * (j + 1)));
		}
		
		return bins;
	}

}
