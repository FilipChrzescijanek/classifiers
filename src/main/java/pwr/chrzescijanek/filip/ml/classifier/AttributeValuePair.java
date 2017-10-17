package pwr.chrzescijanek.filip.ml.classifier;

import java.util.Objects;

public class AttributeValuePair {

	private final String attributeName;
	private final Object value;
	
	public AttributeValuePair(String attributeName, Object value) {
		this.attributeName = Objects.requireNonNull(attributeName);
		this.value         = Objects.requireNonNull(value);
	}
	
	public String getAttributeName() {
		return attributeName;
	}
	
	public Object getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttributeValuePair other = (AttributeValuePair) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
