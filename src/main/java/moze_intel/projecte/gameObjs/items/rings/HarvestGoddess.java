package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;

public class HarvestGoddess extends PEToggleItem implements IPedestalItem {

	public HarvestGoddess(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int slot, boolean held) {
		if (world.isClientSide || slot >= PlayerInventory.getSelectionSize() || !(entity instanceof PlayerEntity)) {
			return;
		}
		super.inventoryTick(stack, world, entity, slot, held);
		PlayerEntity player = (PlayerEntity) entity;
		CompoundNBT nbt = stack.getOrCreateTag();
		if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
			long storedEmc = getEmc(stack);
			if (storedEmc == 0 && !consumeFuel(player, stack, 64, true)) {
				nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
			} else {
				WorldHelper.growNearbyRandomly(true, world, player.blockPosition(), player);
				removeEmc(stack, EMCHelper.removeFractionalEMC(stack, 0.32F));
			}
		} else {
			WorldHelper.growNearbyRandomly(false, world, player.blockPosition(), player);
		}
	}

	@Nonnull
	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World world = ctx.getLevel();
		PlayerEntity player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		if (world.isClientSide || player == null || !player.mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())) {
			return ActionResultType.FAIL;
		}
		if (player.isShiftKeyDown()) {
			for (int i = 0; i < player.inventory.items.size(); i++) {
				ItemStack stack = player.inventory.items.get(i);
				if (!stack.isEmpty() && stack.getCount() >= 4 && stack.getItem() == Items.BONE_MEAL) {
					if (useBoneMeal(world, pos)) {
						player.inventory.removeItem(i, 4);
						player.inventoryMenu.broadcastChanges();
						return ActionResultType.SUCCESS;
					}
					break;
				}
			}
		} else if (plantSeeds(world, player, pos)) {
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	private boolean useBoneMeal(World world, BlockPos pos) {
		boolean result = false;
		if (world instanceof ServerWorld) {
			for (BlockPos currentPos : BlockPos.betweenClosed(pos.offset(-15, 0, -15), pos.offset(15, 0, 15))) {
				BlockState state = world.getBlockState(currentPos);
				Block crop = state.getBlock();
				if (crop instanceof IGrowable) {
					IGrowable growable = (IGrowable) crop;
					if (growable.isValidBonemealTarget(world, currentPos, state, false) && growable.isBonemealSuccess(world, world.random, currentPos, state)) {
						growable.performBonemeal((ServerWorld) world, world.random, currentPos.immutable(), state);
						if (!result) {
							result = true;
						}
					}
				}
			}
		}
		return result;
	}

	private boolean plantSeeds(World world, PlayerEntity player, BlockPos pos) {
		List<StackWithSlot> seeds = getAllSeeds(player.inventory.items);
		if (seeds.isEmpty()) {
			return false;
		}
		boolean result = false;
		for (BlockPos currentPos : BlockPos.betweenClosed(pos.offset(-8, 0, -8), pos.offset(8, 0, 8))) {
			if (world.isEmptyBlock(currentPos)) {
				continue;
			}
			BlockState state = world.getBlockState(currentPos);
			//Ensure we are immutable so that changing blocks doesn't act weird
			currentPos = currentPos.immutable();
			for (int i = 0; i < seeds.size(); i++) {
				StackWithSlot s = seeds.get(i);
				if (state.canSustainPlant(world, currentPos, Direction.UP, s.plantable) && world.isEmptyBlock(currentPos.above())) {
					world.setBlockAndUpdate(currentPos.above(), s.plantable.getPlant(world, currentPos.above()));
					player.inventory.removeItem(s.slot, 1);
					player.inventoryMenu.broadcastChanges();
					s.count--;
					if (s.count == 0) {
						seeds.remove(i);
						if (seeds.isEmpty()) {
							//If we are out of seeds, hard exit the method
							return true;
						}
					}
					if (!result) {
						result = true;
					}
					//Once we set a seed in that position, break out of trying to place other seeds in that position
					break;
				}
			}
		}
		return result;
	}

	private List<StackWithSlot> getAllSeeds(NonNullList<ItemStack> inv) {
		List<StackWithSlot> result = new ArrayList<>();
		for (int i = 0; i < inv.size(); i++) {
			ItemStack stack = inv.get(i);
			if (!stack.isEmpty()) {
				Item item = stack.getItem();
				if (item instanceof IPlantable) {
					result.add(new StackWithSlot(stack, i, (IPlantable) item));
				} else {
					Block block = Block.byItem(item);
					if (block instanceof IPlantable) {
						result.add(new StackWithSlot(stack, i, (IPlantable) block));
					}
				}
			}
		}
		return result;
	}

	@Override
	public void updateInPedestal(@Nonnull World world, @Nonnull BlockPos pos) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile != null) {
				if (tile.getActivityCooldown() == 0) {
					WorldHelper.growNearbyRandomly(true, world, pos, null);
					tile.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.harvest.get());
				} else {
					tile.decrementActivityCooldown();
				}
			}
		}
	}

	@Nonnull
	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_1.translateColored(TextFormatting.BLUE));
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_2.translateColored(TextFormatting.BLUE));
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_3.translateColored(TextFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.harvest.get())));
		}
		return list;
	}

	private static class StackWithSlot {

		public final IPlantable plantable;
		public final int slot;
		public int count;

		public StackWithSlot(ItemStack stack, int slot, IPlantable plantable) {
			this.slot = slot;
			this.count = stack.getCount();
			this.plantable = plantable;
		}
	}
}