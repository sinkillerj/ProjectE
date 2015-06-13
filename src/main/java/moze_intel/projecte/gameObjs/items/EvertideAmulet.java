package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.FluidHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class EvertideAmulet extends ItemPE implements IProjectileShooter, IBauble, IPedestalItem, IFluidContainerItem
{
	private int startRainCooldown;

	public EvertideAmulet()
	{
		this.setUnlocalizedName("evertide_amulet");
		this.setMaxStackSize(1);
		this.setNoRepair();
		this.setContainerItem(this);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing sideHit, float f1, float f2, float f3)
	{
		if (!world.isRemote)
		{
			TileEntity tile = world.getTileEntity(pos);

			if (tile instanceof IFluidHandler)
			{
				IFluidHandler tank = (IFluidHandler) tile;

				if (FluidHelper.canFillTank(tank, FluidRegistry.WATER, sideHit))
				{
					FluidHelper.fillTank(tank, FluidRegistry.WATER, sideHit, 1000);
					return true;
				}
			}

			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == Blocks.cauldron)
			{
				int waterLevel = ((Integer) state.getValue(BlockCauldron.LEVEL));
				if (waterLevel < 3)
				{
					((BlockCauldron) state.getBlock()).setWaterLevel(world, pos, state, waterLevel + 1);
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, false);
			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				BlockPos blockPosHit = mop.getBlockPos();

				if (!(world.getTileEntity(blockPosHit) instanceof IFluidHandler))
				{
					if (world.isAirBlock(blockPosHit.offset(mop.sideHit)))
					{
						world.playSoundAtEntity(player, "projecte:item.pewatermagic", 1.0F, 1.0F);
						placeWater(world, blockPosHit.offset(mop.sideHit));
						PlayerHelper.swingItem(((EntityPlayerMP) player));
					}
				}
			}
		}

		return stack;
	}

	private void placeWater(World world, BlockPos pos)
	{
		Material material = world.getBlockState(pos).getBlock().getMaterial();

		if (world.provider.doesWaterVaporize())
		{
			world.playSoundEffect(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

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
			world.setBlockState(pos, Blocks.flowing_water.getDefaultState());
		}

	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
	{
		if (invSlot > 8 || !(entity instanceof EntityPlayer)) 
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;

		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		if ((world.getBlockState(pos.down()).getBlock() == Blocks.water || world.getBlockState(pos.down()).getBlock() == Blocks.flowing_water) && world.isAirBlock(pos))
		{
			if (!player.isSneaking())
			{
				player.motionY = 0.0D;
				player.fallDistance = 0.0F;
				player.onGround = true;
			}
				
			if (!world.isRemote && player.capabilities.getWalkSpeed() < 0.25F)
			{
				PlayerHelper.setPlayerWalkSpeed(player, 0.25F);
			}
		}
		else if (!world.isRemote)
		{
			if (player.isInWater())
			{
				player.setAir(300);
			}
				
			if (player.capabilities.getWalkSpeed() != Constants.PLAYER_WALK_SPEED)
			{
				PlayerHelper.setPlayerWalkSpeed(player, Constants.PLAYER_WALK_SPEED);
			}
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		World world = player.worldObj;

		if (!world.provider.doesWaterVaporize())
		{
			world.playSoundAtEntity(player, "projecte:item.pewatermagic", 1.0F, 1.0F);
			world.spawnEntityInWorld(new EntityWaterProjectile(world, player));
			return true;
		}

		return false;
	}

	/** Start IFluidContainerItem **/
	@Override
	public FluidStack getFluid(ItemStack container)
	{
		return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
	}

	@Override
	public int getCapacity(ItemStack container)
	{
		return FluidContainerRegistry.BUCKET_VOLUME;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill)
	{
		return 0;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain)
	{
		return new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
	}

	/** End IFluidContainerItem **/

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		list.add(String.format(StatCollector.translateToLocal("pe.evertide.tooltip1"), ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));

		list.add(StatCollector.translateToLocal("pe.evertide.tooltip2"));
		list.add(StatCollector.translateToLocal("pe.evertide.tooltip3"));
		list.add(StatCollector.translateToLocal("pe.evertide.tooltip4"));
	}
	
	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase player) 
	{
		this.onUpdate(stack, player.worldObj, player, 0, false);
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

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
	public void updateInPedestal(World world, BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.evertidePedCooldown != -1)
		{
			if (startRainCooldown == 0)
			{
				int i = (300 + world.rand.nextInt(600)) * 20;
				world.getWorldInfo().setRainTime(i);
				world.getWorldInfo().setThunderTime(i);
				world.getWorldInfo().setRaining(true);

				startRainCooldown = ProjectEConfig.evertidePedCooldown;
			}
			else
			{
				startRainCooldown--;
			}
		}
	}

	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.evertidePedCooldown != -1)
		{
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.evertide.pedestal1"));
			list.add(EnumChatFormatting.BLUE + String.format(
					StatCollector.translateToLocal("pe.evertide.pedestal2"), MathUtils.tickToSecFormatted(ProjectEConfig.evertidePedCooldown)));
		}
		return list;
	}
}
