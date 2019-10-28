package moze_intel.projecte.gameObjs.tiles;

import java.util.Random;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class DMPedestalTile extends TileEmc {

	private static final int RANGE = 4;
	private boolean isActive = false;
	private ItemStackHandler inventory = new StackHandler(1);
	private final LazyOptional<IItemHandler> automationInv = LazyOptional.of(() -> inventory);
	private int particleCooldown = 10;
	private int activityCooldown = 0;
	public boolean previousRedstoneState = false;
	public double centeredX, centeredY, centeredZ;

	public DMPedestalTile() {
		super(ObjHandler.DM_PEDESTAL_TILE);
	}

	@Override
	public void remove() {
		super.remove();
		automationInv.invalidate();
	}

	@Override
	public void tick() {
		centeredX = pos.getX() + 0.5;
		centeredY = pos.getY() + 0.5;
		centeredZ = pos.getZ() + 0.5;

		if (getActive()) {
			ItemStack stack = inventory.getStackInSlot(0);
			if (!stack.isEmpty()) {
				stack.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> pedestalItem.updateInPedestal(world, getPos()));
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
	}

	private void spawnParticleTypes() {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		world.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.2, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.5, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.8, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.2, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.8, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.2, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.5, 0, 0, 0);
		world.addParticle(ParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.8, 0, 0, 0);

		Random rand = world.rand;
		for (int i = 0; i < 3; ++i) {
			int j = rand.nextInt(2) * 2 - 1;
			int k = rand.nextInt(2) * 2 - 1;
			double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
			double d1 = (float) pos.getY() + rand.nextFloat();
			double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
			double d3 = rand.nextFloat() * (float) j;
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
			double d5 = rand.nextFloat() * (float) k;
			world.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}
	}

	public int getActivityCooldown() {
		return activityCooldown;
	}

	public void setActivityCooldown(int i) {
		activityCooldown = i;
	}

	public void decrementActivityCooldown() {
		activityCooldown--;
	}

	/**
	 * @return Inclusive bounding box of all positions this pedestal should apply effects in
	 */
	public AxisAlignedBB getEffectBounds() {
		return new AxisAlignedBB(getPos().add(-RANGE, -RANGE, -RANGE), getPos().add(RANGE, RANGE, RANGE));
	}

	@Override
	public void read(@Nonnull CompoundNBT tag) {
		super.read(tag);
		inventory = new ItemStackHandler(1);
		inventory.deserializeNBT(tag);
		setActive(tag.getBoolean("isActive"));
		activityCooldown = tag.getInt("activityCooldown");
		previousRedstoneState = tag.getBoolean("powered");
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tag) {
		tag = super.write(tag);
		tag.merge(inventory.serializeNBT());
		tag.putBoolean("isActive", getActive());
		tag.putInt("activityCooldown", activityCooldown);
		tag.putBoolean("powered", previousRedstoneState);
		return tag;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
		read(packet.getNbtCompound());
	}

	public boolean getActive() {
		return isActive;
	}

	public void setActive(boolean newState) {
		if (newState != this.getActive() && world != null) {
			if (newState) {
				world.playSound(null, pos, PESounds.CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < world.rand.nextInt(35) + 10; ++i) {
					this.getWorld().addParticle(ParticleTypes.WITCH, centeredX + world.rand.nextGaussian() * 0.12999999523162842D,
							getPos().getY() + 1 + world.rand.nextGaussian() * 0.12999999523162842D,
							centeredZ + world.rand.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			} else {
				world.playSound(null, pos, PESounds.UNCHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < world.rand.nextInt(35) + 10; ++i) {
					this.getWorld().addParticle(ParticleTypes.SMOKE, centeredX + world.rand.nextGaussian() * 0.12999999523162842D,
							getPos().getY() + 1 + world.rand.nextGaussian() * 0.12999999523162842D,
							centeredZ + world.rand.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			}
		}
		this.isActive = newState;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return automationInv.cast();
		}
		return super.getCapability(cap, side);
	}

	public IItemHandlerModifiable getInventory() {
		return inventory;
	}
}