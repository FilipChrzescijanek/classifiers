package pwr.chrzescijanek.filip.ml.data;

import java.util.List;
import java.util.Objects;

public class ContinuousAttribute extends DataAttribute {

	private Double mean;
	private Double stdDev;
	
	public ContinuousAttribute(String name, List<Double> values) {
		super(name);
		this.mean = Objects.requireNonNull(values).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
		this.stdDev = Math.sqrt(values.stream().reduce(0.0, (a, b) -> a + Math.pow(b - getMean(), 2)) / (values.size() - 1));
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

	public Double getMean() {
		return mean;
	}
	
	public Double getStdDev() {
		return stdDev;
	}

}
