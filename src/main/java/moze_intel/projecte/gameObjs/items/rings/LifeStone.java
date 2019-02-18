package moze_intel.projecte.gameObjs.items.rings;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

// todo 1.13 @Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class LifeStone extends RingToggle implements IPedestalItem
{
	public LifeStone(Properties props)
	{
		super(props);
	}
	

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean held)
	{
		if (world.isRemote || slot > 8 || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		super.inventoryTick(stack, world, entity, slot, held);
		
		EntityPlayer player = (EntityPlayer) entity;

        if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			if (!consumeFuel(player, stack, 2*64, false))
			{
				stack.getTag().putBoolean(TAG_ACTIVE, false);
			}
			else
			{
				player.getCapability(InternalTimers.CAPABILITY, null).ifPresent(timers -> {
					timers.activateFeed();
					if (player.getFoodStats().needFood() && timers.canFeed())
					{
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1, 1);
						player.getFoodStats().addStats(2, 10);
						removeEmc(stack, 64);
					}

					timers.activateHeal();
					if (player.getHealth() < player.getMaxHealth() && timers.canHeal())
					{
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.PLAYERS, 1, 1);
						player.heal(2.0F);
						removeEmc(stack, 64);
					}
				});
			}
		}
	}
	
	@Override
	public boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
        NBTTagCompound tag  = stack.getOrCreateTag();
		tag.putBoolean(TAG_ACTIVE, !tag.getBoolean(TAG_ACTIVE));
		return true;
	}
	/* todo 1.13
	@Override
	@Optional.Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.inventoryTick(stack, player.getEntityWorld(), player, 0, false);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canEquip(ItemStack stack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}
	*/

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.life.get() != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			if(!(te instanceof DMPedestalTile))
			{
				return;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0)
			{
				List<EntityPlayerMP> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, tile.getEffectBounds());

				for (EntityPlayerMP player : players)
				{
					if (player.getHealth() < player.getMaxHealth())
					{
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.BLOCKS, 1, 1);
						player.heal(1.0F); // 1/2 heart
					}
					if (player.getFoodStats().needFood())
					{
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.HEAL, SoundCategory.BLOCKS, 1, 1);
						player.getFoodStats().addStats(1, 1); // 1/2 shank
					}
				}

				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.life.get());
			}
			else
			{
				tile.decrementActivityCooldown();
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription()
	{
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.life.get() != -1)
		{
			list.add(new TextComponentTranslation("pe.life.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TextComponentTranslation("pe.life.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.life.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}
}
