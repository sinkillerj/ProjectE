package moze_intel.projecte.api.data;

import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
class BaseFileBuilder<BUILDER extends BaseFileBuilder<BUILDER>> {

	@Nullable
	protected String comment;
	private final String builderType;

	protected BaseFileBuilder(String builderType) {
		this.builderType = builderType;
	}

	@SuppressWarnings("unchecked")
	private BUILDER self() {
		return (BUILDER) this;
	}

	/**
	 * Optionally adds a given comment to the for this builder. Useful for describing what the file or section is used for to people looking at the json file.
	 *
	 * @param comment Comment to add.
	 */
	public BUILDER comment(String comment) {
		Objects.requireNonNull(comment, "Comment defaults to null, remove unnecessary call.");
		if (this.comment != null) {
			throw new RuntimeException(builderType + " Builder already has a comment declared.");
		}
		this.comment = comment;
		return self();
	}

	/**
	 * @return {@code true} if this group has a comment, {@code false} otherwise.
	 */
	public boolean hasComment() {
		return comment != null;
	}
}
