package moze_intel.projecte.gameObjs.items.rings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.SpecialPlantable;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HarvestGoddess extends PEToggleItem implements IPedestalItem {

	public HarvestGoddess(Properties props) {
		super(props.component(PEDataComponentTypes.STORED_EMC, 0L)
				.component(PEDataComponentTypes.UNPROCESSED_EMC, 0.0)
		);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity, @Nullable EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		if (level.isClientSide() || !hotBarOrOffHand(slot) || !(entity instanceof Player player)) {
			return;
		}
		if (stack.getOrDefault(PEDataComponentTypes.ACTIVE, false)) {
			if (!hasEmc(player, stack, 64, true)) {
				stack.set(PEDataComponentTypes.ACTIVE, false);
			} else {
				WorldHelper.growNearbyRandomly(true, level, player);
				removeEmc(stack, 0.32F);
			}
		} else {
			WorldHelper.growNearbyRandomly(false, level, player);
		}
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		Player player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		Direction side = ctx.getClickedFace();
		if (player == null || !player.mayUseItemAt(pos, side, ctx.getItemInHand())) {
			return InteractionResult.FAIL;
		}
		if (ctx.isSecondaryUseActive()) {
			for (ItemStack stack : ItemHelper.getInventoryStacks(player.getInventory())) {
				if (stack.is(Items.BONE_MEAL)) {
					InteractionResult result = useBoneMeal(level, pos, side, player, stack);
					if (result != InteractionResult.PASS) {
						return result;
					}
				}
			}
		} else if (plantSeeds(level, player, pos)) {
			return InteractionResult.CONSUME;
		}
		return InteractionResult.FAIL;
	}

	private InteractionResult useBoneMeal(Level level, BlockPos pos, Direction side, Player player, ItemStack stack) {
		int count = stack.getCount();
		if (count < 4) {
			return InteractionResult.PASS;
		}
		int successfulTargets = 0;
		for (BlockPos currentPos : WorldHelper.horizontalPositionsAround(pos, 15)) {
			boolean wasSuccessful = false;
			BlockState state = level.getBlockState(currentPos);
			//TODO: Do we want to fire this with a different stack if we have already used the four that we accounted?
			net.minecraft.world.level.block.BonemealSource bonemealSource = net.minecraft.world.level.block.BonemealSource.INTERACTION;
			BonemealEvent event = EventHooks.fireBonemealEvent(player, level, currentPos, state, bonemealSource, stack);
			if (event.isCanceled()) {
				wasSuccessful = event.isSuccessful();
			} else if (event.isValidBonemealTarget()) {
				wasSuccessful = true;
				if (level instanceof ServerLevel serverLevel) {
					//Note: We mirror vanilla only checking isBonemealSuccess on the server side
					BonemealableBlock growable = (BonemealableBlock) state.getBlock();
					if (growable.isBonemealSuccess(level, level.getRandom(), currentPos, state, bonemealSource)) {
						growable.performBonemeal(serverLevel, level.getRandom(), currentPos, state, bonemealSource);
						player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
						level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
					}
				}
			}
			if (wasSuccessful) {
				successfulTargets++;
			} else {
				BlockPos posAgainst = currentPos.relative(side.getOpposite());
				if (level.getBlockState(posAgainst).isFaceSturdy(level, posAgainst, side) && BoneMealItem.growWaterPlant(ItemStack.EMPTY, level, currentPos, side)) {
					successfulTargets++;
					if (level.isClientSide()) {
						break;
					}
					player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
					level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, currentPos, 0);
				}
			}
		}
		if (!level.isClientSide()) {
			int alreadyRemoved = count - stack.getCount();
			if (alreadyRemoved >= 0 && alreadyRemoved <= 4) {
				//Note: We do this before checking successful targets is greater than zero so that we can sync changes that might have been made from the event
				// as shrinking by zero will just do nothing
				stack.shrink(Math.min(4 - alreadyRemoved, successfulTargets));
			}
			if (stack.getCount() != count) {
				player.inventoryMenu.broadcastChanges();
			}
		}
		return successfulTargets == 0 ? InteractionResult.FAIL : InteractionResult.CONSUME;
	}

	private boolean plantSeeds(Level level, Player player, BlockPos pos) {
		List<ItemStack> seeds = getAllSeeds(ItemHelper.getInventoryStacks(player.getInventory()));
		if (seeds.isEmpty()) {
			return false;
		}
		boolean hasPlantedAny = false;
		for (BlockPos currentPos : WorldHelper.horizontalPositionsAround(pos, 8)) {
			BlockState state = level.getBlockState(currentPos);
			if (state.isAir()) {
				continue;
			}
			BlockPos plantPos = currentPos.above();
			BlockPlaceContext placeContext = null;
			for (Iterator<ItemStack> iterator = seeds.iterator(); iterator.hasNext(); ) {
				ItemStack stack = iterator.next();
				boolean planted = false;
				//Note: Unlike the patched in check in HarvestFarmland, we check special plantable before falling back to the item as a block item
				if (stack.getItem() instanceof SpecialPlantable plantable) {
					if (plantable.canPlacePlantAtPosition(stack, level, plantPos, Direction.DOWN)) {
						plantable.spawnPlantAtPosition(stack, level, plantPos, Direction.DOWN);
						planted = true;
					}
				} else if (stack.is(PETags.Items.PLANTABLE_SEEDS) && stack.getItem() instanceof BlockItem blockItem) {
					if (placeContext == null) {
						placeContext = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, new BlockHitResult(
								Vec3.atCenterOf(currentPos).relative(Direction.UP, 0.5), Direction.UP, currentPos, false
						));
						//Note: We don't want to replace the block we are trying to place against
						placeContext.replaceClicked = false;
					}
					//Effectively does the checks from BlockItem#getPlacementState
					if (placeContext.canPlace()) {
						//Note: Unlike villagers, we try to get the state for placement based on the stack that is being used
						// and have to validate if it can survive at the given position
						//Note: BlockItem#getPlacementState returns null if the state can't survive at the given position
						BlockState plantState = blockItem.getPlacementState(placeContext);
						if (plantState != null) {
							//If the plant will be able to survive at the position, go ahead and actually place it
							level.setBlockAndUpdate(plantPos, plantState);
							level.gameEvent(GameEvent.BLOCK_PLACE, plantPos, GameEvent.Context.of(player, plantState));
							planted = true;
						}
					}
				}
				if (planted) {
					hasPlantedAny = true;
					stack.shrink(1);
					player.inventoryMenu.broadcastChanges();
					if (stack.isEmpty()) {
						iterator.remove();
						if (seeds.isEmpty()) {
							//If we are out of seeds, hard exit the method
							return hasPlantedAny;
						}
					}
					//Once we set a seed in that position, break out of trying to place other seeds in that position
					break;
				}
			}
		}
		return hasPlantedAny;
	}

	private List<ItemStack> getAllSeeds(NonNullList<ItemStack> inv) {
		List<ItemStack> result = new ArrayList<>();
		for (ItemStack stack : inv) {
			if (!stack.isEmpty()) {
				Item item = stack.getItem();
				if (item instanceof SpecialPlantable || stack.is(PETags.Items.PLANTABLE_SEEDS) && item instanceof BlockItem) {
					result.add(stack);
				}
			}
		}
		return result;
	}

	@Override
	public <PEDESTAL extends BlockEntity & IDMPedestal> boolean updateInPedestal(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockPos pos,
			@NotNull PEDESTAL pedestal) {
		if (!level.isClientSide() && ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			if (pedestal.getActivityCooldown() == 0) {
				WorldHelper.growNearbyRandomly(true, level, pedestal.getEffectBounds(), null);
				pedestal.setActivityCooldown(level, pos, ProjectEConfig.server.cooldown.pedestal.harvest.get());
			} else {
				pedestal.decrementActivityCooldown(level, pos);
			}
		}
		return false;
	}

	@NotNull
	@Override
	public List<Component> getPedestalDescription(float tickRate) {
		List<Component> list = new ArrayList<>();
		if (ProjectEConfig.server.cooldown.pedestal.harvest.get() != -1) {
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_1.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_2.translateColored(ChatFormatting.BLUE));
			list.add(PELang.PEDESTAL_HARVEST_GODDESS_3.translateColored(ChatFormatting.BLUE, MathUtils.tickToSecFormatted(ProjectEConfig.server.cooldown.pedestal.harvest.get(), tickRate)));
		}
		return list;
	}
}
