package moze_intel.projecte.utils;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.utils.AnnotationHelper.PrioritizedElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test annotation-loaded element ordering")
class AnnotationHelperTest {

	@Test
	@DisplayName("Higher priorities run first and equal priorities retain discovery order")
	void testPriorityAndDiscoveryOrder() {
		Object first = new Object();
		Object second = new Object();
		Object highPriority = new Object();
		List<PrioritizedElement<Object>> elements = new ArrayList<>(List.of(
				new PrioritizedElement<>(second, 0, 1),
				new PrioritizedElement<>(first, 0, 0),
				new PrioritizedElement<>(highPriority, 10, 2)
		));

		List<Object> sorted = AnnotationHelper.sortByPriority(elements);

		Assertions.assertSame(highPriority, sorted.get(0));
		Assertions.assertSame(first, sorted.get(1));
		Assertions.assertSame(second, sorted.get(2));
	}

	@Test
	@DisplayName("Equal mapper objects retain their own priorities")
	void testEqualObjectsDoNotCollide() {
		EqualElement lowPriority = new EqualElement("low");
		EqualElement highPriority = new EqualElement("high");
		List<PrioritizedElement<EqualElement>> elements = new ArrayList<>(List.of(
				new PrioritizedElement<>(lowPriority, -10, 0),
				new PrioritizedElement<>(highPriority, 10, 1)
		));

		List<EqualElement> sorted = AnnotationHelper.sortByPriority(elements);

		Assertions.assertSame(highPriority, sorted.get(0));
		Assertions.assertSame(lowPriority, sorted.get(1));
	}

	private record EqualElement(String name) {

		@Override
		public boolean equals(Object obj) {
			return obj instanceof EqualElement;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}
}
