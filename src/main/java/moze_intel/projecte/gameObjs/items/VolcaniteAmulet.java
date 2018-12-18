package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.FluidHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class VolcaniteAmulet extends ItemPE implements IProjectileShooter, IBauble, IPedestalItem, IFireProtector
{
	private static final AttributeModifier SPEED_BOOST = new AttributeModifier("Walk on lava speed boost", 0.15, 0).setSaved(false);

	public VolcaniteAmulet()
	{
		this.setTranslationKey("volcanite_amulet");
		this.setMaxStackSize(1);
		this.setContainerItem(this);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing sideHit, float f1, float f2, float f3)
	{
		if (!world.isRemote
				&& PlayerHelper.hasEditPermission(((EntityPlayerMP) player), pos)
				&& consumeFuel(player, player.getHeldItem(hand), 32, true))
		{
			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit))
			{
				FluidHelper.tryFillTank(tile, FluidRegistry.LAVA, sideHit, Fluid.BUCKET_VOLUME);
			} else
			{
				placeLava(player, pos.offset(sideHit), hand);
				world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
		}

		return EnumActionResult.SUCCESS;
	}

	private void placeLava(EntityPlayer player, BlockPos pos, EnumHand hand)
	{
		PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos, Blocks.FLOWING_LAVA.getDefaultState(), hand);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
	{
		if (player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
		{
			player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
		}
		return true;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
	{
		if (invSlot > 8 || !(entity instanceof EntityLivingBase))
		{
			return;
		}

		EntityLivingBase living = (EntityLivingBase) entity;

		int x = (int) Math.floor(living.posX);
		int y = (int) (living.posY - living.getYOffset());
		int z = (int) Math.floor(living.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		if ((world.getBlockState(pos.down()).getBlock() == Blocks.LAVA || world.getBlockState(pos.down()).getBlock() == Blocks.FLOWING_LAVA) && world.isAirBlock(pos))
		{
			if (!living.isSneaking())
			{
				living.motionY = 0.0D;
				living.fallDistance = 0.0F;
				living.onGround = true;
			}

			if (!world.isRemote && !living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
			{
				living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(SPEED_BOOST);
			}
		}
		else if (!world.isRemote)
		{
			if (living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
			{
				living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
			}
		}
	}
	
	@Override
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
		EntityLavaProjectile ent = new EntityLavaProjectile(player.getEntityWorld(), player);
		ent.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		player.getEntityWorld().spawnEntity(ent);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flags)
	{
		list.add(I18n.format("pe.volcanite.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		list.add(I18n.format("pe.volcanite.tooltip2"));
		list.add(I18n.format("pe.volcanite.tooltip3"));
		list.add(I18n.format("pe.volcanite.tooltip4"));
	}
	
	@Override
	@Optional.Method(modid = "baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase ent)
	{
		this.onUpdate(stack, ent.getEntityWorld(), ent, 0, false);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canEquip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	@Optional.Method(modid = "baubles")
	public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) 
	{
		return true;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.volcanitePedCooldown != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			if(!(te instanceof DMPedestalTile))
			{
				return;
			}

			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0)
			{
				world.getWorldInfo().setRainTime(0);
				world.getWorldInfo().setThunderTime(0);
				world.getWorldInfo().setRaining(false);
				world.getWorldInfo().setThundering(false);

				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.volcanitePedCooldown);
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
		List<String> list = new ArrayList<>();
		if (ProjectEConfig.pedestalCooldown.volcanitePedCooldown != -1)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.volcanite.pedestal1"));
			list.add(TextFormatting.BLUE + I18n.format("pe.volcanite.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.volcanitePedCooldown)));
		}
		return list;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}
}
