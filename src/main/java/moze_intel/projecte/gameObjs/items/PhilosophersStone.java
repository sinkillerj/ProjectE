package moze_intel.projecte.gameObjs.items;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.capability.ExtraFunctionItemCapabilityWrapper;
import moze_intel.projecte.capability.ProjectileShooterItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.PEKeybind;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class PhilosophersStone extends ItemMode implements IProjectileShooter, IExtraFunction {

	public PhilosophersStone(Properties props) {
		super(props, (byte) 4, PELang.MODE_PHILOSOPHER_1, PELang.MODE_PHILOSOPHER_2, PELang.MODE_PHILOSOPHER_3);
		addItemCapability(ExtraFunctionItemCapabilityWrapper::new);
		addItemCapability(ProjectileShooterItemCapabilityWrapper::new);
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return stack.copy();
	}

	public BlockRayTraceResult getHitBlock(PlayerEntity player) {
		return getPlayerPOVHitResult(player.getCommandSenderWorld(), player, player.isShiftKeyDown() ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
	}

	@Nonnull
	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		if (player == null) {
			return ActionResultType.FAIL;
		}
		BlockPos pos = ctx.getClickedPos();
		Direction sideHit = ctx.getClickedFace();
		World world = ctx.getLevel();
		ItemStack stack = ctx.getItemInHand();

		if (world.isClientSide) {
			return ActionResultType.SUCCESS;
		}

		BlockRayTraceResult rtr = getHitBlock(player);
		if (rtr.getType() == RayTraceResult.Type.BLOCK && !rtr.getBlockPos().equals(pos)) {
			pos = rtr.getBlockPos();
			sideHit = rtr.getDirection();
		}
		Map<BlockPos, BlockState> toChange = getChanges(world, pos, player, sideHit, getMode(stack), getCharge(stack));
		if (!toChange.isEmpty()) {
			for (Map.Entry<BlockPos, BlockState> entry : toChange.entrySet()) {
				BlockPos currentPos = entry.getKey();
				PlayerHelper.checkedReplaceBlock((ServerPlayerEntity) player, currentPos, entry.getValue());
				if (world.random.nextInt(8) == 0) {
					((ServerWorld) world).sendParticles(ParticleTypes.LARGE_SMOKE, currentPos.getX(), currentPos.getY() + 1, currentPos.getZ(), 2, 0, 0, 0, 0);
				}
			}
			world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.TRANSMUTE.get(), SoundCategory.PLAYERS, 1, 1);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		World world = player.getCommandSenderWorld();
		world.playSound(null, player.getX(), player.getY(), player.getZ(), PESoundEvents.TRANSMUTE.get(), SoundCategory.PLAYERS, 1, 1);
		EntityMobRandomizer ent = new EntityMobRandomizer(player, world);
		ent.shootFromRotation(player, player.xRot, player.yRot, 0, 1.5F, 1);
		world.addFreshEntity(ent);
		return true;
	}

	@Override
	public boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, Hand hand) {
		if (!player.getCommandSenderWorld().isClientSide) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(stack));
		}
		return true;
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltips, @Nonnull ITooltipFlag flags) {
		super.appendHoverText(stack, world, tooltips, flags);
		tooltips.add(PELang.TOOLTIP_PHILOSTONE.translate(ClientKeyHelper.getKeyName(PEKeybind.EXTRA_FUNCTION)));
	}

	public static Map<BlockPos, BlockState> getChanges(World world, BlockPos pos, PlayerEntity player, Direction sideHit, int mode, int charge) {
		BlockState targeted = world.getBlockState(pos);
		boolean isSneaking = player.isShiftKeyDown();
		BlockState result = WorldTransmutations.getWorldTransmutation(targeted, isSneaking);
		if (result == null) {
			//Targeted block has no transmutations, no positions
			return Collections.emptyMap();
		}
		Stream<BlockPos> stream = null;
		switch (mode) {
			case 0: // Cube
				stream = BlockPos.betweenClosedStream(pos.offset(-charge, -charge, -charge), pos.offset(charge, charge, charge));
				break;
			case 1: // Panel
				if (sideHit == Direction.UP || sideHit == Direction.DOWN) {
					stream = BlockPos.betweenClosedStream(pos.offset(-charge, 0, -charge), pos.offset(charge, 0, charge));
				} else if (sideHit == Direction.EAST || sideHit == Direction.WEST) {
					stream = BlockPos.betweenClosedStream(pos.offset(0, -charge, -charge), pos.offset(0, charge, charge));
				} else if (sideHit == Direction.SOUTH || sideHit == Direction.NORTH) {
					stream = BlockPos.betweenClosedStream(pos.offset(-charge, -charge, 0), pos.offset(charge, charge, 0));
				}
				break;
			case 2: // Line
				Direction playerFacing = player.getDirection();
				if (playerFacing.getAxis() == Direction.Axis.Z) {
					stream = BlockPos.betweenClosedStream(pos.offset(0, 0, -charge), pos.offset(0, 0, charge));
				} else if (playerFacing.getAxis() == Direction.Axis.X) {
					stream = BlockPos.betweenClosedStream(pos.offset(-charge, 0, 0), pos.offset(charge, 0, 0));
				}
				break;
		}
		if (stream == null) {
			return Collections.emptyMap();
		}
		Map<BlockState, BlockState> conversions = new Object2ObjectArrayMap<>();
		conversions.put(targeted, result);
		Map<BlockPos, BlockState> changes = new HashMap<>();
		Block targetBlock = targeted.getBlock();
		stream.forEach(currentPos -> {
			BlockState state = world.getBlockState(currentPos);
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
		});
		return changes;
	}

	private static class ContainerProvider implements INamedContainerProvider {

		private final ItemStack stack;

		private ContainerProvider(ItemStack stack) {
			this.stack = stack;
		}

		@Nonnull
		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
			return new PhilosStoneContainer(windowId, playerInventory, IWorldPosCallable.create(playerIn.getCommandSenderWorld(), playerIn.blockPosition()));
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return stack.getHoverName();
		}
	}
}