package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MindStone extends PEToggleItem implements IPedestalItem {

	private static final int TRANSFER_RATE = 50;

	public MindStone(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held) {
		if (world.isRemote || slot > 8 || !(entity instanceof PlayerEntity)) {
			return;
		}
		super.inventoryTick(stack, world, entity, slot, held);
		PlayerEntity player = (PlayerEntity) entity;
		if (ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE) && getXP(player) > 0) {
			int toAdd = Math.min(getXP(player), TRANSFER_RATE);
			addStoredXP(stack, toAdd);
			removeXP(player, TRANSFER_RATE);
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote && !stack.getOrCreateTag().getBoolean(Constants.NBT_KEY_ACTIVE) && getStoredXP(stack) != 0) {
			int toAdd = removeStoredXP(stack, TRANSFER_RATE);
			if (toAdd > 0) {
				addXP(player, toAdd);
			}
		}
		return ActionResult.resultSuccess(stack);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.addInformation(stack, world, tooltips, flags);
		if (stack.getTag() != null) {
			//TODO - 1.16: Number format
			tooltips.add(PELang.TOOLTIP_STORED_XP.translateColored(TextFormatting.DARK_GREEN, TextFormatting.GREEN, String.format("%,d", getStoredXP(stack))));
		}
	}


	private void removeXP(PlayerEntity player, int amount) {
		int totalExperience = getXP(player) - amount;
		if (totalExperience < 0) {
			player.experienceTotal = 0;
			player.experienceLevel = 0;
			player.experience = 0;
		} else {
			player.experienceTotal = totalExperience;
			player.experienceLevel = getLvlForXP(totalExperience);
			player.experience = (float) (totalExperience - getXPForLvl(player.experienceLevel)) / (float) player.xpBarCap();
		}
	}

	private void addXP(PlayerEntity player, int amount) {
		int experiencetotal = getXP(player) + amount;
		player.experienceTotal = experiencetotal;
		player.experienceLevel = getLvlForXP(experiencetotal);
		player.experience = (float) (experiencetotal - getXPForLvl(player.experienceLevel)) / (float) player.xpBarCap();
	}

	private int getXP(PlayerEntity player) {
		return (int) (getXPForLvl(player.experienceLevel) + player.experience * player.xpBarCap());
	}

	// Math referenced from the MC wiki
	private int getXPForLvl(int level) {
		if (level < 0) {
			return Integer.MAX_VALUE;
		}

		if (level <= 16) {
			return level * level + 6 * level;
		}

		if (level <= 31) {
			return (int) (level * level * 2.5D - 40.5D * level + 360.0D);
		}

		return (int) (level * level * 4.5D - 162.5D * level + 2220.0D);
	}

	private int getLvlForXP(int totalXP) {
		int result = 0;

		while (getXPForLvl(result) <= totalXP) {
			result++;
		}

		return --result;
	}

	private int getStoredXP(ItemStack stack) {
		return stack.hasTag() ? stack.getOrCreateTag().getInt(Constants.NBT_KEY_STORED_XP) : 0;
	}

	private void setStoredXP(ItemStack stack, int XP) {
		stack.getOrCreateTag().putInt(Constants.NBT_KEY_STORED_XP, XP);
	}

	private void addStoredXP(ItemStack stack, int XP) {
		long result = (long) getStoredXP(stack) + XP;
		if (result > Integer.MAX_VALUE) {
			result = Integer.MAX_VALUE;
		}
		setStoredXP(stack, (int) result);
	}

	private int removeStoredXP(ItemStack stack, int XP) {
		int currentXP = getStoredXP(stack);
		int result;
		int returnResult;

		if (currentXP < XP) {
			result = 0;
			returnResult = currentXP;
		} else {
			result = currentXP - XP;
			returnResult = XP;
		}

		setStoredXP(stack, result);
		return returnResult;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
		if (tile != null) {
			List<ExperienceOrbEntity> orbs = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, tile.getEffectBounds());
			for (ExperienceOrbEntity orb : orbs) {
				WorldHelper.gravitateEntityTowards(orb, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
				if (!world.isRemote && orb.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21) {
					suckXP(orb, tile.getInventory().getStackInSlot(0));
				}
			}
		}
	}

	private void suckXP(ExperienceOrbEntity orb, ItemStack mindStone) {
		long l = getStoredXP(mindStone);
		if (l + orb.xpValue > Integer.MAX_VALUE) {
			orb.xpValue = (int) (l + orb.xpValue - Integer.MAX_VALUE);
			setStoredXP(mindStone, Integer.MAX_VALUE);
		} else {
			addStoredXP(mindStone, orb.xpValue);
			orb.remove();
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		return Lists.newArrayList(PELang.PEDESTAL_MIND_STONE.translateColored(TextFormatting.BLUE));
	}
}