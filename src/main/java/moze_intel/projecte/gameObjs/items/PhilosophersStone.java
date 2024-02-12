package moze_intel.projecte.gameObjs.items;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.items.PhilosophersStone.PhilosophersStoneMode;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhilosophersStone extends ItemMode<PhilosophersStoneMode> implements IProjectileShooter, IExtraFunction {

	public PhilosophersStone(Properties props) {
		super(props, 4);
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return stack.copy();
	}

	public BlockHitResult getHitBlock(Player player) {
		return getPlayerPOVHitResult(player.level(), player, player.isSecondaryUseActive() ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Player player = ctx.getPlayer();
		if (player == null) {
			return InteractionResult.FAIL;
		}
		BlockPos pos = ctx.getClickedPos();
		Direction sideHit = ctx.getClickedFace();
		Level level = ctx.getLevel();
		ItemStack stack = ctx.getItemInHand();

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		BlockHitResult rtr = getHitBlock(player);
		if (rtr.getType() == HitResult.Type.BLOCK && !rtr.getBlockPos().equals(pos)) {
			pos = rtr.getBlockPos();
			sideHit = rtr.getDirection();
		}
		Map<BlockPos, BlockState> toChange = getChanges(level, pos, player, sideHit, getMode(stack), getCharge(stack));
		if (!toChange.isEmpty()) {
			for (Map.Entry<BlockPos, BlockState> entry : toChange.entrySet()) {
				BlockPos currentPos = entry.getKey();
				PlayerHelper.checkedReplaceBlock((ServerPlayer) player, currentPos, entry.getValue());
				if (level.random.nextInt(8) == 0) {
					((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, currentPos.getX(), currentPos.getY() + 1, currentPos.getZ(), 2, 0, 0, 0, 0);
				}
			}
			level.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.TRANSMUTE.get(), SoundSource.PLAYERS, 1, 1);
		}
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
	public boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, InteractionHand hand) {
		if (!player.level().isClientSide) {
			player.openMenu(new ContainerProvider(stack));
		}
		return true;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
		super.appendHoverText(stack, level, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_PHILOSTONE.translate(ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
	}

	public static Map<BlockPos, BlockState> getChanges(Level level, BlockPos pos, Player player, Direction sideHit, PhilosophersStoneMode mode, int charge) {
		BlockState targeted = level.getBlockState(pos);
		boolean isSneaking = player.isSecondaryUseActive();
		BlockState result = WorldTransmutations.getWorldTransmutation(targeted, isSneaking);
		if (result == null) {
			//Targeted block has no transmutations, no positions
			return Collections.emptyMap();
		}
		Iterable<BlockPos> targets = switch (mode) {
			case CUBE -> WorldHelper.positionsAround(pos, charge);
			case PANEL -> switch (sideHit.getAxis()) {
				case X -> WorldHelper.positionsAround(pos, 0, charge, charge);
				case Y -> WorldHelper.horizontalPositionsAround(pos, charge);
				case Z -> WorldHelper.positionsAround(pos, charge, charge, 0);
			};
			case LINE -> switch (player.getDirection().getAxis()) {
				case X -> WorldHelper.positionsAround(pos, charge, 0, 0);
				case Y -> null;
				case Z -> WorldHelper.positionsAround(pos, 0, 0, charge);
			};
		};
		if (targets == null) {
			return Collections.emptyMap();
		}
		Map<BlockState, BlockState> conversions = new Object2ObjectArrayMap<>();
		conversions.put(targeted, result);
		Map<BlockPos, BlockState> changes = new HashMap<>();
		Block targetBlock = targeted.getBlock();
		for (BlockPos currentPos : targets) {
			BlockState state = level.getBlockState(currentPos);
			if (state.is(targetBlock)) {
				BlockState actualResult;
				if (conversions.containsKey(state)) {
					actualResult = conversions.get(state);
				} else {
					conversions.put(state, actualResult = WorldTransmutations.getWorldTransmutation(state, isSneaking));
				}
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
	public AttachmentType<PhilosophersStoneMode> getAttachmentType() {
		return PEAttachmentTypes.PHILOSOPHERS_STONE_MODE.get();
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

		private final IHasTranslationKey langEntry;

		PhilosophersStoneMode(IHasTranslationKey langEntry) {
			this.langEntry = langEntry;
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