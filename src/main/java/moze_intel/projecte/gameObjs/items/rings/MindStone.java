package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MindStone extends PEToggleItem implements IPedestalItem
{
	private static final int TRANSFER_RATE = 50;

	public MindStone(Properties props) {
		super(props);
		addItemCapability(new PedestalItemCapabilityWrapper());
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held)
	{
		if (world.isRemote || slot > 8 || !(entity instanceof PlayerEntity))
		{
			return;
		}

		super.inventoryTick(stack, world, entity, slot, held);

		PlayerEntity player = (PlayerEntity) entity;

        if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			if (getXP(player) > 0)
			{
				int toAdd = Math.min(getXP(player), TRANSFER_RATE);
				addStoredXP(stack, toAdd);
				removeXP(player, TRANSFER_RATE);
			}
		}
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && !stack.getOrCreateTag().getBoolean(TAG_ACTIVE) && getStoredXP(stack) != 0)
		{
			int toAdd = removeStoredXP(stack, TRANSFER_RATE);
			
			if (toAdd > 0)
			{
				addXP(player, toAdd);
			}
		}
		
		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flags)
	{
		if(stack.getTag() != null)
		{
			ITextComponent label = new TranslationTextComponent("pe.misc.storedxp_tooltip").setStyle(new Style().setColor(TextFormatting.DARK_GREEN));
			ITextComponent value = new StringTextComponent(String.format("%,d", getStoredXP(stack))).setStyle(new Style().setColor(TextFormatting.GREEN));
			tooltip.add(label.appendText(" ").appendSibling(value));
		}

	}


	private void removeXP(PlayerEntity player, int amount)
	{
		int experiencetotal = getXP(player) - amount;
		
		if (experiencetotal < 0)
		{
			player.experienceTotal = 0;
			player.experienceLevel = 0;
			player.experience = 0;
		}
		else
		{
			player.experienceTotal = experiencetotal;
			player.experienceLevel = getLvlForXP(experiencetotal);
			player.experience = (float)(experiencetotal - getXPForLvl(player.experienceLevel)) / (float)player.xpBarCap();
		}
	}

	private void addXP(PlayerEntity player, int amount)
	{
		int experiencetotal = getXP(player) + amount;
		player.experienceTotal = experiencetotal;
		player.experienceLevel = getLvlForXP(experiencetotal);
		player.experience = (float)(experiencetotal - getXPForLvl(player.experienceLevel)) / (float)player.xpBarCap();
	}

	private int getXP(PlayerEntity player)
	{
		return (int)(getXPForLvl(player.experienceLevel) + (player.experience * player.xpBarCap()));
	}

	// Math referenced from the MC wiki
	private int getXPForLvl(int level) 
	{
		if (level < 0) 
		{
			return Integer.MAX_VALUE;
		}

		if (level <= 16)
		{
			return level * level + 6 * level;
		}

		if (level <= 31)
		{
			return (int) (((level * level) * 2.5D) - (40.5D * level) + 360.0D);
		}

		return (int) (((level * level) * 4.5D) - (162.5D * level) + 2220.0D);
	}

	private int getLvlForXP(int totalXP)
	{
		int result = 0;

		while (getXPForLvl(result) <= totalXP) 
		{
			result++;
		}

		return --result;
	}
	
	private int getStoredXP(ItemStack stack)
	{
        return stack.getOrCreateTag().getInt("StoredXP");
	}

	private void setStoredXP(ItemStack stack, int XP)
	{
        stack.getOrCreateTag().putInt("StoredXP", XP);
	}

	private void addStoredXP(ItemStack stack, int XP) 
	{
		long result = getStoredXP(stack) + XP;
		
		if (result > Integer.MAX_VALUE)
		{
			result = Integer.MAX_VALUE;
		}
		
		setStoredXP(stack, (int) result);
	}

	private int removeStoredXP(ItemStack stack, int XP) 
	{
		int currentXP = getStoredXP(stack);
		int result;
		int returnResult;
		
		if (currentXP < XP)
		{
			result = 0;
			returnResult = currentXP;
		}
		else
		{
			result = currentXP - XP;
			returnResult = XP;
		}
		
		setStoredXP(stack, result);
		return returnResult;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof DMPedestalTile))
		{
			return;
		}
		DMPedestalTile tile = (DMPedestalTile) te;
		List<ExperienceOrbEntity> orbs = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, tile.getEffectBounds());
		for (ExperienceOrbEntity orb : orbs)
		{
			WorldHelper.gravitateEntityTowards(orb, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			if (!world.isRemote && orb.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21)
			{
				suckXP(orb, tile.getInventory().getStackInSlot(0));
			}
		}

	}

	private void suckXP(ExperienceOrbEntity orb, ItemStack mindStone)
	{
		long l = getStoredXP(mindStone);
		if (l + orb.xpValue > Integer.MAX_VALUE)
		{
			orb.xpValue = ((int) (l + orb.xpValue - Integer.MAX_VALUE));
			setStoredXP(mindStone, Integer.MAX_VALUE);
		}
		else
		{
			addStoredXP(mindStone, orb.xpValue);
			orb.remove();
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription()
	{
		return Lists.newArrayList(new TranslationTextComponent("pe.mind.pedestal1"));
	}
}
