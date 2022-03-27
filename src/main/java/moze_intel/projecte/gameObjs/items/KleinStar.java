package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Range;

public class KleinStar extends ItemPE implements IItemEmcHolder, IBarHelper {

	public final EnumKleinTier tier;

	public KleinStar(Properties props, EnumKleinTier tier) {
		super(props);
		this.tier = tier;
		addItemCapability(EmcHolderItemCapabilityWrapper::new);
		addItemCapability(IntegrationHelper.CURIO_MODID, IntegrationHelper.CURIO_CAP_SUPPLIER);
	}

	@Override
	public boolean isBarVisible(@Nonnull ItemStack stack) {
		return stack.hasTag();
	}

	@Override
	public float getWidthForBar(ItemStack stack) {
		long starEmc = getEmc(stack);
		if (starEmc == 0) {
			return 1;
		}
		return (float) (1 - starEmc / (double) EMCHelper.getKleinStarMaxEmc(stack));
	}

	@Override
	public int getBarWidth(@Nonnull ItemStack stack) {
		return getScaledBarWidth(stack);
	}

	@Override
	public int getBarColor(@Nonnull ItemStack stack) {
		return getColorForBar(stack);
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide && !FMLEnvironment.production) {
			setEmc(stack, EMCHelper.getKleinStarMaxEmc(stack));
			return InteractionResultHolder.success(stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	public enum EnumKleinTier {
		EIN("ein"),
		ZWEI("zwei"),
		DREI("drei"),
		VIER("vier"),
		SPHERE("sphere"),
		OMEGA("omega");

		public final String name;

		EnumKleinTier(String name) {
			this.name = name;
		}
	}

	// -- IItemEmc -- //

	@Override
	public long insertEmc(@Nonnull ItemStack stack, long toInsert, EmcAction action) {
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
	public long extractEmc(@Nonnull ItemStack stack, long toExtract, EmcAction action) {
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
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}

	@Override
	@Range(from = 1, to = Long.MAX_VALUE)
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return EMCHelper.getKleinStarMaxEmc(stack);
	}
}