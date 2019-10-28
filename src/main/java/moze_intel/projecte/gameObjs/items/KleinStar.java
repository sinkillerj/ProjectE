package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.capability.EmcHolderItemCapabilityWrapper;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class KleinStar extends ItemPE implements IItemEmcHolder {

	public final EnumKleinTier tier;

	public KleinStar(Properties props, EnumKleinTier tier) {
		super(props);
		this.tier = tier;
		addItemCapability(new EmcHolderItemCapabilityWrapper());
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.hasTag();
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		long starEmc = getEmc(stack);

		if (starEmc == 0) {
			return 1.0D;
		}

		return 1.0D - starEmc / (double) EMCHelper.getKleinStarMaxEmc(stack);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote && PECore.DEV_ENVIRONMENT) {
			setEmc(stack, EMCHelper.getKleinStarMaxEmc(stack));
			return ActionResult.newResult(ActionResultType.SUCCESS, stack);
		}

		return ActionResult.newResult(ActionResultType.PASS, stack);
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
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}

	@Override
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return EMCHelper.getKleinStarMaxEmc(stack);
	}
}