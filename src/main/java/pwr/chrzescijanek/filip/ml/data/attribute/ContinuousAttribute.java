package pwr.chrzescijanek.filip.ml.data.attribute;

import java.util.List;

public class ContinuousAttribute extends DataAttribute {
	
	public ContinuousAttribute(String name, List<Double> values) {
		super(name);
	}

	@Override
	public boolean isDiscrete() {
		return false;
	}

}
