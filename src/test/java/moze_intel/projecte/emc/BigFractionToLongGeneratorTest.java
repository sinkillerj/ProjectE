package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test final BigFraction to long publication")
class BigFractionToLongGeneratorTest {

	@Test
	@DisplayName("Test publication truncates positive fractions and enforces long bounds")
	void testPublicationBoundaries() {
		Map<String, BigFraction> generated = new LinkedHashMap<>();
		generated.put("fraction", new BigFraction(7, 2));
		generated.put("subunit", new BigFraction(1, 2));
		generated.put("zero", BigFraction.ZERO);
		generated.put("negative", new BigFraction(-1));
		generated.put("max", new BigFraction(Long.MAX_VALUE));
		generated.put("overflow", new BigFraction(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE)));

		Object2LongMap<String> values = new BigFractionToLongGenerator<String>(() -> generated).generateValues();
		Assertions.assertEquals(3, values.getLong("fraction"));
		Assertions.assertFalse(values.containsKey("subunit"));
		Assertions.assertFalse(values.containsKey("zero"));
		Assertions.assertFalse(values.containsKey("negative"));
		Assertions.assertEquals(Long.MAX_VALUE, values.getLong("max"));
		Assertions.assertFalse(values.containsKey("overflow"));
	}
}
