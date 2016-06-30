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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class VolcaniteAmulet extends ItemPE implements IProjectileShooter, IBauble, IPedestalItem, IFireProtector
{
	public VolcaniteAmulet()
	{
		this.setUnlocalizedName("volcanite_amulet");
		this.setMaxStackSize(1);
		this.setContainerItem(this);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing sideHit, float f1, float f2, float f3)
	{
		if (!world.isRemote && PlayerHelper.hasEditPermission(((EntityPlayerMP) player), pos))
		{
			TileEntity tile = world.getTileEntity(pos);

			if (consumeFuel(player, stack, 32.0F, true))
			{
				FluidHelper.tryFillTank(tile, FluidRegistry.LAVA, sideHit, Fluid.BUCKET_VOLUME);
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote)
		{
			RayTraceResult mop = this.rayTrace(world, player, false);
			if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				BlockPos blockPosHit = mop.getBlockPos();
				if (!(world.getTileEntity(blockPosHit) instanceof IFluidHandler))
				{
					if (world.isAirBlock(blockPosHit.offset(mop.sideHit)) && consumeFuel(player, stack, 32, true))
					{
						placeLava(world, player, blockPosHit.offset(mop.sideHit));
						world.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
						PlayerHelper.swingItem(player);
					}
				}
			}
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	private void placeLava(World world, EntityPlayer player, BlockPos pos)
	{
		PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) player), pos, Blocks.FLOWING_LAVA.getDefaultState());
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int invSlot, boolean par5)
	{
		if (invSlot > 8 || !(entity instanceof EntityPlayer)) return;
		
		EntityPlayer player = (EntityPlayer) entity;

		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		if ((player.worldObj.getBlockState(pos.down()).getBlock() == Blocks.LAVA || player.worldObj.getBlockState(pos.down()).getBlock() == Blocks.FLOWING_LAVA) && player.worldObj.isAirBlock(pos))
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
	public boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, EnumHand hand)
	{
		player.worldObj.playSound(null, player.posX, player.posY, player.posZ, PESounds.TRANSMUTE, SoundCategory.PLAYERS, 1, 1);
		EntityLavaProjectile ent = new EntityLavaProjectile(player.worldObj, player);
		ent.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0, 1.5F, 1);
		player.worldObj.spawnEntityInWorld(ent);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean par4)
	{
		list.add(I18n.format("pe.volcanite.tooltip1", ClientKeyHelper.getKeyName(PEKeybind.FIRE_PROJECTILE)));
		list.add(I18n.format("pe.volcanite.tooltip2"));
		list.add(I18n.format("pe.volcanite.tooltip3"));
		list.add(I18n.format("pe.volcanite.tooltip4"));
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

		int x = (int) Math.floor(player.posX);
		int y = (int) (player.posY - player.getYOffset());
		int z = (int) Math.floor(player.posZ);
		BlockPos pos = new BlockPos(x, y, z);

		if ((player.worldObj.getBlockState(pos.down()).getBlock() == Blocks.LAVA || player.worldObj.getBlockState(pos.down()).getBlock() == Blocks.FLOWING_LAVA) && player.worldObj.isAirBlock(pos))
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
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
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

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		List<String> list = Lists.newArrayList();
		if (ProjectEConfig.volcanitePedCooldown != -1)
		{
			list.add(TextFormatting.BLUE + I18n.format("pe.volcanite.pedestal1"));
			list.add(TextFormatting.BLUE + I18n.format("pe.volcanite.pedestal2", MathUtils.tickToSecFormatted(ProjectEConfig.volcanitePedCooldown)));
		}
		return list;
	}

	@Override
	public boolean canProtectAgainstFire(ItemStack stack, EntityPlayerMP player)
	{
		return true;
	}
}
