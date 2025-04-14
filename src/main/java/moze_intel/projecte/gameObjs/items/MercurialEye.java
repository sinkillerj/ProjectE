package moze_intel.projecte.gameObjs.items;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.function.IntFunction;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.MercurialEyeContainer;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.items.MercurialEye.MercurialEyeMode;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
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
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MercurialEye extends ItemMode<MercurialEyeMode> implements IExtraFunction, ICapabilityAware {

	public MercurialEye(Properties props) {
		super(props.component(PEDataComponentTypes.MERCURIAL_EYE_MODE, MercurialEyeMode.CREATION)
						.component(PEDataComponentTypes.EYE_INVENTORY, ItemContainerContents.EMPTY),
				4
		);
	}

	@Override
	public boolean doExtraFunction(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
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
		Level level = ctx.getLevel();
		return level.isClientSide ? InteractionResult.SUCCESS : formBlocks(stack, ctx.getPlayer(), ctx.getHand(), level, ctx.getClickedPos(), ctx.getClickedFace());
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getMode(stack) == MercurialEyeMode.CREATION) {
			if (level.isClientSide) {
				return InteractionResultHolder.success(stack);
			}
			//I'm not sure why there has to be a one point offset to the X coordinate here, but it's pretty consistent in testing.
			Vec3 targVec = PlayerHelper.getLookTarget(player, 2);
			return ItemHelper.actionResultFromType(formBlocks(stack, player, hand, level, BlockPos.containing(targVec), null), stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	private void playNoEMCSound(Player player) {
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.UNCHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	private InteractionResult formBlocks(ItemStack eye, Player player, InteractionHand hand, Level level, BlockPos startingPos, @Nullable Direction facing) {
		IItemHandler inventory = eye.getCapability(ItemHandler.ITEM);
		if (inventory == null) {
			return InteractionResult.FAIL;
		}
		ItemStack klein = inventory.getStackInSlot(0);
		if (klein.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY) == null) {
			playNoEMCSound(player);
			return InteractionResult.FAIL;
		}

		BlockState startingState = level.getBlockState(startingPos);
		long startingBlockEmc = IEMCProxy.INSTANCE.getValue(startingState.getBlock());
		ItemStack target = inventory.getStackInSlot(1);
		BlockState newState;
		long newBlockEmc;
		MercurialEyeMode mode = getMode(eye);
		BlockPlaceContext context;
		BlockHitResult hitResult;
		if (facing == null) {
			hitResult = new BlockHitResult(startingPos.getCenter(), Direction.UP, startingPos, true);
		} else {
			hitResult = new BlockHitResult(startingPos.relative(facing).getCenter(), facing, startingPos, false);
		}
		if (!target.isEmpty()) {
			context = new BlockPlaceContext(level, player, hand, target.copy(), hitResult);
			newState = ItemHelper.stackToState(target, context);
			newBlockEmc = IEMCProxy.INSTANCE.getValue(target);
			if (newBlockEmc == 0) {
				//If the target no longer has an EMC value fail
				return InteractionResult.FAIL;
			}
		} else if (startingBlockEmc != 0 && mode.isExtension()) {
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
		if (mode == MercurialEyeMode.CREATION) {
			if (facing != null && (!context.replacingClickedOnBlock() || context.isSecondaryUseActive() && !startingState.isAir())) {
				BlockPos offsetPos = startingPos.relative(facing);
				BlockState offsetState = level.getBlockState(offsetPos);
				if (!offsetState.canBeReplaced(context)) {
					return InteractionResult.FAIL;
				}
				long offsetBlockEmc = IEMCProxy.INSTANCE.getValue(offsetState.getBlock());
				//Just in case it is not air but is a replaceable block like tall grass, get the proper EMC instead of just using 0
				if (doBlockPlace(player, level, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc, drops, context)) {
					hitTargets++;
				}
			} else if (doBlockPlace(player, level, startingState, startingPos, newState, eye, startingBlockEmc, newBlockEmc, drops, context)) {
				//Otherwise replace it (it may have been air), or it may have been something like tall grass
				hitTargets++;
			}
		} else if (mode == MercurialEyeMode.PILLAR) {
			//Fills in replaceable blocks in up to a 3x3x3/6/9/12/15 area
			AABB bounds = getBounds(startingPos, facing, 1, 3 * charge + 2);
			hitTargets += fillGaps(eye, player, level, context, startingState, newState, newBlockEmc, bounds, drops);
		} else if (mode == MercurialEyeMode.EXTENSION_CLASSIC) {
			//if it is replaceable fill in the gaps in up to a 9x9x1 area
			AABB bounds = getBounds(startingPos, facing, charge, 0);
			hitTargets += fillGaps(eye, player, level, context, startingState, newState, newBlockEmc, bounds, drops);
		} else if (mode == MercurialEyeMode.TRANSMUTATION_CLASSIC) {
			//if state is same as the start state replace it in an up to 9x9x1 area
			AABB bounds = getBounds(startingPos, facing, charge, 0);
			for (BlockPos pos : WorldHelper.getPositionsInBox(bounds)) {
				BlockState placedState = level.getBlockState(pos);
				//Ensure we are immutable so that removal/placing doesn't act weird
				if (placedState == startingState && doBlockPlace(player, level, placedState, pos.immutable(), newState, eye, startingBlockEmc, newBlockEmc, drops, context)) {
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
					if (mode == MercurialEyeMode.EXTENSION) {
						VoxelShape cbBox = startingState.getCollisionShape(level, offsetPos);
						if (level.isUnobstructed(null, cbBox)) {
							long offsetBlockEmc = IEMCProxy.INSTANCE.getValue(offsetState.getBlock());
							hit = doBlockPlace(player, level, offsetState, offsetPos, newState, eye, offsetBlockEmc, newBlockEmc, drops, context);
						}
					} else if (mode == MercurialEyeMode.TRANSMUTATION) {
						hit = doBlockPlace(player, level, checkState, pos, newState, eye, startingBlockEmc, newBlockEmc, drops, context);
					}

					if (hit) {
						hitTargets++;
						if (hitTargets >= size) {
							break;
						}
						for (Direction e : Constants.DIRECTIONS) {
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
			//Make all the drops fall together
			WorldHelper.createLootDrop(drops, player.level(), startingPos);
		}
		return InteractionResult.CONSUME;
	}

	private boolean doBlockPlace(Player player, Level level, BlockState oldState, BlockPos placePos, BlockState newState, ItemStack eye, long oldEMC, long newEMC,
			NonNullList<ItemStack> drops, BlockPlaceContext context) {
		if (oldState.hasBlockEntity()) {
			return false;
		} else if (!newState.getValues().isEmpty()) {
			//If the block has multiple states, make sure we update the state based on where we are placing it
			BlockPlaceContext adjustedContext = BlockPlaceContext.at(context, placePos, context.getClickedFace());
			//Ensure that the context returns the actual spot and knows we are replacing the existing state
			adjustedContext.replaceClicked = true;
			newState = newState.getBlock().getStateForPlacement(adjustedContext);
			if (newState == null) {
				return false;
			}
		}
		return doBlockPlace(player, level, oldState, placePos, newState, eye, oldEMC, newEMC, drops);
	}

	private boolean doBlockPlace(Player player, Level level, BlockState oldState, BlockPos placePos, BlockState newState, ItemStack eye, long oldEMC, long newEMC,
			NonNullList<ItemStack> drops) {
		if (oldState == newState || oldState.hasBlockEntity()) {
			return false;
		}
		IItemHandler inventory = eye.getCapability(ItemHandler.ITEM);
		if (inventory == null) {
			return false;
		}
		ItemStack klein = inventory.getStackInSlot(0);
		IItemEmcHolder emcHolder = klein.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder == null || emcHolder.getStoredEmc(klein) < newEMC - oldEMC) {
			playNoEMCSound(player);
			return false;
		} else if (oldEMC == 0 && oldState.getDestroySpeed(level, placePos) == Block.INDESTRUCTIBLE) {
			//Don't allow replacing unbreakable blocks (unless they have an EMC value)
			return false;
		}

		ServerPlayer serverPlayer = (ServerPlayer) player;
		if (PlayerHelper.checkedReplaceBlock(serverPlayer, level, placePos, newState)) {
			//Note: We have to copy it, as components are immutable
			ItemStack replacement = klein.copy();
			if (oldEMC > newEMC) {
				emcHolder.insertEmc(replacement, oldEMC - newEMC, EmcAction.EXECUTE);
			} else if (oldEMC != newEMC) {
				if (oldEMC == 0) {
					//Drop the block because it doesn't have an emc value
					drops.addAll(Block.getDrops(oldState, serverPlayer.serverLevel(), placePos, null, player, eye));
				}
				emcHolder.extractEmc(replacement, newEMC - oldEMC, EmcAction.EXECUTE);
			}
			if (inventory instanceof IItemHandlerModifiable modifiable) {
				modifiable.setStackInSlot(0, replacement);
			}
			return true;
		}
		return false;
	}

	private int fillGaps(ItemStack eye, Player player, Level level, BlockPlaceContext context, BlockState startingState, BlockState newState, long newBlockEmc,
			AABB bounds, NonNullList<ItemStack> drops) {
		int hitTargets = 0;
		for (BlockPos pos : WorldHelper.getPositionsInBox(bounds)) {
			VoxelShape bb = startingState.getCollisionShape(level, pos);
			if (level.isUnobstructed(null, bb)) {
				BlockPlaceContext adjustedContext = BlockPlaceContext.at(context, pos, context.getClickedFace());
				if (adjustedContext.replacingClickedOnBlock()) {
					BlockState placeState = level.getBlockState(pos);
					//Only replace replaceable blocks
					long placeBlockEmc = IEMCProxy.INSTANCE.getValue(placeState.getBlock());
					//Ensure we are immutable so that changing blocks doesn't act weird
					if (!newState.getValues().isEmpty()) {
						//If the block has multiple states, make sure we update the state based on where we are placing it
						BlockState forPlacement = newState.getBlock().getStateForPlacement(adjustedContext);
						if (forPlacement == null) {
							continue;
						}
					}
					if (doBlockPlace(player, level, placeState, pos.immutable(), newState, eye, placeBlockEmc, newBlockEmc, drops)) {
						hitTargets++;
					}
				}
			}
		}
		return hitTargets;
	}

	private AABB getBounds(BlockPos startingPos, @Nullable Direction facing, int strength, int depth) {
		if (facing == null) {
			return new AABB(startingPos);
		}
		return WorldHelper.getBroadDeepBox(startingPos, facing, strength, depth);
	}

	@Override
	public void attachCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(ItemHandler.ITEM, (stack, context) -> new EyeItemHandler(stack), this);
	}

	@Override
	public DataComponentType<MercurialEyeMode> getDataComponentType() {
		return PEDataComponentTypes.MERCURIAL_EYE_MODE.get();
	}

	@Override
	public MercurialEyeMode getDefaultMode() {
		return MercurialEyeMode.CREATION;
	}

	private static class EyeItemHandler extends ComponentItemHandler {

		public EyeItemHandler(MutableDataComponentHolder parent) {
			super(parent, PEDataComponentTypes.EYE_INVENTORY.get(), 2);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			if (stack.isEmpty()) {
				return true;
			} else if (slot == 0) {
				return SlotPredicates.EMC_HOLDER.test(stack);
			} //slot == 1
			return SlotPredicates.MERCURIAL_TARGET.test(stack);
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		protected void updateContents(@NotNull ItemContainerContents contents, @NotNull ItemStack stack, int slot) {
			//Note: We just do a copy with count of one as the empty stack will stay empty
			super.updateContents(contents, stack.copyWithCount(1), slot);
		}
	}

	public enum MercurialEyeMode implements IModeEnum<MercurialEyeMode> {
		CREATION(PELang.MODE_MERCURIAL_EYE_1),
		EXTENSION(PELang.MODE_MERCURIAL_EYE_2),
		EXTENSION_CLASSIC(PELang.MODE_MERCURIAL_EYE_3),
		TRANSMUTATION(PELang.MODE_MERCURIAL_EYE_4),
		TRANSMUTATION_CLASSIC(PELang.MODE_MERCURIAL_EYE_5),
		PILLAR(PELang.MODE_MERCURIAL_EYE_6);

		public static final Codec<MercurialEyeMode> CODEC = StringRepresentable.fromEnum(MercurialEyeMode::values);
		public static final IntFunction<MercurialEyeMode> BY_ID = ByIdMap.continuous(MercurialEyeMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, MercurialEyeMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MercurialEyeMode::ordinal);

		private final IHasTranslationKey langEntry;
		private final String serializedName;

		MercurialEyeMode(IHasTranslationKey langEntry) {
			this.serializedName = name().toLowerCase(Locale.ROOT);
			this.langEntry = langEntry;
		}

		@NotNull
		@Override
		public String getSerializedName() {
			return serializedName;
		}

		@Override
		public String getTranslationKey() {
			return langEntry.getTranslationKey();
		}

		public boolean isExtension() {
			return this == EXTENSION || this == EXTENSION_CLASSIC;
		}

		@Override
		public MercurialEyeMode next(ItemStack stack) {
			return switch (this) {
				case CREATION -> EXTENSION;
				case EXTENSION -> EXTENSION_CLASSIC;
				case EXTENSION_CLASSIC -> TRANSMUTATION;
				case TRANSMUTATION -> TRANSMUTATION_CLASSIC;
				case TRANSMUTATION_CLASSIC -> PILLAR;
				case PILLAR -> CREATION;
			};
		}
	}
}