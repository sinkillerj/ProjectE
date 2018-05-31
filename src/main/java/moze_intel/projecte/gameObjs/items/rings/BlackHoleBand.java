package moze_intel.projecte.gameObjs.items.rings;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.google.common.collect.Lists;
import moze_intel.projecte.api.item.IAlchBagItem;
import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class BlackHoleBand extends RingToggle implements IAlchBagItem, IAlchChestItem, IBauble, IPedestalItem
{
	public BlackHoleBand()
	{
		super("black_hole");
		this.setNoRepair();
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		BlockPos fluidPos = pos.offset(facing);
		IBlockState state = world.getBlockState(fluidPos);
		if (state.getBlock() instanceof BlockFluidBase
				|| state.getBlock() instanceof BlockLiquid)
		{
			if (!world.isRemote)
			{
				world.setBlockToAir(fluidPos);
				Fluid f = FluidRegistry.lookupFluidForBlock(state.getBlock());
				if (f != null)
				{
					world.playSound(null, pos, f.getFillSound(world, fluidPos), SoundCategory.BLOCKS, 1, 1);
				}
			}

			return EnumActionResult.SUCCESS;
		} else
		{
			return EnumActionResult.PASS;
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
	{
		if (!world.isRemote)
		{
			changeMode(player, player.getHeldItem(hand), hand);
		}
		
		return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{
		if (!ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE) || !(entity instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		AxisAlignedBB bBox = player.getEntityBoundingBox().grow(7);
		List<EntityItem> itemList = world.getEntitiesWithinAABB(EntityItem.class, bBox);
		
		for (EntityItem item : itemList)
		{
			if (ItemHelper.hasSpace(player.inventory.mainInventory, item.getItem()))
			{
				WorldHelper.gravitateEntityTowards(item, player.posX, player.posY, player.posZ);
			}
		}
	}

	@Override
	@Optional.Method(modid = "baubles")
	public BaubleType getBaubleType(ItemStack itemstack)
	{
		return BaubleType.RING;
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
		DMPedestalTile tile = ((DMPedestalTile) world.getTileEntity(pos));
		if (tile != null)
		{
			List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, tile.getEffectBounds());
			for (EntityItem item : list)
			{
				WorldHelper.gravitateEntityTowards(item, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
				if (!world.isRemote && item.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 1.21 && !item.isDead)
				{
					suckDumpItem(item, tile);
				}
			}
		}
	}

	private void suckDumpItem(EntityItem item, DMPedestalTile tile)
	{
		Map<EnumFacing, TileEntity> map = WorldHelper.getAdjacentTileEntitiesMapped(tile.getWorld(), tile);
		for (Map.Entry<EnumFacing, TileEntity> e : map.entrySet())
		{
			IItemHandler inv = null;

			if (e.getValue().hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, e.getKey()))
			{
				inv = e.getValue().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, e.getKey());
			} else if (e.getValue() instanceof IInventory)
			{
				inv = new InvWrapper((IInventory) e.getValue());
			}

			ItemStack result = ItemHandlerHelper.insertItemStacked(inv, item.getItem(), false);

			if (result.isEmpty())
			{
				item.setDead();
				return;
			}
			else
			{
				item.setItem(result);
			}
		}
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public List<String> getPedestalDescription()
	{
		return Lists.newArrayList(
				TextFormatting.BLUE + I18n.format("pe.bhb.pedestal1"),
				TextFormatting.BLUE + I18n.format("pe.bhb.pedestal2")
		);
	}

	@Override
	public void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack)
	{
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof AlchChestTile))
		{
			return;
		}
		AlchChestTile tile = (AlchChestTile) te;
		if (ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE))
		{
			AxisAlignedBB aabb = new AxisAlignedBB(tile.getPos().getX() - 5, tile.getPos().getY() - 5, tile.getPos().getZ() - 5,
					tile.getPos().getX() + 5, tile.getPos().getY() + 5, tile.getPos().getZ() + 5);
			double centeredX = tile.getPos().getX() + 0.5;
			double centeredY = tile.getPos().getY() + 0.5;
			double centeredZ = tile.getPos().getZ() + 0.5;

			for (EntityItem e : tile.getWorld().getEntitiesWithinAABB(EntityItem.class, aabb))
			{
				WorldHelper.gravitateEntityTowards(e, centeredX, centeredY, centeredZ);
				if (!e.getEntityWorld().isRemote && !e.isDead && e.getDistanceSq(centeredX, centeredY, centeredZ) < 1.21)
				{
					ItemStack result = ItemHandlerHelper.insertItemStacked(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), e.getItem(), false);
					if (!result.isEmpty())
					{
						e.setItem(result);
					}
					else
					{
						e.setDead();
					}
				}
			}
		}
	}

	@Override
	public boolean updateInAlchBag(@Nonnull IItemHandler inv, @Nonnull EntityPlayer player, @Nonnull ItemStack stack)
	{
		if (ItemHelper.getOrCreateCompound(stack).getBoolean(TAG_ACTIVE))
		{
			for (EntityItem e : player.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, player.getEntityBoundingBox().grow(5)))
			{
				WorldHelper.gravitateEntityTowards(e, player.posX, player.posY, player.posZ);
			}
		}
		return false;
	}
}
