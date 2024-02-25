package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class KleinStar extends ItemPE implements IItemEmcHolder, IBarHelper, ICapabilityAware {

	public final EnumKleinTier tier;

	public KleinStar(Properties props, EnumKleinTier tier) {
		super(props);
		this.tier = tier;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return stack.hasData(PEAttachmentTypes.STORED_EMC);
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		long starEmc = getEmc(stack);
		if (starEmc == 0) {
			return 1;
		}
		return (float) (1 - starEmc / (double) tier.maxEmc);
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return getColorForBar(stack);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide && !FMLEnvironment.production && player.isCreative()) {
			setEmc(stack, tier.maxEmc);
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	public enum EnumKleinTier {
		EIN("ein", 50_000),
		ZWEI("zwei", 200_000),
		DREI("drei", 800_000),
		VIER("vier", 3_200_000),
		SPHERE("sphere", 12_800_000),
		OMEGA("omega", 51_200_000);

		public final String name;
		public final long maxEmc;

		EnumKleinTier(String name, long maxEmc) {
			this.name = name;
			this.maxEmc = maxEmc;
		}
	}

	// -- IItemEmc -- //

	@Override
	public long insertEmc(@NotNull ItemStack stack, long toInsert, EmcAction action) {
		if (toInsert < 0) {
			return extractEmc(stack, -toInsert, action);
		}
		long toAdd = Math.min(getNeededEmc(stack), toInsert);
		if (action.execute()) {
			ItemPE.addEmcToStack(stack, toAdd);
		}
		return toAdd;
	}

	@Override
	public long extractEmc(@NotNull ItemStack stack, long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(stack, -toExtract, action);
		}
		long storedEmc = getStoredEmc(stack);
		long toRemove = Math.min(storedEmc, toExtract);
		if (action.execute()) {
			ItemPE.setEmc(stack, storedEmc - toRemove);
		}
		return toRemove;
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getStoredEmc(@NotNull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}

	@Override
	@Range(from = 1, to = Long.MAX_VALUE)
	public long getMaximumEmc(@NotNull ItemStack stack) {
		return tier.maxEmc;
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		IntegrationHelper.registerCuriosCapability(event, this);
	}
}