package pwr.chrzescijanek.filip.ml.data.attribute;

public abstract class DataAttribute extends Attribute {

	protected DataAttribute(String name) {
		super(name);
	}
	
	public abstract boolean isDiscrete();

}
