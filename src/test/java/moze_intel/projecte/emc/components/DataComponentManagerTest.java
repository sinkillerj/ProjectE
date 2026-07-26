package moze_intel.projecte.emc.components;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.components.processor.ContainerProcessor;
import moze_intel.projecte.emc.components.processor.DamageProcessor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test data component EMC resolution")
class DataComponentManagerTest {

	@BeforeEach
	void setup() {
		Assertions.assertTrue(DataComponentManager.loadProcessors().contains(DamageProcessor.INSTANCE),
				"The damage processor must be available so this regression test exercises component recalculation");
		Assertions.assertTrue(DataComponentManager.loadProcessors().stream().anyMatch(ContainerProcessor.class::isInstance),
				"The container processor must be available so these tests exercise stored-item valuation");
	}

	@AfterEach
	void cleanup() {
		EMCMappingHandler.clearEmcMap();
	}

	@Test
	@DisplayName("An exact component-bearing mapping is not processed a second time")
	void testExactComponentMappingIsAuthoritative() {
		ItemInfo base = ItemInfo.fromItem(Items.DIAMOND_PICKAXE);
		ItemInfo damaged = createDamagedPickaxe(100);
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(base, 8_192);
		values.put(damaged, 12_345);
		EMCMappingHandler.updateEmcValues(values);

		Assertions.assertEquals(12_345, DataComponentManager.getEmcValue(damaged),
				"Processors must not double count state already represented by an exact mapping");
	}

	@Test
	@DisplayName("Component processors still apply when only the base item is mapped")
	void testComponentProcessorsApplyToBaseFallback() {
		ItemInfo base = ItemInfo.fromItem(Items.DIAMOND_PICKAXE);
		ItemInfo damaged = createDamagedPickaxe(100);
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(base, 8_192);
		EMCMappingHandler.updateEmcValues(values);

		ItemStack stack = damaged.createStack();
		long expected = Math.multiplyExact(8_192, stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();
		Assertions.assertEquals(expected, DataComponentManager.getEmcValue(damaged),
				"Processors must still calculate component state when there is no exact mapping");
	}

	@Test
	@DisplayName("Damage scaling does not overflow when the discounted value fits in a long")
	void testDamageScalingAvoidsIntermediateOverflow() {
		ItemInfo base = ItemInfo.fromItem(Items.DIAMOND_PICKAXE);
		ItemInfo damaged = createDamagedPickaxe(1);
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(base, Long.MAX_VALUE);
		EMCMappingHandler.updateEmcValues(values);

		ItemStack stack = damaged.createStack();
		long expected = BigInteger.valueOf(Long.MAX_VALUE)
				.multiply(BigInteger.valueOf(stack.getMaxDamage() - stack.getDamageValue()))
				.divide(BigInteger.valueOf(stack.getMaxDamage()))
				.longValueExact();
		Assertions.assertEquals(expected, DataComponentManager.getEmcValue(damaged),
				"A durability discount must not disappear because only the intermediate multiplication exceeds long range");
	}

	@Test
	@DisplayName("Items damaged beyond their maximum durability fail closed")
	void testOverDamagedItemFailsClosed() {
		ItemInfo base = ItemInfo.fromItem(Items.DIAMOND_PICKAXE);
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(base, 8_192);
		EMCMappingHandler.updateEmcValues(values);

		ItemStack malformed = new ItemStack(Items.DIAMOND_PICKAXE);
		malformed.set(DataComponents.MAX_DAMAGE, 100);
		malformed.set(DataComponents.DAMAGE, 101);
		Assertions.assertEquals(0, DataComponentManager.getEmcValue(ItemInfo.fromStack(malformed)),
				"A malformed over-damaged stack must not retain the full base EMC value");
	}

	@Test
	@DisplayName("A broken third-party processor fails closed without publishing partial EMC")
	void testUnexpectedProcessorFailureFailsClosed() throws ReflectiveOperationException {
		ItemInfo base = ItemInfo.fromItem(Items.DIAMOND_PICKAXE);
		ItemInfo damaged = createDamagedPickaxe(1);
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(base, 8_192);
		EMCMappingHandler.updateEmcValues(values);

		AtomicBoolean laterProcessorRan = new AtomicBoolean();
		IDataComponentProcessor failing = new TestProcessor("failing") {
			@Override
			public long recalculateEMC(ItemInfo info, long currentEMC) {
				throw new IllegalStateException("broken integration");
			}
		};
		IDataComponentProcessor later = new TestProcessor("later") {
			@Override
			public long recalculateEMC(ItemInfo info, long currentEMC) {
				laterProcessorRan.set(true);
				return currentEMC + 1;
			}
		};

		List<IDataComponentProcessor> processors = mutableProcessors();
		processors.addFirst(later);
		processors.addFirst(failing);
		try {
			Assertions.assertEquals(0, DataComponentManager.getEmcValue(damaged),
					"An unexpected processor exception must fail the complete item value closed");
			Assertions.assertFalse(laterProcessorRan.get(),
					"Processors after a failure must not observe or publish a partially calculated value");
		} finally {
			processors.remove(failing);
			processors.remove(later);
		}
	}

	@Test
	@DisplayName("Container contents are added exactly once")
	void testContainerContentsAreAddedExactlyOnce() {
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(ItemInfo.fromItem(Items.SHULKER_BOX), 100);
		values.put(ItemInfo.fromItem(Items.STONE), 4);
		EMCMappingHandler.updateEmcValues(values);

		ItemInfo container = createContainer(new ItemStack(Items.STONE, 3));
		Assertions.assertEquals(112, DataComponentManager.getEmcValue(container),
				"The container base and each stored item must be counted once");
	}

	@Test
	@DisplayName("Containers with unvalued contents fail closed")
	void testContainerWithUnvaluedContentsFailsClosed() {
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(ItemInfo.fromItem(Items.SHULKER_BOX), 100);
		EMCMappingHandler.updateEmcValues(values);

		ItemInfo container = createContainer(new ItemStack(Items.DIRT));
		Assertions.assertEquals(0, DataComponentManager.getEmcValue(container),
				"A container must not receive a guessed partial value when any stored item lacks EMC");
	}

	@Test
	@DisplayName("Nested container contents compose without double counting")
	void testNestedContainerContentsCompose() {
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(ItemInfo.fromItem(Items.SHULKER_BOX), 100);
		values.put(ItemInfo.fromItem(Items.STONE), 4);
		EMCMappingHandler.updateEmcValues(values);

		ItemStack inner = createContainerStack(new ItemStack(Items.STONE, 2));
		ItemInfo outer = createContainer(inner);
		Assertions.assertEquals(208, DataComponentManager.getEmcValue(outer),
				"The outer base, inner base, and nested contents must each be counted once");
	}

	@Test
	@DisplayName("Damage discounts the container base but not stored contents")
	void testDamageAndContainerProcessorsComposeSafely() {
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(ItemInfo.fromItem(Items.SHULKER_BOX), 100);
		values.put(ItemInfo.fromItem(Items.STONE), 4);
		EMCMappingHandler.updateEmcValues(values);

		ItemStack container = createContainerStack(new ItemStack(Items.STONE, 3));
		container.set(DataComponents.MAX_DAMAGE, 100);
		container.set(DataComponents.DAMAGE, 50);
		Assertions.assertEquals(62, DataComponentManager.getEmcValue(ItemInfo.fromStack(container)),
				"Durability must discount only the container item before stored contents are added");
	}

	@Test
	@DisplayName("Container value overflow fails closed")
	void testContainerValueOverflowFailsClosed() {
		Object2LongMap<ItemInfo> values = new Object2LongOpenHashMap<>();
		values.put(ItemInfo.fromItem(Items.SHULKER_BOX), 1);
		values.put(ItemInfo.fromItem(Items.STONE), Long.MAX_VALUE);
		EMCMappingHandler.updateEmcValues(values);

		ItemInfo container = createContainer(new ItemStack(Items.STONE, 2));
		Assertions.assertEquals(0, DataComponentManager.getEmcValue(container),
				"An unrepresentable nested value must fail closed rather than wrap or publish partial EMC");
	}

	@SuppressWarnings("unchecked")
	private List<IDataComponentProcessor> mutableProcessors() throws ReflectiveOperationException {
		Field field = DataComponentManager.class.getDeclaredField("processors");
		field.setAccessible(true);
		return (List<IDataComponentProcessor>) field.get(null);
	}

	private abstract static class TestProcessor implements IDataComponentProcessor {

		private final String name;

		private TestProcessor(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return "test-" + name;
		}

		@Override
		public String getTranslationKey() {
			return "test." + name;
		}

		@Override
		public String getDescription() {
			return name;
		}
	}

	private ItemInfo createContainer(ItemStack... contents) {
		return ItemInfo.fromStack(createContainerStack(contents));
	}

	private ItemStack createContainerStack(ItemStack... contents) {
		ItemStack container = new ItemStack(Items.SHULKER_BOX);
		container.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(contents)));
		return container;
	}

	private ItemInfo createDamagedPickaxe(int damage) {
		ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
		stack.setDamageValue(damage);
		return ItemInfo.fromStack(stack);
	}
}
