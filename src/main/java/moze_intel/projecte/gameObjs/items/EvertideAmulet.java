package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.FluidHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class EvertideAmulet extends ItemPE implements IProjectileShooter, IBauble, IPedestalItem
{
	private static final AttributeModifier SPEED_BOOST = new AttributeModifier("Walk on water speed boost", 0.15, 0).setSaved(false);

	public EvertideAmulet()
	{
		this.setTranslationKey("evertide_amulet");
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.setContainerItem(this);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing sideHit, float f1, float f2, float f3)
	{
		if (!world.isRemote && PlayerHelper.hasEditPermission(((EntityPlayerMP) player), pos))
		{
			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, sideHit))
			{
				FluidHelper.tryFillTank(tile, FluidRegistry.WATER, sideHit, Fluid.BUCKET_VOLUME);
			} else
			{
				IBlockState state = world.getBlockState(pos);
				if (state.getBlock() == Blocks.CAULDRON)
				{
					int waterLevel = state.getValue(BlockCauldron.LEVEL);
					if (waterLevel < 3)
					{
						((BlockCauldron) state.getBlock()).setWaterLevel(world, pos, state, waterLevel + 1);
					}
				}
				else
				{
					world.playSound(null, player.posX, player.posY, player.posZ, PESounds.WATER, SoundCategory.PLAYERS, 1.0F, 1.0F);
					placeWater(world, player, pos.offset(sideHit), hand);
				}
			}
		}

		return EnumActionResult.SUCCESS;
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound oldCapNbt)
	{
		return new ICapabilityProvider() {
			private final IFluidHandlerItem handler = new InfiniteFluidHandler(stack);

			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
			}

			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
				{
					return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(handler);
				} else
				{
					return null;
				}
			}
		};
	}

	private void placeWater(World world, EntityPlayer player, BlockPos pos, EnumHand hand)
	{
		Material material = world.getBlockState(pos).getMaterial();

		if (world.provider.doesWaterVaporize())
		{
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

			for (int l = 0; l < 8; ++l)
			{
				world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
			}
		}
		else
		{
			if (!world.isRemote && !material.isSolid() && !material.isLiquid())
			{
				world.destroyBlock(pos, true);
			}
			world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
			PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos, Blocks.FLOWING_WATER.getDefaultState(), hand);
		}

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

		if ((world.getBlockState(pos.down()).getBlock() == Blocks.WATER || world.getBlockState(pos.down()).getBlock() == Blocks.FLOWING_WATER) && world.isAirBlock(pos))
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
			if (living.isInWater())
			{
				living.setAir(300);
			}

			if (living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
			{
				living.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
			}
		}
	}
	
	@Override
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		World world = player.getEntityWorld();

		if (!world.provider.doesWaterVaporize())
		{
			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.WATER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			EntityWaterProjectile ent = new EntityWaterProjectile(world, player);
			ent.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
			world.spawnEntity(ent);
			return true;
		}

		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flags)
	{
		list.add(I18n.format("pe.evertide.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));

		list.add(I18n.format("pe.evertide.tooltip2"));
		list.add(I18n.format("pe.evertide.tooltip3"));
		list.add(I18n.format("pe.evertide.tooltip4"));
	}
	
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
		this.onUpdate(stack, player.getEntityWorld(), player, 0, false);
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
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.evertidePedCooldown != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile))
			{
				return;
			}

			DMPedestalTile tile = ((DMPedestalTile) te);

			if (tile.getActivityCooldown() == 0)
			{
				int i = (300 + world.rand.nextInt(600)) * 20;
				world.getWorldInfo().setRainTime(i);
				world.getWorldInfo().setThunderTime(i);
				world.getWorldInfo().setRaining(true);

				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.evertidePedCooldown);
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
		if (ProjectEConfig.pedestalCooldown.evertidePedCooldown != -1)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.evertide.pedestal1"));
			list.add(TextFormatting.BLUE +
					I18n.format("pe.evertide.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.evertidePedCooldown)));
		}
		return list;
	}

	private static class InfiniteFluidHandler implements IFluidHandlerItem
	{
		private final ItemStack container;

		InfiniteFluidHandler(ItemStack stack) {
			container = stack;
		}

		private final FluidTankProperties props =
				new FluidTankProperties(new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME), Fluid.BUCKET_VOLUME);

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[] { props };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) { return 0; }

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (resource.getFluid() == FluidRegistry.WATER)
			{
				return resource.copy();
			} else
			{
				return null;
			}
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return new FluidStack(FluidRegistry.WATER, Math.min(maxDrain, Fluid.BUCKET_VOLUME));
		}

		@Nonnull
		@Override
		public ItemStack getContainer() {
			return container;
		}
	}

}
