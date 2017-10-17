package pwr.chrzescijanek.filip.ml.classifier;

import java.util.Objects;

public class ClassAttributePair {

	private final String classValue;
	private final String attributeName;
	
	public ClassAttributePair(String classValue, String attributeName) {
		this.classValue    = Objects.requireNonNull(classValue);
		this.attributeName = Objects.requireNonNull(attributeName);
	}
	
	public String getClassValue() {
		return classValue;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
		result = prime * result + ((classValue == null)    ? 0 : classValue.hashCode());
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
		ClassAttributePair other = (ClassAttributePair) obj;
		if (attributeName == null) {
			if (other.attributeName != null)
				return false;
		} else if (!attributeName.equals(other.attributeName))
			return false;
		if (classValue == null) {
			if (other.classValue != null)
				return false;
		} else if (!classValue.equals(other.classValue))
			return false;
		return true;
	}
	
}
