package moze_intel.projecte.gameObjs.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.IFireProtectionItem;
import moze_intel.projecte.api.IPedestalItem;
import moze_intel.projecte.api.IProjectileShooter;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.FluidHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class VolcaniteAmulet extends ItemPE implements IProjectileShooter, IBauble, IPedestalItem, IFireProtectionItem
{
	public VolcaniteAmulet()
	{
		this.setUnlocalizedName("volcanite_amulet");
		this.setMaxStackSize(1);
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

				if (FluidHelper.canFillTank(tank, FluidRegistry.LAVA, sideHit))
				{
					if (consumeFuel(player, stack, 32.0F, true))
					{
						FluidHelper.fillTank(tank, FluidRegistry.LAVA, sideHit, 1000);
						return true;
					}
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
					if (world.isAirBlock(blockPosHit.offset(mop.sideHit)) && consumeFuel(player, stack, 32, true))
					{
						placeLava(world, blockPosHit.offset(mop.sideHit));
						world.playSoundAtEntity(player, "projecte:item.petransmute", 1.0F, 1.0F);
						PlayerHelper.swingItem(player);
					}
				}
			}
		}

		return stack;
	}


	private void placeLava(World world, BlockPos pos)
	{
		Material material = world.getBlockState(pos).getBlock().getMaterial();
		if (!world.isRemote && !material.isSolid() && !material.isLiquid())
		{
			world.destroyBlock(pos, true);
		}
		world.setBlockState(pos, Blocks.flowing_lava.getDefaultState(), 3);
	}

	private void handleWalkOnLava(EntityPlayer player)
	{
		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		if ((player.worldObj.getBlockState(pos.down()).getBlock() == Blocks.lava || player.worldObj.getBlockState(pos.down()).getBlock() == Blocks.flowing_lava) && player.worldObj.isAirBlock(pos))
		{
			if (!player.isSneaking())
			{
				player.motionY = 0.0D;
				player.fallDistance = 0.0F;
				player.onGround = true;
			}

			if (!player.worldObj.isRemote && player.capabilities.getWalkSpeed() < 0.25F)
			{
				PlayerHelper.setPlayerWalkSpeed(player, 0.25F);
			}
		}
		else if (!player.worldObj.isRemote)
		{
			if (player.capabilities.getWalkSpeed() != Constants.PLAYER_WALK_SPEED)
			{
				PlayerHelper.setPlayerWalkSpeed(player, Constants.PLAYER_WALK_SPEED);
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
	{
		if (invSlot > 8 || !(entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer) entity;

		handleWalkOnLava(player);

		if (!world.isRemote)
		{
			if (!player.isImmuneToFire())
			{
				PlayerHelper.setPlayerFireImmunity(player, true);
			}

			PlayerChecks.addPlayerFireChecks((EntityPlayerMP) player);
		}
	}
	
	@Override
	public boolean shootProjectile(EntityPlayer player, ItemStack stack) 
	{
		player.worldObj.playSoundAtEntity(player, "projecte:item.petransmute", 1.0F, 1.0F);
		player.worldObj.spawnEntityInWorld(new EntityLavaProjectile(player.worldObj, player));
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		list.add(String.format(StatCollector.translateToLocal("pe.volcanite.tooltip1"), ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		list.add(StatCollector.translateToLocal("pe.volcanite.tooltip2"));
		list.add(StatCollector.translateToLocal("pe.volcanite.tooltip3"));
		list.add(StatCollector.translateToLocal("pe.volcanite.tooltip4"));
	}
	
	@Override
	@Optional.Method(modid = "Baubles")
	public baubles.api.BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.AMULET;
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onWornTick(ItemStack stack, EntityLivingBase ent) 
	{
		if (!(ent instanceof EntityPlayer)) 
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) ent;

		handleWalkOnLava(player);
		
		if (!player.worldObj.isRemote && !player.isImmuneToFire())
		{
			PlayerHelper.setPlayerFireImmunity(player, true);
		}
	}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onEquipped(ItemStack itemstack, EntityLivingBase player) {}

	@Override
	@Optional.Method(modid = "Baubles")
	public void onUnequipped(ItemStack itemstack, EntityLivingBase player) 
	{
		if (player instanceof EntityPlayer && !player.worldObj.isRemote)
		{
			PlayerHelper.setPlayerFireImmunity((EntityPlayer) player, false);
		}
	}

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
		if (!world.isRemote && ProjectEConfig.volcanitePedCooldown != -1)
		{
			DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
			if (tile.getActivityCooldown() == 0)
			{
				world.getWorldInfo().setRainTime(0);
				world.getWorldInfo().setThunderTime(0);
				world.getWorldInfo().setRaining(false);
				world.getWorldInfo().setThundering(false);

				tile.setActivityCooldown(ProjectEConfig.volcanitePedCooldown);
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
		if (ProjectEConfig.volcanitePedCooldown != -1)
		{
			list.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("pe.volcanite.pedestal1"));
			list.add(EnumChatFormatting.BLUE + String.format(StatCollector.translateToLocal("pe.volcanite.pedestal2"), MathUtils.tickToSecFormatted(ProjectEConfig.volcanitePedCooldown)));
		}
		return list;
	}
}
