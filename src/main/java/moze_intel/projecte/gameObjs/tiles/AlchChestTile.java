package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.api.item.IAlchChestItem;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchChestContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlchChestTile extends TileEmc implements IInteractionObject
{
	private final ItemStackHandler inventory = new StackHandler(104);
	private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);
	public float lidAngle;
	public float prevLidAngle;
	public int numPlayersUsing;
	private int ticksSinceSync;

	public AlchChestTile()
	{
		super(ObjHandler.ALCH_CHEST_TILE);
	}

	@Override
	public void remove()
	{
		super.remove();
		inventoryCap.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, EnumFacing side)
	{
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			return inventoryCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void read(NBTTagCompound nbt)
	{
		super.read(nbt);
		inventory.deserializeNBT(nbt);
	}
	
	@Nonnull
	@Override
	public NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt = super.write(nbt);
		nbt.merge(inventory.serializeNBT());
		return nbt;
	}
	
	@Override
	public void tick()
	{
		if (++ticksSinceSync % 20 * 4 == 0)
		{
			world.addBlockEvent(getPos(), ObjHandler.alchChest, 1, numPlayersUsing);
		}

		prevLidAngle = lidAngle;
		float angleIncrement = 0.1F;

		if (numPlayersUsing > 0 && lidAngle == 0.0F)
		{
			world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F)
		{
			float var8 = lidAngle;

			if (numPlayersUsing > 0)
			{
				lidAngle += angleIncrement;
			}
			else
			{
				lidAngle -= angleIncrement;
			}

			if (lidAngle > 1.0F)
			{
				lidAngle = 1.0F;
			}

			if (lidAngle < 0.5F && var8 >= 0.5F)
			{
				world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F)
			{
				lidAngle = 0.0F;
			}
		}

		for (int i = 0; i < inventory.getSlots(); i++)
		{
			ItemStack stack = inventory.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof IAlchChestItem)
			{
				((IAlchChestItem) stack.getItem()).updateInAlchChest(world, pos, stack);
			}
		}
	}
	
	@Override
	public boolean receiveClientEvent(int number, int arg)
	{
		if (number == 1)
		{
			numPlayersUsing = arg;
			return true;
		}
		else return super.receiveClientEvent(number, arg);
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new AlchChestContainer(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return getType().getRegistryName().toString();
	}

	@Override
	public ITextComponent getName() {
		return new TextComponentString(getGuiID());
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Nullable
	@Override
	public ITextComponent getCustomName() {
		return null;
	}
}
