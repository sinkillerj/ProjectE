package moze_intel.projecte.gameObjs.items.rings;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class MindStone extends RingToggle implements IPedestalItem
{
	private static final int TRANSFER_RATE = 50;

	public MindStone() 
	{
		super("mind_stone");
		this.setNoRepair();
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (world.isRemote || par4 > 8 || !(entity instanceof EntityPlayer))
		{
			return;
		}

		super.onUpdate(stack, world, entity, par4, par5);

		EntityPlayer player = (EntityPlayer) entity;

		if (ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE))
		{
			if (getXP(player) > 0)
			{
				int toAdd = getXP(player) >= TRANSFER_RATE ? TRANSFER_RATE : getXP(player);
				addStoredXP(stack, toAdd);
				removeXP(player, TRANSFER_RATE);
			}
		}
	}
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote && !ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE) && getStoredXP(stack) != 0)
		{
			int toAdd = removeStoredXP(stack, TRANSFER_RATE);
			
			if (toAdd > 0)
			{
				addXP(player, toAdd);
			}
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flags)
	{
		if(stack.getTagCompound() != null)
			tooltip.add(String.format(TextFormatting.DARK_GREEN + I18n.format("pe.misc.storedxp_tooltip") + " " + TextFormatting.GREEN + "%,d", getStoredXP(stack)));
	}


	private void removeXP(EntityPlayer player, int amount)
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

	private void addXP(EntityPlayer player, int amount)
	{
		int experiencetotal = getXP(player) + amount;
		player.experienceTotal = experiencetotal;
		player.experienceLevel = getLvlForXP(experiencetotal);
		player.experience = (float)(experiencetotal - getXPForLvl(player.experienceLevel)) / (float)player.xpBarCap();
	}

	private int getXP(EntityPlayer player)
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
		return ItemHelper.getOrCreateCompound(stack).getInteger("StoredXP");
	}

	private void setStoredXP(ItemStack stack, int XP)
	{
		ItemHelper.getOrCreateCompound(stack).setInteger("StoredXP", XP);
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
		List<EntityXPOrb> orbs = world.getEntitiesWithinAABB(EntityXPOrb.class, tile.getEffectBounds());
		for (EntityXPOrb orb : orbs)
		{
			WorldHelper.gravitateEntityTowards(orb, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			if (!world.isRemote && orb.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21)
			{
				suckXP(orb, tile.getInventory().getStackInSlot(0));
			}
		}

	}

	private void suckXP(EntityXPOrb orb, ItemStack mindStone)
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
			orb.setDead();
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		return Lists.newArrayList(I18n.format("pe.mind.pedestal1"));
	}
}
