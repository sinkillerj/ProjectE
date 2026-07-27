package moze_intel.projecte.emc;

import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test graph mapper conversion diagnostics")
class SimpleGraphMapperDiagnosticsTest {

	private IValueArithmetic<BigFraction> arithmetic;

	@BeforeEach
	void setup() {
		arithmetic = new HiddenBigFractionArithmetic();
	}

	@Test
	@DisplayName("Test multiple outputs are compared by total value")
	void testProfitableMultipleOutputConversion() {
		assertComparison(SimpleGraphMapper.ConversionValueRelation.PROFITABLE, 17, 16, 2, 32);
	}

	@Test
	@DisplayName("Test output loss is not classified as profitable")
	void testLossConversion() {
		assertComparison(SimpleGraphMapper.ConversionValueRelation.LOSS, 17, 8, 2, 16);
	}

	@Test
	@DisplayName("Test exact conversion totals are classified exactly")
	void testExactConversion() {
		assertComparison(SimpleGraphMapper.ConversionValueRelation.EXACT, 18, 9, 2, 18);
	}

	private void assertComparison(SimpleGraphMapper.ConversionValueRelation expectedRelation, long ingredientCost, long outputValue,
			int outputCount, long expectedTotalOutputValue) {
		SimpleGraphMapper.ConversionValueComparison<BigFraction> comparison = SimpleGraphMapper.compareConversionValues(
				arithmetic, new BigFraction(ingredientCost), new BigFraction(outputValue), outputCount
		);

		Assertions.assertEquals(expectedRelation, comparison.relation());
		Assertions.assertEquals(new BigFraction(ingredientCost), comparison.ingredientCost());
		Assertions.assertEquals(new BigFraction(outputValue), comparison.outputValue());
		Assertions.assertEquals(outputCount, comparison.outputCount());
		Assertions.assertEquals(new BigFraction(expectedTotalOutputValue), comparison.totalOutputValue());
	}

	@Test
	@DisplayName("Test invalid output counts are rejected by diagnostics")
	void testInvalidOutputCount() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> SimpleGraphMapper.compareConversionValues(
				arithmetic, BigFraction.ONE, BigFraction.ONE, 0
		));
	}
}
