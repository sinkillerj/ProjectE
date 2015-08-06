package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class SWRG extends ItemPE implements IBauble, IPedestalItem, IFlightProvider
{
	@SideOnly(Side.CLIENT)
	private IIcon ringOff;
	@SideOnly(Side.CLIENT)
	private IIcon[] ringOn;

	public SWRG()
	{
		this.setUnlocalizedName("swrg");
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setNoRepair();
	}

	private void tick(ItemStack stack, EntityPlayer player)
	{
		if (stack.getItemDamage() > 1)
		{
			// Repel on both sides - smooth animation
			WorldHelper.repelEntitiesInAABBFromPoint(player.worldObj, player.boundingBox.expand(5.0, 5.0, 5.0), player.posX, player.posY, player.posZ, true);
		}

		if (player.worldObj.isRemote)
		{
			return;
		}

		EntityPlayerMP playerMP = (EntityPlayerMP) player;

		if (!stack.hasTagCompound())
		{
			stack.stackTagCompound = new NBTTagCompound();
		}

		if (!playerMP.capabilities.isFlying || playerMP.onGround)
		{
			if (stack.getItemDamage() == 3)
			{
				changeMode(stack, 2);
			} else if (stack.getItemDamage() == 1)
			{
				changeMode(stack, 0);
			}
		}

		// Pre-consume a bit of fuel so canProvideFlight can return true, for auto-activate
		if (getEmc(stack) <= 2.0 && !consumeFuel(player, stack, 64, true))
		{
			changeMode(stack, 0);
		}

		if (stack.getItemDamage() == 0)
		{
			return;
		}

		float toRemove = 0;

		if (playerMP.capabilities.isFlying)
		{
			if (stack.getItemDamage() == 0)
			{
				changeMode(stack, 1);
			} else if (stack.getItemDamage() == 2)
			{
				changeMode(stack, 3);
			}
			toRemove = 0.32F;
		}

		if (stack.getItemDamage() == 2)
		{
			toRemove = 0.32F;
		}
		else if (stack.getItemDamage() == 3)
		{
			toRemove = 0.64F;
		}

		if (!consumeFuel(playerMP, stack, toRemove, true) || getEmc(stack) <= 2.0)
		{
			changeMode(stack, 0);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean isHeldItem) 
	{
		if (invSlot > 8 || !(entity instanceof EntityPlayer))
		{
			return;
		}
		tick(stack, ((EntityPlayer) entity));
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			int newMode = 0;
			
			switch (stack.getItemDamage())
			{
				case 0:
					newMode = 2;
					break;
				case 1:
					newMode = 3;
					break;
				case 2:
					newMode = 0;
					break;
				case 3:
					newMode = 1;
					break;
			}
			
			changeMode(stack, newMode);
		}
		return stack;
	}

	/**
	 * Change the mode of SWRG. Modes:<p>
	 * 0 = Ring Off<p>  
	 * 1 = Flight<p>
	 * 2 = Shield<p>
	 * 3 = Flight + Shield<p>
	 */
	public void changeMode(ItemStack stack, int mode)
	{
		stack.setItemDamage(mode);
	}

	@Override
	public boolean canProvideFlight(ItemStack stack)
	{
		boolean flag = getEmc(stack) > 2.0;
		if (System.currentTimeMillis() % 50 == 0)
		{
			System.out.println("SWRG canProvideFlight: " + flag);
		}
		return flag;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		if (dmg == 0)
		{
			return ringOff;
		}
		
		else
		{
			return ringOn[MathHelper.clamp_int(dmg - 1, 0, 2)];
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		ringOff = register.registerIcon(this.getTexture("rings", "swrg_off"));
		ringOn = new IIcon[3];
		
		for (int i = 0; i < 3; i++)
		{
			ringOn[i] = register.registerIcon(this.getTexture("rings", "swrg_on"+(i+1)));
		}
	}
	
	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.RING;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase ent) 
	{
		if (!(ent instanceof EntityPlayer))
		{
			return;
		}
		tick(stack, ((EntityPlayer) ent));
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack stack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public void updateInPedestal(World world, int x, int y, int z)
	{
		if (!world.isRemote && ProjectEConfig.swrgPedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(x, y, z));
			if (tile.getActivityCooldown() <= 0)
			{
				List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, tile.getEffectBounds());
				for (EntityLiving living : list)
				{
					world.addWeatherEffect(new EntityLightningBolt(world, living.posX, living.posY, living.posZ));
				}
				tile.setActivityCooldown(ProjectEConfig.swrgPedCooldown);
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.swrgPedCooldown != -1)
		{
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.swrg.pedestal1"));
			list.add(EnumChatFormatting.BLUE + String.format(
					StatCollector.translateToLocal("pe.swrg.pedestal2"), MathUtils.tickToSecFormatted(ProjectEConfig.swrgPedCooldown)));
		}
		return list;
	}
}
