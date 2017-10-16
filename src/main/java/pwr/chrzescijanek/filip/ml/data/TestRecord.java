package pwr.chrzescijanek.filip.ml.data;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TestRecord extends Record {

	private ClassAttribute assignedClazz;

	public TestRecord(final List<ValueAttribute> attributes, final ClassAttribute clazz) {
		super(attributes, clazz);
	}

	public ClassAttribute getAssignedClazz() {
		return Optional.ofNullable(assignedClazz).orElse(new NullClassAttribute());
	}

	public void setAssignedClazz(final ClassAttribute assignedClazz) {
		this.assignedClazz = Objects.requireNonNull(assignedClazz);
	}

}
