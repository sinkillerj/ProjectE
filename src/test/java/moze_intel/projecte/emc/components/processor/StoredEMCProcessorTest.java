package moze_intel.projecte.emc.components.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test stored EMC component validation")
class StoredEMCProcessorTest {

	@Test
	@DisplayName("Non-negative stored EMC is added exactly")
	void testStoredEmcIsAdded() {
		Assertions.assertEquals(125, StoredEMCProcessor.addStoredEmc(100, 25));
		Assertions.assertEquals(100, StoredEMCProcessor.addStoredEmc(100, 0));
	}

	@Test
	@DisplayName("Negative stored EMC fails closed")
	void testNegativeStoredEmcIsRejected() {
		Assertions.assertThrows(ArithmeticException.class, () -> StoredEMCProcessor.addStoredEmc(100, -1),
				"A broken capability must not use negative stored EMC to reduce an otherwise valid item value");
	}

	@Test
	@DisplayName("Stored EMC addition overflow fails closed")
	void testStoredEmcOverflowIsRejected() {
		Assertions.assertThrows(ArithmeticException.class, () -> StoredEMCProcessor.addStoredEmc(Long.MAX_VALUE, 1));
	}
}
