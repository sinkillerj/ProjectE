package moze_intel.projecte.gameObjs.items;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MercurialEye extends ItemMode implements IExtraFunction, ICapabilityAware {

	private static final int CREATION_MODE = 0;
	private static final int EXTENSION_MODE = 1;
	private static final int EXTENSION_MODE_CLASSIC = 2;
	private static final int TRANSMUTATION_MODE = 3;
	private static final int TRANSMUTATION_MODE_CLASSIC = 4;
	private static final int PILLAR_MODE = 5;

	public MercurialEye(Properties props) {
		super(props, (byte) 4, PELang.MODE_MERCURIAL_EYE_1, PELang.MODE_MERCURIAL_EYE_2, PELang.MODE_MERCURIAL_EYE_3, PELang.MODE_MERCURIAL_EYE_4,
				PELang.MODE_MERCURIAL_EYE_5, PELang.MODE_MERCURIAL_EYE_6);
	}

	@Override
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, InteractionHand hand) {
		int selected = player.getInventory().selected;
		MenuProvider provider = new SimpleMenuProvider((id, inv, pl) -> new MercurialEyeContainer(id, inv, hand, selected), stack.getHoverName());
		player.openMenu(provider, b -> {
			b.writeEnum(hand);
			b.writeByte(selected);
		});
		return true;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		return ctx.getLevel().isClientSide ? InteractionResult.SUCCESS : formBlocks(stack, ctx.getPlayer(), ctx.getHand(), ctx.getClickedPos(), ctx.getClickedFace());
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getMode(stack) == CREATION_MODE) {
			if (level.isClientSide) {
				return InteractionResultHolder.success(stack);
			}
			Vec3 eyeVec = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
			Vec3 lookVec = player.getLookAngle();
			//I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
			Vec3 targVec = eyeVec.add(lookVec.x * 2, lookVec.y * 2, lookVec.z * 2);
			return ItemHelper.actionResultFromType(formBlocks(stack, player, hand, BlockPos.containing(targVec), null), stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	private void playNoEMCSound(Player player) {
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	private InteractionResult formBlocks(ItemStack eye, Player player, InteractionHand hand, BlockPos startingPos, @Nullable Direction facing) {
		IItemHandler inventory = eye.getCapability(ItemHandler.ITEM);
		if (inventory == null) {
			return InteractionResult.FAIL;
		}
		ItemStack klein = inventory.getStackInSlot(0);
		if (klein.isEmpty() || klein.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY) == null) {
			playNoEMCSound(player);
			return InteractionResult.FAIL;
		}

		Level level = player.level();
		BlockState startingState = level.getBlockState(startingPos);
		long startingBlockEmc = EMCHelper.getEmcValue(new ItemStack(startingState.getBlock()));
		ItemStack target = inventory.getStackInSlot(1);
		BlockState newState;
		long newBlockEmc;
		byte mode = getMode(eye);
		BlockPlaceContext context;
		BlockHitResult hitResult;
		if (facing == null) {
			hitResult = new BlockHitResult(Vec3.atCenterOf(startingPos), Direction.UP, startingPos, true);
		} else {
			hitResult = new BlockHitResult(Vec3.atCenterOf(startingPos.relative(facing)), facing, startingPos, false);
		}
		if (!target.isEmpty()) {
			context = new BlockPlaceContext(level, player, hand, target.copy(), hitResult);
			newState = ItemHelper.stackToState(target, context);
			newBlockEmc = EMCHelper.getEmcValue(target);
			if (newBlockEmc == 0) {
				//If the target no longer has an EMC value fail
				return InteractionResult.FAIL;
			}
		} else if (startingBlockEmc != 0 && (mode == EXTENSION_MODE || mode == EXTENSION_MODE_CLASSIC)) {
			//If there is no item key, attempt to determine it for extension mode
			newState = startingState;
			newBlockEmc = startingBlockEmc;
			context = new BlockPlaceContext(level, player, hand, new ItemStack(newState.getBlock()), hitResult);
		} else {
			return InteractionResult.FAIL;
		}
		if (newState == null || newState.isAir()) {
			return InteractionResult.FAIL;
		}

		NonNullList<ItemStack> drops = NonNullList.create();
		int charge = getCharge(eye);
		int hitTargets = 0;
		if (mode == CREATION_MODE) {
			if (facing != null && (!context.replacingClickedOnBlock() || player.isSecondaryUseActive() && !startingState.isAir())) {
				BlockPos offsetPos = startingPos.relative(facing);
				BlockState offsetState = level.getBlockState(offsetPos);
				if (!offsetState.canBeReplaced(context)) {
					return InteractionResult.FAIL;
				}
				long offsetBlockEmc = EMCHelper.getEmcValue(new ItemStack(offsetState.getBlock()));
				//Just in case it is not air but is a replaceable block like tall grass, get the proper EMC instead of just using 0
				if (doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc, drops)) {
					hitTargets++;
				}
			} else if (doBlockPlace(player, startingState, startingPos, newState, eye, startingBlockEmc, newBlockEmc, drops)) {
				//Otherwise replace it (it may have been air), or it may have been something like tall grass
				hitTargets++;
			}
		} else if (mode == PILLAR_MODE) {
			//Fills in replaceable blocks in up to a 3x3x3/6/9/12/15 area
			hitTargets += fillGaps(eye, player, level, context, startingState, newState, newBlockEmc, getCorners(startingPos, facing, 1, 3 * charge + 2), drops);
		} else if (mode == EXTENSION_MODE_CLASSIC) {
			//if it is replaceable fill in the gaps in up to a 9x9x1 area
			hitTargets += fillGaps(eye, player, level, context, startingState, newState, newBlockEmc, getCorners(startingPos, facing, charge, 0), drops);
		} else if (mode == TRANSMUTATION_MODE_CLASSIC) {
			//if state is same as the start state replace it in an up to 9x9x1 area
			Pair<BlockPos, BlockPos> corners = getCorners(startingPos, facing, charge, 0);
			for (BlockPos pos : WorldHelper.getPositionsFromBox(AABB.encapsulatingFullBlocks(corners.getLeft(), corners.getRight()))) {
				BlockState placedState = level.getBlockState(pos);
				//Ensure we are immutable so that removal/placing doesn't act weird
				if (placedState == startingState && doBlockPlace(player, placedState, pos.immutable(), newState, eye, startingBlockEmc, newBlockEmc, drops)) {
					hitTargets++;
				}
			}
		} else {
			if (startingState.isAir() || facing == null) {
				return InteractionResult.FAIL;
			}

			LinkedList<BlockPos> possibleBlocks = new LinkedList<>();
			Set<BlockPos> visited = new HashSet<>();
			possibleBlocks.add(startingPos);
			visited.add(startingPos);

			int side = 2 * charge + 1;
			int size = side * side;
			int totalTries = size * 4;
			for (int attemptedTargets = 0; attemptedTargets < totalTries && !possibleBlocks.isEmpty(); attemptedTargets++) {
				BlockPos pos = possibleBlocks.poll();
				BlockState checkState = level.getBlockState(pos);
				if (startingState != checkState) {
					continue;
				}
				BlockPos offsetPos = pos.relative(facing);
				BlockState offsetState = level.getBlockState(offsetPos);
				if (!offsetState.isFaceSturdy(level, offsetPos, facing)) {
					boolean hit = false;
					if (mode == EXTENSION_MODE) {
						VoxelShape cbBox = startingState.getCollisionShape(level, offsetPos);
						if (level.isUnobstructed(null, cbBox)) {
							long offsetBlockEmc = EMCHelper.getEmcValue(offsetState.getBlock());
							hit = doBlockPlace(player, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc, drops);
						}
					} else if (mode == TRANSMUTATION_MODE) {
						hit = doBlockPlace(player, checkState, pos, newState, eye, startingBlockEmc, newBlockEmc, drops);
					}

					if (hit) {
						hitTargets++;
						if (hitTargets >= size) {
							break;
						}
						for (Direction e : Direction.values()) {
							if (facing.getAxis() != e.getAxis()) {
								BlockPos offset = pos.relative(e);
								if (visited.add(offset)) {
									possibleBlocks.offer(offset);
								}
								BlockPos offsetOpposite = pos.relative(e.getOpposite());
								if (visited.add(offsetOpposite)) {
									possibleBlocks.offer(offsetOpposite);
								}
							}
						}
					}
				}
			}
		}

		if (hitTargets > 0) {
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.POWER.get(), SoundSource.PLAYERS, 0.8F, 2F / ((float) charge / getNumCharges(eye) + 2F));
			if (!drops.isEmpty()) {
				//Make all the drops fall together
				WorldHelper.createLootDrop(drops, player.level(), startingPos);
			}
		}
		return InteractionResult.CONSUME;
	}

	private boolean doBlockPlace(Player player, BlockState oldState, BlockPos placePos, BlockState newState, ItemStack eye, long oldEMC, long newEMC, NonNullList<ItemStack> drops) {
		IItemHandler inventory = eye.getCapability(ItemHandler.ITEM);
		if (inventory == null) {
			return false;
		}
		ItemStack klein = inventory.getStackInSlot(0);
		if (klein.isEmpty()) {
			playNoEMCSound(player);
			return false;
		}
		IItemEmcHolder emcHolder = klein.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder == null) {
			playNoEMCSound(player);
			return false;
		} else if (oldState == newState) {
			return false;
		} else if (ItemPE.getEmc(klein) < newEMC - oldEMC) {
			playNoEMCSound(player);
			return false;
		} else if (WorldHelper.getBlockEntity(player.level(), placePos) != null) {
			return false;
		}

		if (oldEMC == 0 && oldState.getDestroySpeed(player.level(), placePos) == -1.0F) {
			//Don't allow replacing unbreakable blocks (unless they have an EMC value)
			return false;
		}

		ServerPlayer serverPlayer = (ServerPlayer) player;
		if (PlayerHelper.checkedReplaceBlock(serverPlayer, placePos, newState)) {
			if (oldEMC == 0) {
				//Drop the block because it doesn't have an emc value
				drops.addAll(Block.getDrops(oldState, serverPlayer.serverLevel(), placePos, null, player, eye));
				emcHolder.extractEmc(klein, newEMC, EmcAction.EXECUTE);
			} else if (oldEMC > newEMC) {
				emcHolder.insertEmc(klein, oldEMC - newEMC, EmcAction.EXECUTE);
			} else if (oldEMC < newEMC) {
				emcHolder.extractEmc(klein, newEMC - oldEMC, EmcAction.EXECUTE);
			}
			return true;
		}
		return false;
	}

	private int fillGaps(ItemStack eye, Player player, Level level, BlockPlaceContext context, BlockState startingState, BlockState newState, long newBlockEmc,
			Pair<BlockPos, BlockPos> corners, NonNullList<ItemStack> drops) {
		int hitTargets = 0;
		for (BlockPos pos : WorldHelper.getPositionsFromBox(AABB.encapsulatingFullBlocks(corners.getLeft(), corners.getRight()))) {
			VoxelShape bb = startingState.getCollisionShape(level, pos);
			if (level.isUnobstructed(null, bb)) {
				BlockPlaceContext adjustedContext = BlockPlaceContext.at(context, pos, context.getClickedFace());
				if (adjustedContext.replacingClickedOnBlock()) {
					BlockState placeState = level.getBlockState(pos);
					//Only replace replaceable blocks
					long placeBlockEmc = EMCHelper.getEmcValue(placeState.getBlock());
					//Ensure we are immutable so that changing blocks doesn't act weird
					if (doBlockPlace(player, placeState, pos.immutable(), newState, eye, placeBlockEmc, newBlockEmc, drops)) {
						hitTargets++;
					}
				}
			}
		}
		return hitTargets;
	}

	private Pair<BlockPos, BlockPos> getCorners(BlockPos startingPos, Direction facing, int strength, int depth) {
		if (facing == null) {
			return new ImmutablePair<>(startingPos, startingPos);
		}
		BlockPos start = startingPos;
		BlockPos end = startingPos;
		switch (facing) {
			case UP -> {
				start = start.offset(-strength, -depth, -strength);
				end = end.offset(strength, 0, strength);
			}
			case DOWN -> {
				start = start.offset(-strength, 0, -strength);
				end = end.offset(strength, depth, strength);
			}
			case SOUTH -> {
				start = start.offset(-strength, -strength, -depth);
				end = end.offset(strength, strength, 0);
			}
			case NORTH -> {
				start = start.offset(-strength, -strength, 0);
				end = end.offset(strength, strength, depth);
			}
			case EAST -> {
				start = start.offset(-depth, -strength, -strength);
				end = end.offset(0, strength, strength);
			}
			case WEST -> {
				start = start.offset(0, -strength, -strength);
				end = end.offset(depth, strength, strength);
			}
		}
		return new ImmutablePair<>(start, end);
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		//TODO - 1.20.4: Test this
		event.registerItem(ItemHandler.ITEM, (stack, context) -> stack.getData(PEAttachmentTypes.EYE_INVENTORY), this);
	}
}