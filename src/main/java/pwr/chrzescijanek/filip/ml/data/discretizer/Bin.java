package pwr.chrzescijanek.filip.ml.data.discretizer;

import java.util.Objects;

public class Bin  {

	private final Double minValue;
	private final Double maxValue;

	public Bin(final Double minValue, final Double maxValue) {
		this.minValue = Objects.requireNonNull(minValue);
		this.maxValue = Objects.requireNonNull(maxValue);
	}

	public Double getMinValue() {
		return minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public Boolean contains(final Double d) {
		return minValue.equals(maxValue) ? d.equals(maxValue) : d.compareTo(minValue) >= 0 && d.compareTo(maxValue) < 0;
	}
	
	@Override
	public String toString() {
		return String.format("%.2f - %.2f", minValue, maxValue);
	}

}
