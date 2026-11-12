package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import moze_intel.projecte.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DMPedestalBlockEntity extends EmcBlockEntity implements IDMPedestal {

	public static final ICapabilityProvider<DMPedestalBlockEntity, @Nullable Direction, ResourceHandler<ItemResource>> INVENTORY_PROVIDER = (pedestal, side) -> pedestal.inventory;
	private static final int RANGE = 4;

	private final StackHandler inventory = new StackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (level != null && !level.isClientSide()) {
				//If an item got added via the item handler, then rerender the block
				BlockState state = getBlockState();
				level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_IMMEDIATE);
			}
		}
	};
	private boolean isActive = false;
	private int particleCooldown = 10;
	private int activityCooldown = 0;
	public boolean previousRedstoneState = false;

	public DMPedestalBlockEntity(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.DARK_MATTER_PEDESTAL, pos, state, 1_000);
	}

	public static void tickClient(Level level, BlockPos pos, BlockState state, DMPedestalBlockEntity pedestal) {
		if (pedestal.getActive()) {
			ItemStack stack = pedestal.inventory.getStackInSlot(0);
			IPedestalItem pedestalItem = stack.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
			if (pedestalItem == null) {
				pedestal.setActive(level, pos, false);
			} else {
				pedestalItem.updateInPedestal(stack, level, pos, pedestal);
				if (pedestal.particleCooldown <= 0) {
					spawnParticleTypes(level, pos);
					pedestal.particleCooldown = Constants.TICKS_PER_HALF_SECOND;
				} else {
					pedestal.particleCooldown--;
				}
			}
		}
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, DMPedestalBlockEntity pedestal) {
		if (pedestal.getActive()) {
			ItemStack stack = pedestal.inventory.getStackInSlot(0);
			IPedestalItem pedestalItem = stack.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
			if (pedestalItem == null) {
				pedestal.setActive(level, pos, false);
			} else if (pedestalItem.updateInPedestal(stack, level, pos, pedestal)) {
				pedestal.inventory.onContentsChanged(0);
			}
		}
		pedestal.updateComparators(level, pos);
	}

	private static void spawnParticleTypes(@NotNull Level level, @NotNull BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		level.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.2, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.5, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.8, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.2, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.8, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.2, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.5, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.8, 0, 0, 0);
		RandomSource rand = level.getRandom();
		for (int i = 0; i < 3; ++i) {
			int xDirection = rand.nextBoolean() ? -1 : 1;
			int zDirection = rand.nextBoolean() ? -1 : 1;
			level.addParticle(ParticleTypes.PORTAL,
					x + 0.5D + xDirection * 0.25D, y + rand.nextFloat(), z + 0.5D + zDirection * 0.25D,
					xDirection * rand.nextFloat(), (rand.nextFloat() - 0.5D) * 0.125D, zDirection * rand.nextFloat()
			);
		}
	}

	@Override
	public int getActivityCooldown() {
		return activityCooldown;
	}

	@Override
	public void setActivityCooldown(@NotNull Level level, @NotNull BlockPos pos, int cooldown) {
		if (activityCooldown != cooldown) {
			activityCooldown = cooldown;
			markDirty(level, pos, false);
		}
	}

	@Override
	public void decrementActivityCooldown(@NotNull Level level, @NotNull BlockPos pos) {
		activityCooldown--;
		markDirty(level, pos, false);
	}

	@Override
	public AABB getEffectBounds() {
		return new AABB(worldPosition).inflate(RANGE);
	}

	@Override
	public void loadAdditional(@NotNull ValueInput input) {
		super.loadAdditional(input);
		inventory.deserialize(input);
		isActive = input.getBooleanOr("active", false);
		activityCooldown = input.getIntOr("activity_cooldown", 0);
		previousRedstoneState = input.getBooleanOr("powered", false);
	}

	@Override
	protected void saveAdditional(@NotNull ValueOutput output) {
		super.saveAdditional(output);
		inventory.serialize(output);
		output.putBoolean("active", getActive());
		output.putInt("activity_cooldown", activityCooldown);
		output.putBoolean("powered", previousRedstoneState);
	}

	public boolean getActive() {
		return isActive;
	}

	public void setActive(@NotNull Level level, @NotNull BlockPos pos, boolean newState) {
		if (newState != this.getActive()) {
			if (newState) {
				level.playSound(null, pos, PESoundEvents.CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < level.getRandom().nextInt(35) + 10; ++i) {
					level.addParticle(ParticleTypes.WITCH,
							pos.getX() + 0.5 + level.getRandom().nextGaussian() * 0.12999999523162842D,
							pos.getY() + 1 + level.getRandom().nextGaussian() * 0.12999999523162842D,
							pos.getZ() + 0.5 + level.getRandom().nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			} else {
				level.playSound(null, pos, PESoundEvents.UNCHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < level.getRandom().nextInt(35) + 10; ++i) {
					level.addParticle(ParticleTypes.SMOKE,
							pos.getX() + 0.5 + level.getRandom().nextGaussian() * 0.12999999523162842D,
							pos.getY() + 1 + level.getRandom().nextGaussian() * 0.12999999523162842D,
							pos.getZ() + 0.5 + level.getRandom().nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			}
		}
		this.isActive = newState;
		markDirty(level, pos, true);
	}

	public PEItemStacksHandler getInventory() {
		return inventory;
	}
}
