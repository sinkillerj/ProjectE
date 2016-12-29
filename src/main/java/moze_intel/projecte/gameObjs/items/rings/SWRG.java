package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.items.IFlightProvider;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class SWRG extends ItemPE implements IBauble, IPedestalItem, IFlightProvider, IProjectileShooter
{
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
			WorldHelper.repelEntitiesInAABBFromPoint(player.getEntityWorld(), player.getEntityBoundingBox().expand(5.0, 5.0, 5.0), player.posX, player.posY, player.posZ, true);
		}

		if (player.getEntityWorld().isRemote)
		{
			return;
		}

		EntityPlayerMP playerMP = (EntityPlayerMP) player;

		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}

		if (getEmc(stack) == 0 && !consumeFuel(player, stack, 64, false))
		{
			if (stack.getItemDamage() > 0)
			{
				changeMode(stack, 0);
			}

			if (playerMP.capabilities.allowFlying)
			{
				playerMP.getCapability(InternalAbilities.CAPABILITY, null).disableSwrgFlightOverride();
			}

			return;
		}

		if (!playerMP.capabilities.allowFlying)
		{
			playerMP.getCapability(InternalAbilities.CAPABILITY, null).enableSwrgFlightOverride();
		}

		if (playerMP.capabilities.isFlying)
		{
			if (!isFlyingEnabled(stack))
			{
				changeMode(stack, stack.getItemDamage() == 0 ? 1 : 3);
			}
		}
		else
		{
			if (isFlyingEnabled(stack))
			{
				changeMode(stack, stack.getItemDamage() == 1 ? 0 : 2);
			}
		}

		float toRemove = 0;

		if (playerMP.capabilities.isFlying)
		{
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

		removeEmc(stack, toRemove);

		playerMP.fallDistance = 0;
	}

	private boolean isFlyingEnabled(ItemStack stack)
	{
		return stack.getItemDamage() == 1 || stack.getItemDamage() == 3;
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
	
	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
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
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
	public boolean canProvideFlight(ItemStack stack, EntityPlayerMP player)
	{
		// Dummy result - swrg needs special-casing
		return false;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return false;
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
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.swrgPedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() <= 0)
			{
				List<EntityLiving> list = world.getEntitiesWithinAABB(EntityLiving.class, tile.getEffectBounds());
				for (EntityLiving living : list)
				{
					if (living instanceof EntityTameable && ((EntityTameable) living).isTamed())
					{
						continue;
					}
					world.addWeatherEffect(new EntityLightningBolt(world, living.posX, living.posY, living.posZ, false));
				}
				tile.setActivityCooldown(ProjectEConfig.swrgPedCooldown);
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.swrgPedCooldown != -1)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.swrg.pedestal1"));
			list.add(TextFormatting.BLUE + I18n.format("pe.swrg.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.swrgPedCooldown)));
		}
		return list;
	}

	@Override
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand)
	{
		EntitySWRGProjectile projectile = new EntitySWRGProjectile(player.worldObj, player, false);
		projectile.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		player.worldObj.spawnEntityInWorld(projectile);
		// projectile.playSound(PESounds.WIND, 1.0F, 1.0F);
		return true;
	}
}
