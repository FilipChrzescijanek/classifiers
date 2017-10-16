package pwr.chrzescijanek.filip.ml.data;

import java.util.Objects;

public class Bin {

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
		return d >= minValue && d < maxValue;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) return true;
		if (!(other instanceof Bin)) return false;

		final Bin bin = (Bin) other;

		return getMinValue().equals(bin.getMinValue()) && getMaxValue().equals(bin.getMaxValue());

	}

	@Override
	public int hashCode() {
		int result = getMinValue().hashCode();
		result = 31 * result + getMaxValue().hashCode();
		return result;
	}

}
