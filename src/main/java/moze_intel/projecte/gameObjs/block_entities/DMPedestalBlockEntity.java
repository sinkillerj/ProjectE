package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.block_entity.IDMPedestal;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DMPedestalBlockEntity extends EmcBlockEntity implements IDMPedestal {

	public static final ICapabilityProvider<DMPedestalBlockEntity, @Nullable Direction, IItemHandler> INVENTORY_PROVIDER = (pedestal, side) -> pedestal.inventory;
	private static final int RANGE = 4;

	private final StackHandler inventory = new StackHandler(1) {
		@Override
		public void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (level != null && !level.isClientSide) {
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
			if (stack.isEmpty()) {
				pedestal.setActive(false);
			} else {
				IPedestalItem pedestalItem = stack.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
				if (pedestalItem != null) {
					pedestalItem.updateInPedestal(stack, level, pos, pedestal);
					if (pedestal.particleCooldown <= 0) {
						pedestal.spawnParticleTypes();
						pedestal.particleCooldown = 10;
					} else {
						pedestal.particleCooldown--;
					}
				} else {
					pedestal.setActive(false);
				}
			}
		}
	}

	public static void tickServer(Level level, BlockPos pos, BlockState state, DMPedestalBlockEntity pedestal) {
		if (pedestal.getActive()) {
			ItemStack stack = pedestal.inventory.getStackInSlot(0);
			if (stack.isEmpty()) {
				pedestal.setActive(false);
			} else {
				IPedestalItem pedestalItem = stack.getCapability(PECapabilities.PEDESTAL_ITEM_CAPABILITY);
				if (pedestalItem != null) {
					if (pedestalItem.updateInPedestal(stack, level, pos, pedestal)) {
						pedestal.inventory.onContentsChanged(0);
					}
				} else {
					pedestal.setActive(false);
				}
			}
		}
		pedestal.updateComparators();
	}

	private void spawnParticleTypes() {
		int x = worldPosition.getX();
		int y = worldPosition.getY();
		int z = worldPosition.getZ();
		level.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.2, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.5, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.8, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.2, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.8, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.2, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.5, 0, 0, 0);
		level.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.8, 0, 0, 0);
		RandomSource rand = level.random;
		for (int i = 0; i < 3; ++i) {
			int j = rand.nextInt(2) * 2 - 1;
			int k = rand.nextInt(2) * 2 - 1;
			double d0 = (double) worldPosition.getX() + 0.5D + 0.25D * (double) j;
			double d1 = (float) worldPosition.getY() + rand.nextFloat();
			double d2 = (double) worldPosition.getZ() + 0.5D + 0.25D * (double) k;
			double d3 = rand.nextFloat() * (float) j;
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
			double d5 = rand.nextFloat() * (float) k;
			level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}
	}

	@Override
	public int getActivityCooldown() {
		return activityCooldown;
	}

	@Override
	public void setActivityCooldown(int cooldown) {
		if (activityCooldown != cooldown) {
			activityCooldown = cooldown;
			markDirty(false);
		}
	}

	@Override
	public void decrementActivityCooldown() {
		activityCooldown--;
		markDirty(false);
	}

	@Override
	public AABB getEffectBounds() {
		return new AABB(worldPosition).inflate(RANGE);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt);
		setActive(nbt.getBoolean("isActive"));
		activityCooldown = nbt.getInt("activityCooldown");
		previousRedstoneState = nbt.getBoolean("powered");
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(inventory.serializeNBT());
		tag.putBoolean("isActive", getActive());
		tag.putInt("activityCooldown", activityCooldown);
		tag.putBoolean("powered", previousRedstoneState);
	}

	public boolean getActive() {
		return isActive;
	}

	public void setActive(boolean newState) {
		if (newState != this.getActive() && level != null) {
			if (newState) {
				level.playSound(null, worldPosition, PESoundEvents.CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < level.random.nextInt(35) + 10; ++i) {
					level.addParticle(ParticleTypes.WITCH,
							worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.12999999523162842D,
							worldPosition.getY() + 1 + level.random.nextGaussian() * 0.12999999523162842D,
							worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			} else {
				level.playSound(null, worldPosition, PESoundEvents.UNCHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < level.random.nextInt(35) + 10; ++i) {
					level.addParticle(ParticleTypes.SMOKE,
							worldPosition.getX() + 0.5 + level.random.nextGaussian() * 0.12999999523162842D,
							worldPosition.getY() + 1 + level.random.nextGaussian() * 0.12999999523162842D,
							worldPosition.getZ() + 0.5 + level.random.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			}
		}
		this.isActive = newState;
		setChanged();
	}

	public IItemHandlerModifiable getInventory() {
		return inventory;
	}
}