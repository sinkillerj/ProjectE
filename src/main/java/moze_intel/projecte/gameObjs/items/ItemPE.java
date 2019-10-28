package moze_intel.projecte.gameObjs.items;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemPE extends Item
{
	public static final String TAG_ACTIVE = "Active";
	public static final String TAG_MODE = "Mode";
	protected static final ResourceLocation ACTIVE_NAME = new ResourceLocation(PECore.MODID, "active");
	protected static final IItemPropertyGetter ACTIVE_GETTER = (stack, world, entity) -> stack.hasTag() && stack.getTag().getBoolean(TAG_ACTIVE) ? 1F : 0F;
	protected static final IItemPropertyGetter MODE_GETTER = (stack, world, entity) -> stack.hasTag() ? stack.getTag().getInt(TAG_MODE) : 0F;

	private final List<ItemCapability<?>> supportedCapabilities = new ArrayList<>();

	public ItemPE(Properties props)
	{
		super(props);
	}

	protected <TYPE> void addItemCapability(ItemCapability<TYPE> capability) {
		supportedCapabilities.add(capability);
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		if (oldStack.getItem() != newStack.getItem())
			return true;

		boolean diffActive = oldStack.hasTag() && newStack.hasTag()
				&& oldStack.getTag().contains(TAG_ACTIVE) && newStack.getTag().contains(TAG_ACTIVE)
				&& !oldStack.getTag().get(TAG_ACTIVE).equals(newStack.getTag().get(TAG_ACTIVE));

		boolean diffMode = oldStack.hasTag() && newStack.hasTag()
				&& oldStack.getTag().contains(TAG_MODE) && newStack.getTag().contains(TAG_MODE)
				&& !oldStack.getTag().get(TAG_MODE).equals(newStack.getTag().get(TAG_MODE));

		return diffActive || diffMode;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new ItemCapabilityWrapper(stack, supportedCapabilities);
	}

	public static long getEmc(ItemStack stack)
	{
		return stack.getOrCreateTag().getLong("StoredEMC");
	}
	
	public static void setEmc(ItemStack stack, long amount)
	{
		stack.getOrCreateTag().putLong("StoredEMC", amount);
	}
	
	public static void addEmcToStack(ItemStack stack, long amount)
	{
		setEmc(stack, getEmc(stack) + amount);
	}
	
	public static void removeEmc(ItemStack stack, long amount)
	{
		long result = getEmc(stack) - amount;
		
		if (result < 0)
		{
			result = 0;
		}
		
		setEmc(stack, result);
	}
	
	public static boolean consumeFuel(PlayerEntity player, ItemStack stack, long amount, boolean shouldRemove)
	{
		if (amount <= 0)
		{
			return true;
		}

		long current = getEmc(stack);
		
		if (current < amount)
		{
			long consume = EMCHelper.consumePlayerFuel(player, amount - current);
			
			if (consume == -1)
			{
				return false;
			}
			
			addEmcToStack(stack, consume);
		}
		
		if (shouldRemove)
		{
			removeEmc(stack, amount);
		}
		
		return true;
	}
}