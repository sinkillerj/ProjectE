package moze_intel.projecte.emc.json;

import javax.annotation.Nonnull;

public class NSSFake implements NormalizedSimpleStack {
	private static int fakeItemCounter = 0;
	public final int counter;
	private final String description;

	private NSSFake(String description) {
		this.counter = fakeItemCounter++;
		this.description = description;
	}

	@Nonnull
	public static NormalizedSimpleStack create(String description) {
		return new NSSFake(description);
	}

	@Override
	public boolean equals(Object o) {
		return o == this;
	}

	@Override
	public String json() {
		return "FAKE|" + this.counter + " " + this.description;
	}

	@Override
	public String toString() {
		return "NSSFAKE" + counter + ": " + description;
	}
}
