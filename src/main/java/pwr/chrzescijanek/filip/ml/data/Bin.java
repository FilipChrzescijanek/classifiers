package pwr.chrzescijanek.filip.ml.data;

import java.util.Objects;

public class Bin  {

	private final Integer minValue;
	private final Integer maxValue;

	public Bin(final Integer minValue, final Integer maxValue) {
		this.minValue = Objects.requireNonNull(minValue);
		this.maxValue = Objects.requireNonNull(maxValue);
	}

	public Integer getMinValue() {
		return minValue;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public Boolean contains(final Double d) {
		double epsilon = 0.0000001;
		return minValue == maxValue ? Math.abs(d - maxValue) < epsilon : d >= minValue && d < maxValue;
	}
	
	@Override
	public String toString() {
		return minValue + " - " + maxValue;
	}

}
