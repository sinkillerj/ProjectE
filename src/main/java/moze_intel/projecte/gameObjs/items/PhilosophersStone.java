package moze_intel.projecte.gameObjs.items;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.api.world_transmutation.IWorldTransmutationFunction;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.items.PhilosophersStone.PhilosophersStoneMode;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.world_transmutation.WorldTransmutationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class PhilosophersStone extends ItemMode<PhilosophersStoneMode> implements IProjectileShooter, IExtraFunction, ISelfCraftingRemainder {

	public PhilosophersStone(Properties props) {
		super(props.component(PEDataComponentTypes.PHILOSOPHERS_STONE_MODE, PhilosophersStoneMode.CUBE), 4);
	}

	public BlockHitResult getHitBlock(Level level, Player player, boolean isSneaking) {
		return getPlayerPOVHitResult(level, player, isSneaking ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
	}

	@NotNull
	@Override
	public InteractionResult onItemUseFirst(@NotNull ItemStack stack, @NotNull UseOnContext ctx) {
		//Note: We use this instead of useOn so that we can support blocks that have right click interactions (for example signs)
		Level level = ctx.getLevel();
		Player player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		Direction sideHit = ctx.getClickedFace();
		boolean isSneaking = ctx.isSecondaryUseActive();
		if (isSneaking && player != null) {
			//If the secondary use is active, see if we would hit a fluid before we get to the target
			//Note: If player is null, secondary use should be false. But in case an implementation overrides it, we need to check the player isn't null
			BlockHitResult rtr = getHitBlock(level, player, true);
			if (rtr.getType() == HitResult.Type.BLOCK && !rtr.getBlockPos().equals(pos)) {
				pos = rtr.getBlockPos();
				sideHit = rtr.getDirection();
			}
		}

		if (level.isClientSide()) {
			if (WorldTransmutationManager.INSTANCE.getWorldTransmutation(level.getBlockState(pos), true) == null) {
				//Pass if there is no world transmutation for the target block
				return InteractionResult.PASS;
			}
			return InteractionResult.SUCCESS;
		}

		Object2ReferenceMap<BlockPos, BlockState> toChange = getChanges(level, pos, sideHit, ctx.getHorizontalDirection(), isSneaking, getMode(stack), getCharge(stack));
		if (toChange.isEmpty()) {
			return InteractionResult.PASS;
		}
		for (Iterator<Object2ReferenceMap.Entry<BlockPos, BlockState>> iterator = Object2ReferenceMaps.fastIterator(toChange); iterator.hasNext(); ) {
			Object2ReferenceMap.Entry<BlockPos, BlockState> entry = iterator.next();
			BlockPos currentPos = entry.getKey();
			BlockState targetState = entry.getValue();
			//TODO: Figure out how to get it so that if we transmute something into grass and there is snow on top of that block
			// that we will mark the grass as being snowy
			if (player == null) {
				if (targetState.getBlock() instanceof SignBlock && level.getBlockEntity(currentPos) instanceof SignBlockEntity sign) {
					level.setBlockAndUpdate(currentPos, targetState);
					WorldHelper.copySignData(level, currentPos, sign);
				} else {
					level.setBlockAndUpdate(currentPos, targetState);
				}
			} else {
				PlayerHelper.checkedReplaceBlock((ServerPlayer) player, level, currentPos, targetState);
			}
			if (level.getRandom().nextInt(8) == 0) {
				((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, currentPos.getX(), currentPos.getY() + 1, currentPos.getZ(), 2, 0, 0, 0, 0);
			}
		}
		BlockPos soundPos = player == null ? pos : player.blockPosition();
		level.playSound(null, soundPos.getX(), soundPos.getY(), soundPos.getZ(), PESoundEvents.TRANSMUTE.get(), SoundSource.PLAYERS, 1, 1);
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean shootProjectile(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		Level level = player.level();
		level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.TRANSMUTE.get(), SoundSource.PLAYERS, 1, 1);
		EntityMobRandomizer ent = new EntityMobRandomizer(player, level);
		ent.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
		level.addFreshEntity(ent);
		return true;
	}

	@Override
	public boolean doExtraFunction(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		if (!player.level().isClientSide()) {
			player.openMenu(new ContainerProvider(stack));
		}
		return true;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, context, display, tooltip, flags);
		tooltip.accept(PELang.TOOLTIP_PHILOSTONE.translate(ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
	}

	public static Object2ReferenceMap<BlockPos, BlockState> getChanges(Level level, BlockPos pos, Direction sideHit, Direction horizontalDirection, boolean isSneaking,
			PhilosophersStoneMode mode, int charge) {
		IWorldTransmutationFunction transmutation = WorldTransmutationManager.INSTANCE.getWorldTransmutation(level.getBlockState(pos));
		if (transmutation == null) {
			//Targeted block has no transmutations, no positions
			return Object2ReferenceMaps.emptyMap();
		}
		Iterable<BlockPos> targets = switch (mode) {
			case CUBE -> WorldHelper.positionsAround(pos, charge);
			case PANEL -> switch (sideHit.getAxis()) {
				case X -> WorldHelper.positionsAround(pos, 0, charge, charge);
				case Y -> WorldHelper.horizontalPositionsAround(pos, charge);
				case Z -> WorldHelper.positionsAround(pos, charge, charge, 0);
			};
			case LINE -> switch (horizontalDirection.getAxis()) {
				case X -> WorldHelper.positionsAround(pos, charge, 0, 0);
				case Y -> null;
				case Z -> WorldHelper.positionsAround(pos, 0, 0, charge);
			};
		};
		if (targets == null) {
			return Object2ReferenceMaps.emptyMap();
		}
		Object2ReferenceMap<BlockPos, BlockState> changes = new Object2ReferenceOpenHashMap<>();
		for (BlockPos currentPos : targets) {
			if (WorldHelper.isBlockLoaded(level, currentPos)) {
				BlockState actualResult = transmutation.result(level.getBlockState(currentPos), isSneaking);
				//We allow for null keys to avoid having to look it up again from the world transmutations
				// which may be slightly slower, but we only add it as a position to change if we have a result
				if (actualResult != null) {
					changes.put(currentPos.immutable(), actualResult);
				}
			}
		}
		return changes;
	}

	@Override
	public DataComponentType<PhilosophersStoneMode> getDataComponentType() {
		return PEDataComponentTypes.PHILOSOPHERS_STONE_MODE.get();
	}

	@Override
	public PhilosophersStoneMode getDefaultMode() {
		return PhilosophersStoneMode.CUBE;
	}

	private record ContainerProvider(ItemStack stack) implements MenuProvider {

		@NotNull
		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
			return new PhilosStoneContainer(windowId, playerInventory, ContainerLevelAccess.create(player.level(), player.blockPosition()));
		}

		@NotNull
		@Override
		public Component getDisplayName() {
			return stack.getHoverName();
		}
	}

	public enum PhilosophersStoneMode implements IModeEnum<PhilosophersStoneMode> {
		CUBE(PELang.MODE_PHILOSOPHER_1),
		PANEL(PELang.MODE_PHILOSOPHER_2),
		LINE(PELang.MODE_PHILOSOPHER_3);

		public static final Codec<PhilosophersStoneMode> CODEC = StringRepresentable.fromEnum(PhilosophersStoneMode::values);
		public static final IntFunction<PhilosophersStoneMode> BY_ID = ByIdMap.continuous(PhilosophersStoneMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
		public static final StreamCodec<ByteBuf, PhilosophersStoneMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PhilosophersStoneMode::ordinal);

		private final IHasTranslationKey langEntry;
		private final String serializedName;

		PhilosophersStoneMode(IHasTranslationKey langEntry) {
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

		@Override
		public PhilosophersStoneMode next(ItemStack stack) {
			return switch (this) {
				case CUBE -> PANEL;
				case PANEL -> LINE;
				case LINE -> CUBE;
			};
		}
	}
}
