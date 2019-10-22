package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class HarvestGoddess extends PEToggleItem implements IPedestalItem
{
	public HarvestGoddess(Properties props)
	{
		super(props);
	}
	
	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held)
	{
		if (world.isRemote || slot > 8 || !(entity instanceof PlayerEntity))
		{
			return;
		}
		
		super.inventoryTick(stack, world, entity, slot, held);
		
		PlayerEntity player = (PlayerEntity) entity;

        if (stack.getOrCreateTag().getBoolean(TAG_ACTIVE))
		{
			long storedEmc = getEmc(stack);
			
			if (storedEmc == 0 && !consumeFuel(player, stack, 64, true))
			{
				stack.getTag().putBoolean(TAG_ACTIVE, false);
			}
			else
			{
				WorldHelper.growNearbyRandomly(true, world, new BlockPos(player), player);
				removeEmc(stack, EMCHelper.removeFractionalEMC(stack, 0.32F));
			}
		}
		else
		{
			WorldHelper.growNearbyRandomly(false, world, new BlockPos(player), player);
		}
	}

	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		World world = ctx.getWorld();
		PlayerEntity player = ctx.getPlayer();

		if (world.isRemote || !player.canPlayerEdit(ctx.getPos(), ctx.getFace(), ctx.getItem()))
		{
			return ActionResultType.FAIL;
		}
		
		if (player.isSneaking())
		{
			Object[] obj = getStackFromInventory(player.inventory.mainInventory, Items.BONE_MEAL, 4);

			if (obj == null) 
			{
				return ActionResultType.FAIL;
			}
			
			ItemStack boneMeal = (ItemStack) obj[1];

			if (!boneMeal.isEmpty() && useBoneMeal(world, ctx.getPos()))
			{
				player.inventory.decrStackSize((Integer) obj[0], 4);
				player.container.detectAndSendChanges();
				return ActionResultType.SUCCESS;
			}
			
			return ActionResultType.FAIL;
		}
		
		return plantSeeds(world, player, ctx.getPos()) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}
	
	private boolean useBoneMeal(World world, BlockPos pos)
	{
		boolean result = false;

		for (BlockPos currentPos : BlockPos.getAllInBoxMutable(pos.add(-15, 0, -15), pos.add(15, 0, 15)))
		{
			BlockState state = world.getBlockState(currentPos);
			Block crop = state.getBlock();

			if (crop instanceof IGrowable)
			{
				IGrowable growable = (IGrowable) crop;

				if (growable.canUseBonemeal(world, world.rand, currentPos, state))
				{
					if (!result)
					{
						result = true;
					}

					growable.grow(world, world.rand, currentPos.toImmutable(), state);
				}
			}
		}
		return result;
	}
	
	private boolean plantSeeds(World world, PlayerEntity player, BlockPos pos)
	{
		boolean result = false;
		
		List<StackWithSlot> seeds = getAllSeeds(player.inventory.mainInventory);
		
		if (seeds.isEmpty())
		{
			return false;
		}

		for (BlockPos currentPos : BlockPos.getAllInBoxMutable(pos.add(-8, 0, -8), pos.add(8, 0, 8)))
		{
			BlockState state = world.getBlockState(currentPos);

			if (world.isAirBlock(currentPos))
			{
				continue;
			}

			for (int i = 0; i < seeds.size(); i++)
			{
				StackWithSlot s = seeds.get(i);
				IPlantable plant;

				if (s.stack.getItem() instanceof IPlantable)
				{
					plant = (IPlantable) s.stack.getItem();
				}
				else
				{
					plant = (IPlantable) Block.getBlockFromItem(s.stack.getItem());
				}

				if (state.getBlock().canSustainPlant(state, world, currentPos, Direction.UP, plant) && world.isAirBlock(currentPos.up()))
				{
					world.setBlockState(currentPos.up(), plant.getPlant(world, currentPos.up()));
					player.inventory.decrStackSize(s.slot, 1);
					player.container.detectAndSendChanges();

					s.stack.shrink(1);

					if (s.stack.isEmpty())
					{
						seeds.remove(i);
					}

					if (!result)
					{
						result = true;
					}
				}
			}
		}
		return result;
	}
	
	private List<StackWithSlot> getAllSeeds(NonNullList<ItemStack> inv)
	{
		List<StackWithSlot> result = new ArrayList<>();
		
		for (int i = 0; i < inv.size(); i++)
		{
			ItemStack stack = inv.get(i);
			
			if (!stack.isEmpty())
			{
				if (stack.getItem() instanceof IPlantable)
				{
					result.add(new StackWithSlot(stack, i));
					continue;
				}
				
				Block block = Block.getBlockFromItem(stack.getItem());
				
				if (block instanceof IPlantable)
				{
					result.add(new StackWithSlot(stack, i));
				}
			}
		}
		
		return result;
	}
	
	private Object[] getStackFromInventory(NonNullList<ItemStack> inv, Item item, int minAmount)
	{
		Object[] obj = new Object[2];
		
		for (int i = 0; i < inv.size(); i++)
		{
			ItemStack stack = inv.get(i);
			
			if (!stack.isEmpty() && stack.getCount() >= minAmount && stack.getItem() == item)
			{
				obj[0] = i;
				obj[1] = stack;
				return obj;
			}
		}
		
		return null;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos)
	{
		if (!world.isRemote && ProjectEConfig.pedestalCooldown.harvest.get() != -1)
		{
			TileEntity te = world.getTileEntity(pos);
			if(!(te instanceof DMPedestalTile))
			{
				return;
			}

			DMPedestalTile tile = (DMPedestalTile) te;
			if (tile.getActivityCooldown() == 0)
			{
				WorldHelper.growNearbyRandomly(true, world, pos, null);
				tile.setActivityCooldown(ProjectEConfig.pedestalCooldown.harvest.get());
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
		if (ProjectEConfig.pedestalCooldown.harvest.get() != -1)
		{
			list.add(new TranslationTextComponent("pe.harvestgod.pedestal1").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.harvestgod.pedestal2").applyTextStyle(TextFormatting.BLUE));
			list.add(new TranslationTextComponent("pe.harvestgod.pedestal3", MathUtils.tickToSecFormatted(ProjectEConfig.pedestalCooldown.harvest.get())).applyTextStyle(TextFormatting.BLUE));
		}
		return list;
	}

	private static class StackWithSlot
	{
		public final int slot;
		public final ItemStack stack;
		
		public StackWithSlot(ItemStack stack, int slot) 
		{
			this.stack = stack.copy();
			this.slot = slot;
		}
	}
}
