package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IPedestalItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.Random;

public class DMPedestalTile extends TileEntity implements ITickable
{
	private static final int RANGE = 4;

	private BlockPos lastPos;
	private AxisAlignedBB effectBounds;

	private boolean isActive = false;
	private ItemStackHandler inventory = new ItemStackHandler(1)
	{
		@Override
		public void onContentsChanged(int slot)
		{
			DMPedestalTile.this.markDirty();
		}
	};
	private int particleCooldown = 10;
	private int activityCooldown = 0;
	public boolean previousRedstoneState = false;
	public double centeredX, centeredY, centeredZ;

	@Override
	public void update()
	{
		centeredX = pos.getX() + 0.5;
		centeredY = pos.getY() + 0.5;
		centeredZ = pos.getZ() + 0.5;

		if (getActive())
		{
			ItemStack stack = inventory.getStackInSlot(0);
			if (!stack.isEmpty())
			{
				Item item = stack.getItem();
				if (item instanceof IPedestalItem)
				{
					((IPedestalItem) item).updateInPedestal(world, getPos());
				}
				if (world.isRemote)
				{
					if (particleCooldown <= 0)
					{
						spawnParticles();
						particleCooldown = 10;
					}
					else
					{
						particleCooldown--;
					}
				}
			}
			else
			{
				setActive(false);
			}
		}
	}

	private void spawnParticles()
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.2, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.5, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.2, y + 0.3, z + 0.8, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.2, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.5, y + 0.3, z + 0.8, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.2, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.5, 0, 0, 0);
		world.spawnParticle(EnumParticleTypes.FLAME, x + 0.8, y + 0.3, z + 0.8, 0, 0, 0);

		Random rand = world.rand;
		for (int i = 0; i < 3; ++i)
		{
			int j = rand.nextInt(2) * 2 - 1;
			int k = rand.nextInt(2) * 2 - 1;
			double d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
			double d1 = (double) ((float) pos.getY() + rand.nextFloat());
			double d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
			double d3 = (double) (rand.nextFloat() * (float) j);
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.125D;
			double d5 = (double) (rand.nextFloat() * (float) k);
			world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		}
	}

	public int getActivityCooldown()
	{
		return activityCooldown;
	}

	public void setActivityCooldown(int i)
	{
		activityCooldown = i;
	}

	public void decrementActivityCooldown()
	{
		activityCooldown--;
	}

	/**
	 * @return Inclusive bounding box of all positions this pedestal should apply effects in
	 */
	public AxisAlignedBB getEffectBounds()
	{
		BlockPos pos = getPos();
		if (pos != lastPos)
		{
			effectBounds = new AxisAlignedBB(pos.add(-RANGE, -RANGE, -RANGE), pos.add(RANGE, RANGE, RANGE));
			lastPos = pos;
		}
		return effectBounds;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		inventory = new ItemStackHandler(1);
		inventory.deserializeNBT(tag);
		setActive(tag.getBoolean("isActive"));
		activityCooldown = tag.getInteger("activityCooldown");
		previousRedstoneState = tag.getBoolean("powered");
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);
		tag.merge(inventory.serializeNBT());
		tag.setBoolean("isActive", getActive());
		tag.setInteger("activityCooldown", activityCooldown);
		tag.setBoolean("powered", previousRedstoneState);
		return tag;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState state, @Nonnull IBlockState newState)
	{
		return state.getBlock() != newState.getBlock();
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, -1, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet)
	{
		readFromNBT(packet.getNbtCompound());
	}

	public boolean getActive()
	{
		return isActive;
	}

	public void setActive(boolean newState)
	{
		if (newState != this.getActive() && world != null)
		{
			if (newState)
			{
				world.playSound(null, pos, PESounds.CHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < world.rand.nextInt(35) + 10; ++i)
				{
					this.getWorld().spawnParticle(EnumParticleTypes.SPELL_WITCH, centeredX + world.rand.nextGaussian() * 0.12999999523162842D,
							getPos().getY() + 1 + world.rand.nextGaussian() * 0.12999999523162842D,
							centeredZ + world.rand.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			}
			else
			{
				world.playSound(null, pos, PESounds.UNCHARGE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				for (int i = 0; i < world.rand.nextInt(35) + 10; ++i)
				{
					this.getWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, centeredX + world.rand.nextGaussian() * 0.12999999523162842D,
							getPos().getY() + 1 + world.rand.nextGaussian() * 0.12999999523162842D,
							centeredZ + world.rand.nextGaussian() * 0.12999999523162842D,
							0.0D, 0.0D, 0.0D);
				}
			}
		}
		this.isActive = newState;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing side)
	{
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
		}
		return super.getCapability(cap, side);
	}

	public IItemHandlerModifiable getInventory()
	{
		return inventory;
	}

}
