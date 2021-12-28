package moze_intel.projecte.gameObjs.block_entities;

import java.util.Random;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.capability.managing.BasicCapabilityResolver;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PESoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class DMPedestalTile extends CapabilityTileEMC {

	private static final int RANGE = 4;
	private final ItemStackHandler inventory = new StackHandler(1) {
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
	public double centeredX, centeredY, centeredZ;

	public DMPedestalTile(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.DARK_MATTER_PEDESTAL, pos, state);
		itemHandlerResolver = BasicCapabilityResolver.getBasicItemHandlerResolver(inventory);
	}

	@Override
	protected void tick() {
		centeredX = worldPosition.getX() + 0.5;
		centeredY = worldPosition.getY() + 0.5;
		centeredZ = worldPosition.getZ() + 0.5;
		if (level != null && getActive()) {
			ItemStack stack = inventory.getStackInSlot(0);
			if (!stack.isEmpty()) {
				stack.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> pedestalItem.updateInPedestal(level, worldPosition));
				if (particleCooldown <= 0) {
					spawnParticleTypes();
					particleCooldown = 10;
				} else {
					particleCooldown--;
				}
			} else {
				setActive(false);
			}
		}
		super.tick();
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
		Random rand = level.random;
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

	public int getActivityCooldown() {
		return activityCooldown;
	}

	public void setActivityCooldown(int i) {
		if (activityCooldown != i) {
			activityCooldown = i;
			markDirty(false);
		}
	}

	public void decrementActivityCooldown() {
		activityCooldown--;
		markDirty(false);
	}

	/**
	 * @return Inclusive bounding box of all positions this pedestal should apply effects in
	 */
	public AABB getEffectBounds() {
		return new AABB(worldPosition.offset(-RANGE, -RANGE, -RANGE), worldPosition.offset(RANGE, RANGE, RANGE));
	}

	@Override
	public void load(@Nonnull CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt);
		setActive(nbt.getBoolean("isActive"));
		activityCooldown = nbt.getInt("activityCooldown");
		previousRedstoneState = nbt.getBoolean("powered");
	}

	@Override
	protected void saveAdditional(@Nonnull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(inventory.serializeNBT());
		tag.putBoolean("isActive", getActive());
		tag.putInt("activityCooldown", activityCooldown);
		tag.putBoolean("powered", previousRedstoneState);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		//TODO - 1.18: Test this
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public boolean getActive() {
		return isActive;
	}

	public void setActive(boolean newState) {
		if (newState != this.getActive() && level != null) {
			if (newState) {
				level.playSound(null, worldPosition, PESoundEvents.CHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < level.random.nextInt(35) + 10; ++i) {
					level.addParticle(ParticleTypes.WITCH, centeredX + level.random.nextGaussian() * 0.12999999523162842D,
							worldPosition.getY() + 1 + level.random.nextGaussian() * 0.12999999523162842D,
							centeredZ + level.random.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			} else {
				level.playSound(null, worldPosition, PESoundEvents.UNCHARGE.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < level.random.nextInt(35) + 10; ++i) {
					level.addParticle(ParticleTypes.SMOKE, centeredX + level.random.nextGaussian() * 0.12999999523162842D,
							worldPosition.getY() + 1 + level.random.nextGaussian() * 0.12999999523162842D,
							centeredZ + level.random.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			}
		}
		this.isActive = newState;
		markDirty(false);
	}

	public IItemHandlerModifiable getInventory() {
		return inventory;
	}
}