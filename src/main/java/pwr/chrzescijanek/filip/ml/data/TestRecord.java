package pwr.chrzescijanek.filip.ml.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TestRecord extends Record {

	private String assignedClazz;

	public TestRecord(final List<Object> values, final String clazz) {
		super(values, clazz);
	}

	public String getAssignedClazz() {
		return Optional.ofNullable(assignedClazz).orElse("null");
	}

	public void setAssignedClazz(final String assignedClazz) {
		this.assignedClazz = Objects.requireNonNull(assignedClazz);
	}

}
