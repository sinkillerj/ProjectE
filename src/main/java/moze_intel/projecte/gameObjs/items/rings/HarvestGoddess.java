package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
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
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

public class HarvestGoddess extends PEToggleItem implements IPedestalItem {

	public HarvestGoddess(Properties props) {
		super(props);
		addItemCapability(PedestalItemCapabilityWrapper::new);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, Level level, @NotNull Entity entity, int slot, boolean held) {
		if (level.isClientSide || slot >= Inventory.getSelectionSize() || !(entity instanceof Player player)) {
			return;
		}
		super.inventoryTick(stack, level, entity, slot, held);
		CompoundTag nbt = stack.getOrCreateTag();
		if (nbt.getBoolean(Constants.NBT_KEY_ACTIVE)) {
			long storedEmc = getEmc(stack);
			if (storedEmc == 0 && !consumeFuel(player, stack, 64, true)) {
				nbt.putBoolean(Constants.NBT_KEY_ACTIVE, false);
			} else {
				WorldHelper.growNearbyRandomly(true, level, player.blockPosition(), player);
				removeEmc(stack, EMCHelper.removeFractionalEMC(stack, 0.32F));
			}
		} else {
			WorldHelper.growNearbyRandomly(false, level, player.blockPosition(), player);
		}
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		Player player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		Direction side = ctx.getClickedFace();
		if (level.isClientSide || player == null || !player.mayUseItemAt(pos, side, ctx.getItemInHand())) {
			return InteractionResult.FAIL;
		}
		if (player.isShiftKeyDown()) {
			for (int i = 0; i < player.getInventory().items.size(); i++) {
				ItemStack stack = player.getInventory().items.get(i);
				if (!stack.isEmpty() && stack.getCount() >= 4 && stack.getItem() == Items.BONE_MEAL) {
					if (useBoneMeal(level, pos, side)) {
						player.getInventory().removeItem(i, 4);
						player.inventoryMenu.broadcastChanges();
						return InteractionResult.SUCCESS;
					}
					break;
				}
			}
		} else if (plantSeeds(level, player, pos)) {
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	private boolean useBoneMeal(Level level, BlockPos pos, Direction side) {
		if (level instanceof ServerLevel serverLevel) {
			boolean result = false;
			for (BlockPos currentPos : BlockPos.betweenClosed(pos.offset(-15, 0, -15), pos.offset(15, 0, 15))) {
				currentPos = currentPos.immutable();
				BlockState state = serverLevel.getBlockState(currentPos);
				if (state.getBlock() instanceof BonemealableBlock growable && growable.isValidBonemealTarget(serverLevel, currentPos, state, false) &&
					growable.isBonemealSuccess(serverLevel, serverLevel.random, currentPos, state)) {
					growable.performBonemeal(serverLevel, serverLevel.random, currentPos, state);
					level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
					result = true;
				} else if (WorldHelper.growWaterPlant(serverLevel, currentPos, state, side)) {
					level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
					result = true;
				}
			}
			return result;
		}
		return false;
	}

	private boolean plantSeeds(Level level, Player player, BlockPos pos) {
		List<StackWithSlot> seeds = getAllSeeds(player.getInventory().items);
		if (seeds.isEmpty()) {
			return false;
		}
		boolean result = false;
		for (BlockPos currentPos : BlockPos.betweenClosed(pos.offset(-8, 0, -8), pos.offset(8, 0, 8))) {
			if (level.isEmptyBlock(currentPos)) {
				continue;
			}
			BlockState state = level.getBlockState(currentPos);
			//Ensure we are immutable so that changing blocks doesn't act weird
			currentPos = currentPos.immutable();
			for (int i = 0; i < seeds.size(); i++) {
				StackWithSlot s = seeds.get(i);
				if (state.canSustainPlant(level, currentPos, Direction.UP, s.plantable) && level.isEmptyBlock(currentPos.above())) {
					level.setBlockAndUpdate(currentPos.above(), s.plantable.getPlant(level, currentPos.above()));
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
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide && ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				WorldHelper.growNearbyRandomly(true, level, pos, null);
				pedestal.setActivityCooldown(ProjectEConfig.server.cooldown.pedestal.harvest.get());
			} else {
				pedestal.decrementActivityCooldown();
			}
		}
		return false;
	}

	@NotNull
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