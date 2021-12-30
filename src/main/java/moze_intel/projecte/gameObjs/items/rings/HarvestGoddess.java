package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.tile.IDMPedestal;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;

public class HarvestGoddess extends PEToggleItem implements IPedestalItem {

	public HarvestGoddess(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
	}

	@Override
	public void inventoryTick(@Nonnull ItemStack stack, Level world, @Nonnull Entity entity, int slot, boolean held) {
		if (world.isClientSide || slot >= Inventory.getSelectionSize() || !(entity instanceof Player player)) {
			return;
		}
		super.inventoryTick(stack, world, entity, slot, held);
		CompoundTag nbt = stack.getOrCreateTag();
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
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		Player player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		if (world.isClientSide || player == null || !player.mayUseItemAt(pos, ctx.getClickedFace(), ctx.getItemInHand())) {
			return InteractionResult.FAIL;
		}
		if (player.isShiftKeyDown()) {
			for (int i = 0; i < player.getInventory().items.size(); i++) {
				ItemStack stack = player.getInventory().items.get(i);
				if (!stack.isEmpty() && stack.getCount() >= 4 && stack.getItem() == Items.BONE_MEAL) {
					if (useBoneMeal(world, pos)) {
						player.getInventory().removeItem(i, 4);
						player.inventoryMenu.broadcastChanges();
						return InteractionResult.SUCCESS;
					}
					break;
				}
			}
		} else if (plantSeeds(world, player, pos)) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	private boolean useBoneMeal(Level world, BlockPos pos) {
		if (world instanceof ServerLevel level) {
			boolean result = false;
			for (BlockPos currentPos : BlockPos.betweenClosed(pos.offset(-15, 0, -15), pos.offset(15, 0, 15))) {
				BlockState state = level.getBlockState(currentPos);
				Block crop = state.getBlock();
				if (crop instanceof BonemealableBlock growable && growable.isValidBonemealTarget(level, currentPos, state, false) &&
					growable.isBonemealSuccess(level, level.random, currentPos, state)) {
					growable.performBonemeal(level, level.random, currentPos.immutable(), state);
					if (!result) {
						result = true;
					}
				}
			}
			return result;
		}
		return false;
	}

	private boolean plantSeeds(Level world, Player player, BlockPos pos) {
		List<StackWithSlot> seeds = getAllSeeds(player.getInventory().items);
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
					player.getInventory().removeItem(s.slot, 1);
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
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@Nonnull ItemStack stack, @Nonnull Level world, @Nonnull BlockPos pos,
			@Nonnull PEDESTAL pedestal) {
		if (!world.isClientSide && ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				WorldHelper.growNearbyRandomly(true, world, pos, null);
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.harvest.get());
			} else {
				pedestal.decrementActivityCooldown();
			}
		}
		return false;
	}

	@Nonnull
	@Override
	public List<Component> getPedestalDescription() {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_2.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_3.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.harvest.get())));
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