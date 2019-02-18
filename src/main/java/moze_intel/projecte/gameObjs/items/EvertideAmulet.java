package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.api.item.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ClientKeyHelper;
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
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

// todo 1.13 @Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class EvertideAmulet extends ItemPE implements IProjectileShooter, IPedestalItem
{
	private static final AttributeModifier SPEED_BOOST = new AttributeModifier("Walk on water speed boost", 0.15, 0).setSaved(false);

	public EvertideAmulet(Properties props)
	{
		super(props);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack)
	{
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		return stack.copy();
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		EntityPlayer player = ctx.getPlayer();

		if (!world.isRemote && PlayerHelper.hasEditPermission(((EntityPlayerMP) player), ctx.getPos()))
		{
			TileEntity tile = world.getTileEntity(ctx.getPos());

			if (tile != null && tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, ctx.getFace()).isPresent())
			{
				// todo 1.13 FluidHelper.tryFillTank(tile, FluidRegistry.WATER, ctx.getFace(), Fluid.BUCKET_VOLUME);
			} else
			{
				IBlockState state = world.getBlockState(ctx.getPos());
				if (state.getBlock() == Blocks.CAULDRON)
				{
					int waterLevel = state.get(BlockCauldron.LEVEL);
					if (waterLevel < 3)
					{
						((BlockCauldron) state.getBlock()).setWaterLevel(world, ctx.getPos(), state, waterLevel + 1);
					}
				}
				else
				{
					world.playSound(null, player.posX, player.posY, player.posZ, PESounds.WATER, SoundCategory.PLAYERS, 1.0F, 1.0F);
					placeWater(world, player, ctx.getPos().offset(ctx.getFace()));
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
			private final LazyOptional<IFluidHandlerItem> handler = LazyOptional.of(() -> new InfiniteFluidHandler(stack));

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
				{
					return handler.cast();
				} else
				{
					return LazyOptional.empty();
				}
			}
		};
	}

	private void placeWater(World world, EntityPlayer player, BlockPos pos)
	{
		Material material = world.getBlockState(pos).getMaterial();

		if (world.dimension.doesWaterVaporize())
		{
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

			for (int l = 0; l < 8; ++l)
			{
				world.addParticle(Particles.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
			}
		}
		else
		{
			if (!world.isRemote && !material.isSolid() && !material.isLiquid())
			{
				world.destroyBlock(pos, true);
			}
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
			PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos, Blocks.WATER.getDefaultState());
		}

	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player)
	{
		if (player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
		{
			player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
		}
		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
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

		if (world.getFluidState(pos.down()).getFluid().isIn(FluidTags.WATER) && world.isAirBlock(pos))
		{
			if (!living.isSneaking())
			{
				living.motionY = 0.0D;
				living.fallDistance = 0.0F;
				living.onGround = true;
			}

			if (!world.isRemote && !living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
			{
				living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(SPEED_BOOST);
			}
		}
		else if (!world.isRemote)
		{
			if (living.isInWater())
			{
				living.setAir(300);
			}

			if (living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).hasModifier(SPEED_BOOST))
			{
				living.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SPEED_BOOST);
			}
		}
	}
	
	@Override
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		World world = player.getEntityWorld();

		if (!world.dimension.doesWaterVaporize())
		{
			world.playSound(null, player.posX, player.posY, player.posZ, PESounds.WATER, SoundCategory.PLAYERS, 1.0F, 1.0F);
			EntityWaterProjectile ent = new EntityWaterProjectile(player, world);
			ent.shoot(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
			world.spawnEntity(ent);
			return true;
		}

		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flags)
	{
		list.add(new TextComponentTranslation("pe.evertide.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));

		list.add(new TextComponentTranslation("pe.evertide.tooltip2"));
		list.add(new TextComponentTranslation("pe.evertide.tooltip3"));
		list.add(new TextComponentTranslation("pe.evertide.tooltip4"));
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
	*/

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.evertide.get() != -1)
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

				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.evertide.get());
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
		if (ProjectEConfig.pedestalCooldown.evertide.get() != -1)
		{
			list.add(new TextComponentTranslation("pe.evertide.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TextComponentTranslation("pe.evertide.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.evertide.get())).applyTextStyle(TextFormatting.BLUE));
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
				new FluidTankProperties(null/*todo 1.13 new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME)*/, Fluid.BUCKET_VOLUME);

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[] { props };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) { return 0; }

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			/*if (resource.getFluid() == FluidRegistry.WATER)
			{
				return resource.copy();
			} else*/
			{
				return null;
			}
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			return null; //new FluidStack(FluidRegistry.WATER, Math.min(maxDrain, Fluid.BUCKET_VOLUME));
		}

		@Nonnull
		@Override
		public ItemStack getContainer() {
			return container;
		}
	}

}
